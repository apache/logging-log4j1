/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.log4j.helpers;

import java.io.PrintWriter;


/**
 *
 * A RederWriterLock allows multiple readers to obtain the lock at the same time
 * but allows only one writer at a time.
 *
 * When both readers and writers wait to obtain the lock, priority is given to 
 * waiting writers.
 *
 * This lock is not reentrant. It is possible for a writer in possession of a writer
 * lock to fail to obtain a reader lock. The same goes for reader in possession of a 
 * reader lock. It can fail to obtain a writer lock.
 * 
 * THIS LOCK IS NOT RENTRANT.
 * 
 * It is the developer's responsability to retstrict the use of this lock to small segments
 * of code where reentrancy can be avoided.
 * 
 * Note that the RederWriterLock is only useful in cases where a resource:
 * 
 * 1) Has many frequent read operations performed on it
 * 2) Only rarely is the resource modified (written)
 * 3) Read operations are invoked by many different threads
 * 
 * If any of the above conditions are not met, it is better to avoid this fancy lock.
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class ReaderWriterLock {
  int readers = 0;
  int writers = 0;
  int waitingWriters = 0;
  PrintWriter printWriter;

  public ReaderWriterLock() {
  }

  public ReaderWriterLock(PrintWriter pw) {
    printWriter = pw;
  }

  public synchronized void getReadLock() {
    if (printWriter != null) {
      printMessage("Asking for read lock.");
    }

    while ((writers > 0) || (waitingWriters > 0)) {
      try {
        wait();
      } catch (InterruptedException ie) {
      }
    }

    if (printWriter != null) {
      printMessage("Got read lock.");
    }

    readers++;
  }

  public synchronized void releaseReadLock() {
    if (printWriter != null) {
      printMessage("About to release read lock.");
    }

    readers--;

    if (waitingWriters > 0) {
      notifyAll();
    }
  }

  public synchronized void getWriteLock() {
    if (printWriter != null) {
      printMessage("Asking for write lock.");
    }

    waitingWriters++;

    while ((readers > 0) || (writers > 0)) {
      try {
        wait();
      } catch (InterruptedException ie) {
      }
    }

    if (printWriter != null) {
      printMessage("Got write lock.");
    }

    waitingWriters--;
    writers++;
  }

  public synchronized void releaseWriteLock() {
    if (printWriter != null) {
      printMessage("About to release write lock.");
    }

    writers--;
    notifyAll();
  }

  void printMessage(String msg) {
    //printWriter.print("[");      
    printWriter.println(Thread.currentThread().getName() + " " + msg);
  }
}
