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
    assertEquals(v.size(), 1);
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
    Thread.sleep(200);

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
    final int repetitions = 100;
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
      greeter.start();
      Thread.sleep(100);

      //
      //   Undesirable behavior: Interrupts are swallowed by
      //   AsycnAppender which could interfere with expected
      //   response to interrupts if the client code called wait or
      //   sleep.
      //
      greeter.interrupt();
      Thread.sleep(10);
      greeter.interrupt();
      Thread.sleep(10);
      greeter.interrupt();
    }

    greeter.join();
    asyncAppender.close();

    Vector events = blockableAppender.getVector();
    assertEquals(100, events.size());
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

    //
    //   Undesirable action: interrupting the dispatch thread
    //        removes all appenders.
    //
    Enumeration iter = asyncAppender.getAllAppenders();
    assertTrue((iter == null) || !iter.hasMoreElements());
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
    assertEquals(0, asyncAppender.getBufferSize());

    //
    //   any logging request will deadlock.
    //root.debug("m1");
    //root.debug("m2");
    asyncAppender.close();

    Vector v = vectorAppender.getVector();
    assertEquals(v.size(), 0);
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
      synchronized (this) {
        for (int i = 0; (i < repetitions) && !Thread.interrupted(); i++) {
          logger.info("Hello, World");
        }
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
