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

package org.apache.log4j.varia;


/**
   This is a very simple filter based on string matching.


   <p>The filter admits two options <b>StringToMatch</b> and
   <b>AcceptOnMatch</b>. If there is a match between the value of the
   StringToMatch option and the message of the {@link LoggingEvent},
   then the {@link #decide} method returns {@link Filter#ACCEPT} if
   the <b>AcceptOnMatch</b> option value is true, if it is false then
   {@link Filter#DENY} is returned. If there is no match, {@link
   Filter#NEUTRAL} is returned.

   <p>See configuration files <a
   href="../xml/doc-files/test6.xml">test6.xml</a>, <a
   href="../xml/doc-files/test7.xml">test7.xml</a>, <a
   href="../xml/doc-files/test8.xml">test8.xml</a>, <a
   href="../xml/doc-files/test9.xml">test9.xml</a>, and <a
   href="../xml/doc-files/test10.xml">test10.xml</a> for examples of
   seeting up a <code>StringMatchFilter</code>.


   @author Ceki G&uuml;lc&uuml;

   @since 0.9.0
   @deprecated org.apache.log4j.filter.StringMatchFilter
   */
public class StringMatchFilter
  extends org.apache.log4j.filter.StringMatchFilter {
}
