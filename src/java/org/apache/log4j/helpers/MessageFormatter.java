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

package org.apache.log4j.helpers;


/**
 * Formats messages according to very simple rules.
 * See {@link #format(String,Object)} and
 * {@link #format(String,Object,Object)} for more details.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class MessageFormatter {
    /**
     * Private formatter since all methods and members are static.
     */
    private MessageFormatter() {
        super();
    }

    /**
     * Start of replacement block.
     */
    private static final char DELIM_START = '{';
    /**
     * End of replacement block.
     */
    private static final char DELIM_STOP = '}';

    /**
     * Performs single argument substitution for the 'messagePattern' passed as
     * parameter.
     * <p/>
     * For example, <code>MessageFormatter.format("Hi {}.", "there");</code>
     * will return the string "Hi there.".
     * <p/>
     * The {} pair is called the formatting element. It serves to designate the
     * location where the argument needs to be inserted within the pattern.
     *
     * @param messagePattern
     *     The message pattern which will be parsed and formatted
     * @param argument
     *     The argument to be inserted instead of the formatting element
     * @return The formatted message
     */
    public static String format(final String messagePattern,
                                final Object argument) {
        int j = messagePattern.indexOf(DELIM_START);
        int len = messagePattern.length();
        char escape = 'x';

        // if there are no { characters or { is the last character
        // then we just return messagePattern
        if (j == -1 || (j + 1 == len)) {
            return messagePattern;
        } else {
            char delimStop = messagePattern.charAt(j + 1);
            if (j > 0) {
                escape = messagePattern.charAt(j - 1);
            }
            if ((delimStop != DELIM_STOP) || (escape == '\\')) {
                // invalid DELIM_START/DELIM_STOP pair or espace character is
                // present
                return messagePattern;
            } else {
                StringBuffer sbuf = new StringBuffer(len + 20);
                sbuf.append(messagePattern.substring(0, j));
                sbuf.append(argument);
                sbuf.append(messagePattern.substring(j + 2));
                return sbuf.toString();
            }
        }
    }

    /**
     * /**
     * Performs a two argument substitution for the 'messagePattern' passed as
     * parameter.
     * <p/>
     * For example, <code>MessageFormatter.format("Hi {}. My name is {}.",
     * "there", "David");</code> will return the string
     * "Hi there. My name is David.".
     * <p/>
     * The '{}' pair is called a formatting element. It serves to designate the
     * location where the arguments need to be inserted within
     * the message pattern.
     *
     * @param messagePattern
     *     The message pattern which will be parsed and formatted
     * @param arg1
     *     The first argument to replace the first formatting element
     * @param arg2
     *     The second argument to replace the second formatting element
     * @return The formatted message
     */
    public static String format(final String messagePattern,
                                final Object arg1,
                                final Object arg2) {
        int i = 0;
        int len = messagePattern.length();

        StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);

        for (int l = 0; l < 2; l++) {
            int j = messagePattern.indexOf(DELIM_START, i);

            if (j == -1 || (j + 1 == len)) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return messagePattern;
                } else {
                    // add the tail string which contains no variables
                    // and return the result.
                    sbuf.append(messagePattern.substring(i,
                                    messagePattern.length()));
                    return sbuf.toString();
                }
            } else {
                char delimStop = messagePattern.charAt(j + 1);
                if ((delimStop != DELIM_STOP)) {
                    // invalid DELIM_START/DELIM_STOP pair
                    sbuf.append(messagePattern.substring(i,
                            messagePattern.length()));
                    return sbuf.toString();
                }
                sbuf.append(messagePattern.substring(i, j));
                if (l == 0) {
                    sbuf.append(arg1);
                } else {
                    sbuf.append(arg2);
                }
                i = j + 2;
            }
        }
        // append the characters following the second {} pair.
        sbuf.append(messagePattern.substring(i, messagePattern.length()));
        return sbuf.toString();
    }
}
