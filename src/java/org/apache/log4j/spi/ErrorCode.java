//  Copyright 2000 Ceki Gulcu.  All Rights Reserved.
//  See the LICENCE file for the terms of distribution.

package org.log4j.spi;


/**
   This interface defines commonly encoutered error codes.

   @author Ceki G&uuml;lc&uuml;
   @since 0.9.0
 */
public interface ErrorCode {

  public final int WRITE_FAILURE = 1;
  public final int FLUSH_FAILURE = 2;
  public final int CLOSE_FAILURE = 3;
  public final int FILE_OPEN_FAILURE = 4;
  public final int MISSING_LAYOUT = 5;
  public final int ADDRESS_PARSE_FAILURE = 6;
}
