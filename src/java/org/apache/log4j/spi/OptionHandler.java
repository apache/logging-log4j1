/**
  Copyright (c) 2000, Ceki Gulcu

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation files
  (the "Software"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge,
  publish, distribute, sublicense, and/or sell copies of the Software,
  and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
  ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/

package org.apache.log4j.spi;

/**

   A string based interface to configure package components.

   @author Ceki G&uuml;lc&uuml;
   @since 0.8.1
 */
public interface OptionHandler {

  /**
     Activate the options that were previously set with calls to {@link
     #setOption setOption}.

     <p>This allows to defer activiation of the options until all
     options have been set. This is required for components which have
     related options that remain ambigous until all are set.

     <p>For example, the FileAppender has the "File" and "Append" options
     both of which are ambigous until the other is also set.
  */
  void activateOptions();  

  /**
     Return list of strings that the OptionHandler instance recognizes.
   */
  String[] getOptionStrings();

  /**
     Set <code>option</code> to <code>value</code>.

     <p>The handling of each option depends on the OptionHandler
     instance. Some options may become active immediately whereas
     other may be activated only when {@link #activateOptions} is
     called.

  */
  void setOption(String option, String value);
}
