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

import org.apache.log4j.spi.LoggingEvent;

import java.util.Comparator;


/**
 *
 * @author Claude Duguay
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
*/
public class ColumnComparator implements Comparator {
  protected int index;
  protected boolean ascending;

  public ColumnComparator(int index, boolean ascending) {
    this.index = index;
    this.ascending = ascending;
  }

  public int compare(Object o1, Object o2) {
    int sort = 1;

    if (o1 instanceof LoggingEvent && o2 instanceof LoggingEvent) {

//		TODO not everything catered for here yet...

      LoggingEvent e1 = (LoggingEvent) o1;
      LoggingEvent e2 = (LoggingEvent) o2;

      switch (index + 1) {
      case ChainsawColumns.INDEX_LEVEL_COL_NAME:
        sort = e1.getLevel().isGreaterOrEqual(e2.getLevel()) ? 1 : (-1);

        break;

      case ChainsawColumns.INDEX_LOGGER_COL_NAME:
        sort = e1.getLoggerName().compareToIgnoreCase(e2.getLoggerName());

        break;

      case ChainsawColumns.INDEX_MESSAGE_COL_NAME:
        sort =
          e1.getMessage().toString().compareToIgnoreCase(
            e2.getMessage().toString());

        break;

      case ChainsawColumns.INDEX_METHOD_COL_NAME:

        if (
          (e1.getLocationInformation() != null)
            & (e2.getLocationInformation() != null)) {
          sort =
            e1.getLocationInformation().getMethodName().compareToIgnoreCase(
              e2.getLocationInformation().getMethodName());
        }

        break;

      case ChainsawColumns.INDEX_CLASS_COL_NAME:

        if (
          (e1.getLocationInformation() != null)
            & (e2.getLocationInformation() != null)) {
          sort =
            e1.getLocationInformation().getClassName().compareToIgnoreCase(
              e2.getLocationInformation().getClassName());
        }

        break;

      case ChainsawColumns.INDEX_FILE_COL_NAME:

        if (
          (e1.getLocationInformation() != null)
            & (e2.getLocationInformation() != null)) {
          sort =
            e1.getLocationInformation().getFileName().compareToIgnoreCase(
              e2.getLocationInformation().getFileName());
        }

        break;
        
       case ChainsawColumns.INDEX_TIMESTAMP_COL_NAME:
       		sort = (e1.timeStamp<e2.timeStamp ? -1 : (e1.timeStamp==e2.timeStamp ? 0 : 1));
       		break;
       		
       case ChainsawColumns.INDEX_THREAD_COL_NAME:
       		sort = e1.getThreadName().compareToIgnoreCase(e2.getThreadName());
       		break;
      }
    }

    sort = (sort == 0) ? 0 : ((sort < 0) ? (-1) : 1);

    if (!ascending && (sort != 0)) {
      sort = (sort < 0) ? 1 : (-1);
    }

    return sort;
  }
}
