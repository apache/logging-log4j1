/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j.rolling.helper;

import java.io.File;


/**
 * File rename action.
 *
 * @author Curt Arnold
 * @since 1.3
 */
public final class FileRenameAction extends ActionBase {
  /**
   * Source.
   */
  private final File source;

  /**
   * Destination.
   */
  private final File destination;

  /**
   * If true, rename empty files, otherwise delete empty files.
   */
  private final boolean renameEmptyFiles;

  /**
   * Creates an FileRenameAction.
   *
   * @param src current file name.
   * @param dst new file name.
   * @param renameEmptyFiles if true, rename file even if empty, otherwise delete empty files.
   */
  public FileRenameAction(
    final File src, final File dst, boolean renameEmptyFiles) {
    source = src;
    destination = dst;
    this.renameEmptyFiles = renameEmptyFiles;
  }

  /**
   * Rename file.
   *
   * @return true if successfully renamed.
   */
  public boolean execute() {
    return execute(source, destination, renameEmptyFiles);
  }

  /**
   * Rename file.
   * @param source current file name.
   * @param destination new file name.
   * @param renameEmptyFiles if true, rename file even if empty, otherwise delete empty files.
   * @return true if successfully renamed.
   */
  public static boolean execute(
    final File source, final File destination, boolean renameEmptyFiles) {
    if (renameEmptyFiles || (source.length() > 0)) {
      return source.renameTo(destination);
    }

    return source.delete();
  }
}
