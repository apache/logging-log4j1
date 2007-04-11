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

package org.apache.log4j.varia;


/**
 * <font color="red>Don't use this class.  Use
 *  {@link org.apache.log4j.filter.StringMatchFilter} instead.</font>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 0.9.0
 * @deprecated org.apache.log4j.filter.StringMatchFilter
 */
public class StringMatchFilter
  extends org.apache.log4j.filter.StringMatchFilter {
    /**
       @deprecated Options are now handled using the JavaBeans paradigm.
       This constant is not longer needed and will be removed in the
       <em>near</em> term.
     */
    public static final String STRING_TO_MATCH_OPTION = "StringToMatch";

    /**
       @deprecated Options are now handled using the JavaBeans paradigm.
       This constant is not longer needed and will be removed in the
       <em>near</em> term.
     */
    public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";

    /**
       @deprecated We now use JavaBeans introspection to configure
       components. Options strings are no longer needed.
    */
    public
    String[] getOptionStrings() {
      return new String[] {STRING_TO_MATCH_OPTION, ACCEPT_ON_MATCH_OPTION};
    }

    /**
       @deprecated Use the setter method for the option directly instead
       of the generic <code>setOption</code> method.
    */
    public
    void setOption(String key, String value) {

      if(key.equalsIgnoreCase(STRING_TO_MATCH_OPTION)) {
        this.setStringToMatch(value);
      } else if (key.equalsIgnoreCase(ACCEPT_ON_MATCH_OPTION)) {
        this.setAcceptOnMatch(Boolean.valueOf(value).booleanValue());
      }
    }


}
