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

package trivial;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.NDC;


/**
   View the <a href="doc-files/Trivial.java">source code</a> of this a
   trivial usage example. Running <code>java examples.Trivial</code>
   should output something similar to:

   <pre>
      0    INFO  [main] examples.Trivial (Client #45890) - Awake awake. Put on thy strength.
      15   DEBUG [main] examples.Trivial (Client #45890 DB) - Now king David was old.
      278  INFO  [main] examples.Trivial$InnerTrivial (Client #45890) - Entered foo.
      293  INFO  [main] examples.Trivial (Client #45890) - Exiting Trivial.
   </pre>

   <p> The increasing numbers at the beginning of each line are the
   times elapsed since the start of the program. The string between
   the parentheses is the nested diagnostic context.

   <p>See {@link Sort} and {@link SortAlgo} for sligtly more elaborate
   examples.

   <p>Note thent class files for the example code is not included in
   any of the distributed log4j jar files. You will have to add the
   directory <code>/dir-where-you-unpacked-log4j/classes</code> to
   your classpath before trying out the examples.

 */
public class Trivial {
  static Category cat = Category.getInstance(Trivial.class.getName());

  public static void main(String[] args) {
    BasicConfigurator.configure();
    NDC.push("Client #45890");

    cat.info("Awake awake. Put on thy strength.");
    Trivial.foo();
    InnerTrivial.foo();
    cat.info("Exiting Trivial.");
  }

  static void foo() {
    NDC.push("DB");
    cat.debug("Now king David was old.");
    NDC.pop();
  }

  static class InnerTrivial {
    static Category cat = Category.getInstance(InnerTrivial.class.getName());

    static void foo() {
      cat.info("Entered foo.");
    }
  }
}
