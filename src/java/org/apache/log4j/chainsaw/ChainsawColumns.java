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

package org.apache.log4j.chainsaw;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ChainsawColumns {
  private static final List columnNames = new ArrayList();

  static {
    columnNames.add(ChainsawConstants.LOGGER_COL_NAME);
    columnNames.add(ChainsawConstants.TIMESTAMP_COL_NAME);
    columnNames.add(ChainsawConstants.LEVEL_COL_NAME);
    columnNames.add(ChainsawConstants.THREAD_COL_NAME);
    columnNames.add(ChainsawConstants.MESSAGE_COL_NAME);
    columnNames.add(ChainsawConstants.NDC_COL_NAME);
    columnNames.add(ChainsawConstants.MDC_COL_NAME);
    columnNames.add(ChainsawConstants.THROWABLE_COL_NAME);
    columnNames.add(ChainsawConstants.CLASS_COL_NAME);
    columnNames.add(ChainsawConstants.METHOD_COL_NAME);
    columnNames.add(ChainsawConstants.FILE_COL_NAME);
    columnNames.add(ChainsawConstants.LINE_COL_NAME);
    columnNames.add(ChainsawConstants.PROPERTIES_COL_NAME);

    //NOTE:  ID must ALWAYS be last field because the model adds this value itself as an identifier to the end of the consructed vector
    columnNames.add(ChainsawConstants.ID_COL_NAME);
  }
  
  public static final int INDEX_LOGGER_COL_NAME = 1;
  public static final int INDEX_TIMESTAMP_COL_NAME = 2;
  public static final int INDEX_LEVEL_COL_NAME = 3;
  public static final int INDEX_THREAD_COL_NAME = 4;
  public static final int INDEX_MESSAGE_COL_NAME = 5;
  public static final int INDEX_NDC_COL_NAME = 6;
  public static final int INDEX_MDC_COL_NAME = 7;
  public static final int INDEX_THROWABLE_COL_NAME = 8;
  public static final int INDEX_CLASS_COL_NAME = 9;
  public static final int INDEX_METHOD_COL_NAME = 10;
  public static final int INDEX_FILE_COL_NAME = 11;
  public static final int INDEX_LINE_COL_NAME = 12;
  public static final int INDEX_PROPERTIES_COL_NAME = 13;
  public static final int INDEX_ID_COL_NAME = 14;
  
  private ChainsawColumns() {
  }

  public static List getColumnsNames() {
    return columnNames;
  }
}
