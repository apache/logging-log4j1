/*
 * Copyright 1999,2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import junit.framework.TestCase;

import org.apache.log4j.spi.LoggingEvent;

import java.util.Enumeration;
import java.util.Vector;


/**
 *  Tests for AsyncAppender.
 *
 * @author Curt Arnold
 *
 */
public final class AsyncAppenderTestCase extends TestCase {
  /**
   * root logger.
   */
  private final Logger root = Logger.getRootLogger();

  /**
   * appender under test.
   */
  private AsyncAppender asyncAppender;

  /**
   * Create new instance of test.
   * @param name test name.
   */
  public AsyncAppenderTestCase(final String name) {
    super(name);
  }

  /**
   * Create a vector appender with a 10 ms delay.
   * @return new VectorAppender.
   */
  private static VectorAppender createDelayedAppender() {
    VectorAppender vectorAppender = new VectorAppender();
    vectorAppender.setDelay(10);

    return vectorAppender;
  }

  /**
   * Create new appender and attach to root logger.
   * @param wrappedAppender appender wrapped by async logger.
   * @param bufferSize buffer size.
   * @return new AsyncAppender.
   */
  private static AsyncAppender createAsyncAppender(
    final Appender wrappedAppender, final int bufferSize) {
    AsyncAppender async = new AsyncAppender();
    async.addAppender(wrappedAppender);
    async.setBufferSize(bufferSize);
    async.activateOptions();
    Logger.getRootLogger().addAppender(async);

    return async;
  }

  /**
   * Performs post test cleanup.
   */
  public void tearDown() {
    if (asyncAppender != null) {
      asyncAppender.close();
    }

    LogManager.shutdown();
  }

  /**
   * Tests writing to an AsyncAppender after calling close.
   */
  public void testClose() {
    VectorAppender vectorAppender = createDelayedAppender();
    asyncAppender =
      createAsyncAppender(vectorAppender, AsyncAppender.DEFAULT_BUFFER_SIZE);
    asyncAppender.setName("async-testClose");

    root.debug("m1");
    asyncAppender.close();
    root.debug("m2");

    Vector v = vectorAppender.getVector();
    assertEquals(1, v.size());
    assertTrue(vectorAppender.isClosed());
  }

  /**
   * Tests that bad appenders do not silently fail forever
   * on the dispatching thread.
   *
   * @throws InterruptedException if test is interrupted while sleeping.
   */
  public void testBadAppender() throws InterruptedException {
    Appender nullPointerAppender = new NullPointerAppender();
    asyncAppender =
      createAsyncAppender(
        nullPointerAppender, AsyncAppender.DEFAULT_BUFFER_SIZE);

    //
    //  NullPointerException should kill dispatching thread
    //     before sleep returns.
    root.info("Message");
    Thread.sleep(100);

    try {
      //
      //   subsequent call should be synchronous
      //     and result in a NullPointerException on this thread.
      root.info("Message");
      fail("Should have thrown exception");
    } catch (NullPointerException ex) {
      assertNotNull(ex);
    }
  }

  /**
   * Test logging to AsyncAppender from many threads.
   * @throws InterruptedException if test is interrupted while sleeping.
   */
  public void testManyLoggingThreads() throws InterruptedException {
    BlockableVectorAppender blockableAppender = new BlockableVectorAppender();
    asyncAppender = createAsyncAppender(blockableAppender, 5);

    //
    //   create threads
    //
    final int threadCount = 10;
    Thread[] threads = new Thread[threadCount];
    final int repetitions = 20;
    Greeter greeter = new Greeter(root, repetitions);

    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(greeter);
    }

    //
    //   block underlying appender
    synchronized (blockableAppender.getMonitor()) {
      //
      //   start threads holding for queue to open up
      for (int i = 0; i < threads.length; i++) {
        threads[i].start();
      }
    }

    //   dispatcher now running free
    //
    //   wait until all threads complete
    for (int i = 0; i < threads.length; i++) {
      threads[i].join(2000);
      assertFalse(threads[i].isAlive());
    }

    asyncAppender.close();
    assertEquals(
      threadCount * repetitions, blockableAppender.getVector().size());
  }

  /**
   *  Tests interruption handling on logging threads.

   * @throws InterruptedException if test is interrupted while sleeping.
   */
  public void testInterruptWhileLogging() throws InterruptedException {
    BlockableVectorAppender blockableAppender = new BlockableVectorAppender();
    asyncAppender = createAsyncAppender(blockableAppender, 5);

    Thread greeter = new Thread(new Greeter(root, 100));

    synchronized (blockableAppender.getMonitor()) {
      //  Start greeter
      greeter.start();

      //   Give it enough time to fill buffer
      Thread.sleep(100);

      //
      //   Interrupt should stop greeter after next logging event
      greeter.interrupt();
    }

    greeter.join();
    asyncAppender.close();

    Vector events = blockableAppender.getVector();

    //
    //   0-5 popped off of buffer by dispatcher before being blocked
    //   5 in buffer before it filled up
    //   1 before Thread.sleep in greeter
    assertTrue(events.size() <= 11);
  }

  /**
   *  Tests interruption handling in AsyncAppender.close.
   *
   * @throws InterruptedException if test is interrupted while sleeping.
   *
   */
  public void testInterruptWhileClosing() throws InterruptedException {
    BlockableVectorAppender blockableAppender = new BlockableVectorAppender();
    asyncAppender = createAsyncAppender(blockableAppender, 5);

    Thread greeter = new Thread(new Greeter(root, 100));
    Thread closer = new Thread(new Closer(asyncAppender));

    synchronized (blockableAppender.getMonitor()) {
      greeter.start();
      Thread.sleep(100);
      closer.start();
      Thread.sleep(100);
      closer.interrupt();
    }

    greeter.join();
    closer.join();
  }

  /**
   *  Tests killing the dispatch thread.
   *
   * @throws InterruptedException if test is interrupted while sleeping.
   *
   */
  public void testInterruptDispatcher() throws InterruptedException {
    BlockableVectorAppender blockableAppender = new BlockableVectorAppender();
    asyncAppender = createAsyncAppender(blockableAppender, 5);
    assertTrue(asyncAppender.getAllAppenders().hasMoreElements());
    root.info("Hello, World");

    //
    //  sleep long enough for that to get dispatched
    //
    Thread.sleep(50);

    Thread dispatcher = blockableAppender.getDispatcher();
    assertNotNull(dispatcher);
    dispatcher.interrupt();
    Thread.sleep(50);
    root.info("Hello, World");

    //
    //  interrupting the dispatch thread should
    //     degrade to synchronous dispatching of logging requests
    Enumeration iter = asyncAppender.getAllAppenders();
    assertTrue(iter.hasMoreElements());
    assertSame(blockableAppender, iter.nextElement());
    assertFalse(iter.hasMoreElements());
    assertEquals(2, blockableAppender.getVector().size());
  }

  /**
   * Tests getBufferSize.
   */
  public void testGetBufferSize() {
    asyncAppender = new AsyncAppender();
    assertEquals(
      AsyncAppender.DEFAULT_BUFFER_SIZE, asyncAppender.getBufferSize());
  }

  /**
   * Tests setBufferSize(0).
   */
  public void testSetBufferSizeZero() {
    VectorAppender vectorAppender = createDelayedAppender();
    asyncAppender = createAsyncAppender(vectorAppender, 0);
    assertEquals(1, asyncAppender.getBufferSize());

    //
    //   any logging request will deadlock.
    root.debug("m1");
    root.debug("m2");
    asyncAppender.close();

    Vector v = vectorAppender.getVector();
    assertEquals(2, v.size());
  }

  /**
   * Tests setBufferSize(-10).
   */
  public void testSetBufferSizeNegative() {
    asyncAppender = new AsyncAppender();

    try {
      asyncAppender.setBufferSize(-10);
      fail("Should have thrown NegativeArraySizeException");
    } catch (NegativeArraySizeException ex) {
      assertNotNull(ex);
    }
  }

  /**
   * Tests getAllAppenders.
   */
  public void testGetAllAppenders() {
    VectorAppender vectorAppender = createDelayedAppender();
    asyncAppender = createAsyncAppender(vectorAppender, 5);

    Enumeration iter = asyncAppender.getAllAppenders();
    assertTrue(iter.hasMoreElements());
    assertSame(vectorAppender, iter.nextElement());
    assertFalse(iter.hasMoreElements());
  }

  /**
   * Tests getAppender.
   */
  public void testGetAppender() {
    VectorAppender vectorAppender = createDelayedAppender();
    vectorAppender.setName("test");
    asyncAppender = createAsyncAppender(vectorAppender, 5);

    Appender appender = asyncAppender.getAppender("test");
    assertSame(vectorAppender, appender);
  }

  /**
   * Test getLocationInfo.
   */
  public void testGetLocationInfo() {
    asyncAppender = new AsyncAppender();
    assertFalse(asyncAppender.getLocationInfo());
  }

  /**
   * Tests isAttached.
   */
  public void testIsAttached() {
    VectorAppender vectorAppender = createDelayedAppender();
    asyncAppender = createAsyncAppender(vectorAppender, 5);
    assertTrue(asyncAppender.isAttached(vectorAppender));
    assertFalse(asyncAppender.isAttached(asyncAppender));
    assertFalse(asyncAppender.isAttached(new BlockableVectorAppender()));
  }

  /**
   * Tests requiresLayout.
   *
   * @deprecated feature under test is deprecated.
   */
  public void testRequiresLayout() {
    asyncAppender = new AsyncAppender();
    assertFalse(asyncAppender.requiresLayout());
  }

  /**
   * Tests removeAllAppenders.
   */
  public void testRemoveAllAppenders() {
    VectorAppender vectorAppender = createDelayedAppender();
    asyncAppender = new AsyncAppender();
    asyncAppender.addAppender(vectorAppender);
    assertTrue(asyncAppender.getAllAppenders().hasMoreElements());
    asyncAppender.removeAllAppenders();

    Enumeration iter = asyncAppender.getAllAppenders();
    assertTrue((iter == null) || !iter.hasMoreElements());
  }

  /**
   * Tests removeAppender(Appender).
   */
  public void testRemoveAppender() {
    VectorAppender vectorAppender = createDelayedAppender();
    vectorAppender.setName("test");
    asyncAppender = new AsyncAppender();
    asyncAppender.addAppender(vectorAppender);
    assertTrue(asyncAppender.getAllAppenders().hasMoreElements());

    VectorAppender appender2 = new VectorAppender();
    appender2.setName("test");
    asyncAppender.removeAppender(appender2);
    assertTrue(asyncAppender.getAllAppenders().hasMoreElements());
    asyncAppender.removeAppender(vectorAppender);
    assertFalse(asyncAppender.getAllAppenders().hasMoreElements());
  }

  /**
   * Tests removeAppender(String).
   */
  public void testRemoveAppenderByName() {
    VectorAppender vectorAppender = createDelayedAppender();
    vectorAppender.setName("test");
    asyncAppender = new AsyncAppender();
    asyncAppender.addAppender(vectorAppender);
    assertTrue(asyncAppender.getAllAppenders().hasMoreElements());
    asyncAppender.removeAppender("TEST");
    assertTrue(asyncAppender.getAllAppenders().hasMoreElements());
    asyncAppender.removeAppender("test");
    assertFalse(asyncAppender.getAllAppenders().hasMoreElements());
  }

    /**
     * Tests discarding of messages when buffer is full.
     */
    public void testDiscard() {
        BlockableVectorAppender blockableAppender = new BlockableVectorAppender();
        asyncAppender = createAsyncAppender(blockableAppender, 5);
        assertTrue(asyncAppender.getBlocking());
        asyncAppender.setBlocking(false);
        assertFalse(asyncAppender.getBlocking());
        Greeter greeter = new Greeter(root, 100);
        synchronized(blockableAppender.getMonitor()) {
            greeter.run();
            root.error("That's all folks.");
        }
        asyncAppender.close();
        Vector events = blockableAppender.getVector();
        //
        //  0-5 event pulled from buffer by dispatcher before blocking
        //  5 events in buffer
        //  1 summary event
        //
        assertTrue(events.size() <= 11);
        //
        //  last message should start with "Discarded"
        LoggingEvent event = (LoggingEvent) events.get(events.size() - 1);
        assertEquals("Discarded", event.getMessage().toString().substring(0, 9));
        assertSame(Level.ERROR, event.getLevel());
        for (int i = 0; i < events.size() - 1; i++) {
            event = (LoggingEvent) events.get(i);
            assertEquals("Hello, World", event.getMessage().toString());
        }
    }


    /**
     * Tests location processing when buffer is full and locationInfo=true.
     * See bug 41186.
     */
    public void testLocationInfoTrue() {
        BlockableVectorAppender blockableAppender = new BlockableVectorAppender();
        AsyncAppender async = new AsyncAppender();
        async.addAppender(blockableAppender);
        async.setBufferSize(5);
        async.setLocationInfo(true);
        async.setBlocking(false);
        async.activateOptions();
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.addAppender(async);
        Greeter greeter = new Greeter(rootLogger, 100);
        synchronized(blockableAppender.getMonitor()) {
            greeter.run();
            rootLogger.error("That's all folks.");
        }
        async.close();
        Vector events = blockableAppender.getVector();
        LoggingEvent initialEvent = (LoggingEvent) events.get(0);
        LoggingEvent discardEvent = (LoggingEvent) events.get(events.size() - 1);
        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%C:%L %m%n");
        layout.activateOptions();
        String initialStr = layout.format(initialEvent);
        assertEquals(AsyncAppenderTestCase.class.getName(),
                initialStr.substring(0, AsyncAppenderTestCase.class.getName().length()));
        String discardStr = layout.format(discardEvent);
        assertEquals("?:? ", discardStr.substring(0, 4));
    }


    /**
     * Tests location processing when buffer is full and locationInfo=false.
     * See bug 41186.
     */
    public void testLocationInfoFalse() {
        BlockableVectorAppender blockableAppender = new BlockableVectorAppender();
        AsyncAppender async = new AsyncAppender();
        async.addAppender(blockableAppender);
        async.setBufferSize(5);
        async.setLocationInfo(false);
        async.setBlocking(false);
        async.activateOptions();
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.addAppender(async);
        Greeter greeter = new Greeter(rootLogger, 100);
        synchronized(blockableAppender.getMonitor()) {
            greeter.run();
            rootLogger.error("That's all folks.");
        }
        async.close();
        Vector events = blockableAppender.getVector();
        LoggingEvent initialEvent = (LoggingEvent) events.get(0);
        LoggingEvent discardEvent = (LoggingEvent) events.get(events.size() - 1);
        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%C:%L %m%n");
        layout.activateOptions();
        String initialStr = layout.format(initialEvent);
        assertEquals("?:? ", initialStr.substring(0, 4));
        String discardStr = layout.format(discardEvent);
        assertEquals("?:? ", discardStr.substring(0, 4));
    }

    /**
     * Tests behavior when wrapped appender
     *    makes log request on dispatch thread.
     *
     * See bug 30106
     */
    public void testLoggingInDispatcher() throws InterruptedException {
        BlockableVectorAppender appender = new BlockableVectorAppender();
        asyncAppender =
          createAsyncAppender(appender, 2);
        //
        //   triggers several log requests on dispatch thread
        //
        root.fatal("Anybody up there...");
        Thread.sleep(100);
        asyncAppender.close();

        Vector events = appender.getVector();
        //
        //  last message should start with "Discarded"
        LoggingEvent event = (LoggingEvent) events.get(events.size() - 1);
        assertEquals("Discarded", event.getMessage().toString().substring(0, 9));
    }


  /**
   * Appender that throws a NullPointerException on calls to append.
   * Used to test behavior of AsyncAppender when dispatching to
   * misbehaving appenders.
   */
  private static final class NullPointerAppender extends AppenderSkeleton {
    /**
     * Create new instance.
     */
    public NullPointerAppender() {
      super(true);
    }

    /**
     * {@inheritDoc}
     */
    public void append(final LoggingEvent event) {
      throw new NullPointerException();
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresLayout() {
      return false;
    }
  }

  /**
   *  Logging request runnable.
   */
  private static final class Greeter implements Runnable {
    /**
     * Logger.
     */
    private final Logger logger;

    /**
     * Repetitions.
     */
    private final int repetitions;

    /**
     * Create new instance.
     * @param logger logger, may not be null.
     * @param repetitions repetitions.
     */
    public Greeter(final Logger logger, final int repetitions) {
      if (logger == null) {
        throw new IllegalArgumentException("logger");
      }

      this.logger = logger;
      this.repetitions = repetitions;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
      try {
        for (int i = 0; i < repetitions; i++) {
          logger.info("Hello, World");
          Thread.sleep(1);
        }
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Vector appender that can be explicitly blocked.
   */
  private static final class BlockableVectorAppender extends VectorAppender {
    /**
     * Monitor object used to block appender.
     */
    private final Object monitor = new Object();

    /**
     * Thread of last call to append.
     */
    private Thread dispatcher;

    /**
     * Create new instance.
     */
    public BlockableVectorAppender() {
      super();
    }

    /**
     * {@inheritDoc}
     */
    public void append(final LoggingEvent event) {
      synchronized (monitor) {
        dispatcher = Thread.currentThread();
        super.append(event);
          //
          //   if fatal, echo messages for testLoggingInDispatcher
          //
          if (event.getLevel() == Level.FATAL) {
              Logger logger = event.getLogger();
              logger.error(event.getMessage().toString());
              logger.warn(event.getMessage().toString());
              logger.info(event.getMessage().toString());
              logger.debug(event.getMessage().toString());
          }
      }
    }

    /**
     * Get monitor object.
     * @return monitor.
     */
    public Object getMonitor() {
      return monitor;
    }

    /**
     * Get thread of previous call to append.
     * @return thread, may be null.
     */
    public Thread getDispatcher() {
      synchronized (monitor) {
        return dispatcher;
      }
    }
  }

  /**
   * Closes appender.
   */
  private static final class Closer implements Runnable {
    /**
     * Appender.
     */
    private final AsyncAppender appender;

    /**
     * Create new instance.
     * @param appender appender, may not be null.
     */
    public Closer(final AsyncAppender appender) {
      if (appender == null) {
        throw new IllegalArgumentException("appender");
      }

      this.appender = appender;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
      appender.close();
    }
  }
}
