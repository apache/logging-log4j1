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

import org.apache.oro.text.perl.Perl5Util;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * This thread knows about the number of reader and writer threads and keeps
 * track of their operations.
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
class VerifierThread extends Thread {
  int writeLockHolder = -1;
  
  boolean[] readLockHolders;
  boolean[] readLockWaiters;
  boolean[] writerLockWaiters;
  BufferedReader bufferedReader;
  double v1 = 0;
  double v2 = 0;
  Perl5Util regex;
  Exception exception;
  boolean closed;

  VerifierThread(BufferedReader br, int numberOfReaders, int numberOfWriters) {
    bufferedReader = br;
    readLockHolders = new boolean[numberOfReaders];
    readLockWaiters = new boolean[numberOfReaders];
    writerLockWaiters = new boolean[numberOfWriters];
    regex = new Perl5Util();
  }

  boolean isClosed() {
    return closed;
  }

  public void run() {
    System.out.println("In run of VerifThread");
	  String line = null;

    while (!closed) {
      try {
        line = bufferedReader.readLine();
        if(!closed) {
          System.out.println(line);
        } 
        if (regex.match("/([RW])-(\\d{1,3}) (.*)/", line)) {
          String type = regex.group(1);
          int num = Integer.parseInt(regex.group(2));
          String msg = regex.group(3);

		      //System.out.println(type +"_"+num+ " "+msg);
          if (type.equals("R")) {
            readerMsg(num, msg);
          } else if (type.equals("W")) {
            writerMsg(num, msg);
          }
        } else {
          System.out.println(
            "[" + line + "] does not match expected pattern.");
        }
      } catch(IOException ioe) {
        System.err.println("IOException occured.");
        ioe.printStackTrace(System.err);
      }catch (Exception e) {
      	if(exception == null) {
          exception = e;
      	}
        closed = true;
        System.out.println("====Offending line ["+line+"].");
        e.printStackTrace(System.out);
      }
    }
  }

  public Exception getException() {
    return exception;
  }

  void readerMsg(int num, String msg) {
    if (msg.equals("Asking for read lock.")) {
      askReadLock(num);
    } else if (msg.equals("Got read lock.")) {
      gotReadLock(num);
    } else if (msg.startsWith("Value1")) {
      value1Message(num, msg);
    } else if (msg.startsWith("Value2")) {
      value2Message(num, msg);
    } else if (msg.equals("About to release read lock.")) {
      releaseReadLock(num);
    }
  }

  void writerMsg(int num, String msg) {
    if (msg.equals("Asking for write lock.")) {
      askWriterLock(num);
    } else if (msg.equals("Got write lock.")) {
      gotWriteLock(num);
    } else if (msg.equals("About to increment values.")) {
      v1 += 1;
      v2 += 10.0;
    } else if (msg.equals("About to release write lock.")) {
      releaseWriteLock(num);
    }
  }

  boolean writerHoldsLock() {
    return writeLockHolder != -1;
  }

  boolean writerIsWaiting() {
    for (int i = 0; i < writerLockWaiters.length; i++) {
      if (writerLockWaiters[i]) {
        return true;
      }
    }

    return false;
  }

  void askReadLock(int num) {
    readLockWaiters[num] = true;
  }

  void askWriterLock(int num) {
    writerLockWaiters[num] = true;
  }

  void gotReadLock(int num) {
    if (!readLockWaiters[num]) {
      throw new IllegalStateException(
        "Reader " + num + " got a lock without asking.");
    }

    if (writerHoldsLock()) {
      throw new IllegalStateException(
        "Reader " + num + " got a lock while a writer had it.");
    }

    if (writerIsWaiting()) {
      throw new IllegalStateException(
        "Reader " + num + " got a lock while a writers were waiting.");
    }

    readLockWaiters[num] = false;
    readLockHolders[num] = true;
  }

  void gotWriteLock(int num) {
    if (!writerLockWaiters[num]) {
      throw new IllegalStateException(
        "Writer " + num + " got a lock without asking.");
    }

    if (writerHoldsLock()) {
      throw new IllegalStateException(
        "Writer " + num + " got a lock while a writer had it.");
    }

    writerLockWaiters[num] = false;
    writeLockHolder = num;
  }

  void releaseReadLock(int num) {
    if (readLockWaiters[num]) {
      throw new IllegalStateException(
        "Reader " + num + " released a lock while waiting for it.");
    }

    if (writerHoldsLock()) {
      throw new IllegalStateException(
        "Reader " + num + " released a lock while a writer had it.");
    }

    readLockHolders[num] = false;
  }

  void releaseWriteLock(int num) {
    if (writerLockWaiters[num]) {
      throw new IllegalStateException(
        "Writer " + num + " released a lock while waiting for it.");
    }

    writeLockHolder = -1;
  }

  void value1Message(int num, String msg) {
    if (regex.match("/Value1 is (\\d*)/", msg)) {
      double r = Double.parseDouble(regex.group(1));

      if (r != v1) {
        throw new IllegalStateException(
          "Reported value is " + r + " was expecting " + v1);
      }
    }
  }

  void value2Message(int num, String msg) {
    if (regex.match("/Value1 is (\\d*)/", msg)) {
      double r = Double.parseDouble(regex.group(1));

      if (r != v2) {
        throw new IllegalStateException(
          "Reported value is " + r + " was expecting " + v2);
      }
    }
  }
}
