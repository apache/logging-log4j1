/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.log4j.rule;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import java.util.Stack;

/**
 * A Rule class providing support for ORO-based regular expression syntax. 
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class LikeRule extends AbstractRule {
  private static final LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
  private final Pattern pattern;
  private final Perl5Matcher matcher = new Perl5Matcher();
  private final String field;

  private LikeRule(String field, Pattern pattern) {
    if (!resolver.isField(field)) {
        throw new IllegalArgumentException("Invalid LIKE rule - " + field + " is not a supported field");
    }
    
    this.field = field;
    this.pattern = pattern;
  }

  public static Rule getRule(Stack stack) {
      if (stack.size() < 2) {
          throw new IllegalArgumentException("Invalid LIKE rule - expected two parameters but received " + stack.size());
      }  
      
      String p2 = stack.pop().toString();
      String p1 = stack.pop().toString();
      return getRule(p1, p2);
  }

  public static Rule getRule(String field, String pattern) {
    Perl5Compiler compiler = new Perl5Compiler();
    Pattern pattern1 = null;

    try {
      pattern1 = compiler.compile(pattern, Perl5Compiler.CASE_INSENSITIVE_MASK);
    } catch (MalformedPatternException e) {
        throw new IllegalArgumentException("Invalid LIKE rule - " + e.getMessage());
    }

    return new LikeRule(field, pattern1);
  }

  public boolean evaluate(LoggingEvent event) {
    Object input = resolver.getValue(field, event);
    return ((input != null) && (pattern != null) && (matcher.matches(input.toString(), pattern)));
  }
}
