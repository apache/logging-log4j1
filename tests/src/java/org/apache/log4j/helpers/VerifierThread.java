/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.helpers;

import org.apache.oro.text.perl.Perl5Util;

import java.io.BufferedReader;
import java.io.IOException;


/**
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

  VerifierThread(BufferedReader br, int numberOfReaders, int numberOfWriters) {
    bufferedReader = br;
    readLockHolders = new boolean[numberOfReaders];
    readLockWaiters = new boolean[numberOfReaders];
    writerLockWaiters = new boolean[numberOfWriters];
  }

 
  public void run() {
    System.out.println("In run of VerifThread");

    Perl5Util regex = new Perl5Util();

    while (true) {
      try {
        String line = bufferedReader.readLine();
		    System.out.println(line); 		    
     
        if (regex.match("/([RW])-(\\d{1,2}) (.*)/", line)) {
          String type = regex.group(1);
          int num = Integer.parseInt(regex.group(2));
          String msg = regex.group(3);

          if (type.equals("R")) {
            readerMsg(num, msg);
          } else if (type.equals("W")) {
          }
        } else {
          System.out.println(
            "[" + line + "] does not match expected pattern.");
        }

        //System.out.println("."+type+"-"+num+" "+msg); 		    
      } catch (IOException e) {
      }
    }
  }

  void readerMsg(int num, String msg) {
    if (msg.equals("Asking for read lock.")) {
      askReadLock(num);
    } else if (msg.equals("Got read lock.")) {
      gotReadLock(num);
    } else if (msg.startsWith("Value")) {
      //releaseReadLock(num);
    } else if (msg.equals("About to release read lock.")) {
      releaseReadLock(num);
    }
  }

  void writerMsg(int num, String msg) {
    if (msg.equals("Asking for write lock.")) {
      askWriterLock(num);
    } else if (msg.equals("Got write lock.")) {
      gotWriteLock(num);
    } else if (msg.startsWith("Value")) {
      //releaseReadLock(num);
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
}
