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
package org.apache.log4j.rolling;

import org.apache.log4j.rolling.helpers.Compress;

/**
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
abstract public class RollingPolicySkeleton implements RollingPolicy {
  int compressionMode = Compress.NONE;
  
  /* (non-Javadoc)
   * @see org.apache.log4j.rolling.RollingPolicy#rollover()
   */
  abstract public void rollover();
  
  /* (non-Javadoc)
   * @see org.apache.log4j.rolling.RollingPolicy#getActiveLogFileName()
   */
  abstract public String getActiveLogFileName();
  
  /* (non-Javadoc)
   * @see org.apache.log4j.spi.OptionHandler#activateOptions()
   */
  abstract public void activateOptions();
  
  public String getCompressionMode() {
    switch(compressionMode) {
      case Compress.NONE: return Compress.NONE_STR;
      case Compress.GZ: return Compress.GZ_STR;
      case Compress.ZIP: return Compress.ZIP_STR;
    }
    return Compress.NONE_STR;
  }


  public void setCompressionMode(String compMode) {
    if(compMode == null) {
      compressionMode = Compress.NONE;
    }
    compMode = compMode.trim();
    if(compMode.equalsIgnoreCase(Compress.GZ_STR)) {
      compressionMode = Compress.GZ;
    } else if (compMode.equalsIgnoreCase(Compress.ZIP_STR)) {
      compressionMode = Compress.ZIP;
    } else {
      compressionMode = Compress.NONE;
    }   
  }
}
