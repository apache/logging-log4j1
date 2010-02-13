/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.FileOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * An implementation of the "prudent" mode where file locking and
 * repositioning are used to minimize the consequences of 
 * concurrent writes to a log file multiple appenders or
 * programs.  The effectiveness is operating system and
 * Java platform dependent.  Avoiding concurrent writing
 * of log files is recommended.
 * 
 * @since 1.2.16
 */
public final class PrudentWriter extends FilterWriter {
	/**
	 * File channel associated with encapsulated writer.
	 */
	private final FileChannel channel;

	/**
	 * Constructs a new instance.
	 * @param out writer to encapsulate, may not be null.
	 * @param ostream file stream corresponding to writer, may not be null.
	 */
	public PrudentWriter(final Writer out, final FileOutputStream ostream) {
		super(out);
		channel = ostream.getChannel();
	}
	
	/**
	 * Write string to encapsulated writer within a lock
	 * on the associated FileChannel.  Channel will be 
	 * repositioned to the end of the file if necessary. 
	 */
	public void write(final String str) throws IOException {
		FileLock lock = null;
		try {
			lock = channel.lock();
			long size = channel.size();
			if (size != channel.position()) {
				channel.position(size);
			}
			super.write(str);
			super.flush();
		} finally {
			if (lock != null) {
				lock.release();
			}
		}
	}
	
}
