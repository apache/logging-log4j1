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

package joran.helloWorld;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.joran.Interpreter;
import org.apache.joran.Pattern;
import org.apache.joran.RuleStore;
import org.apache.joran.helper.SimpleRuleStore;

/**
 * 
 * A hello world example using Joran.
 * 
 * The first and only argument of this application must be the path to
 * the XML file to interpret.
 * 
 * For example,
 * 
<pre>
    java joran.helloWorld.HelloWorld examples/src/joran/helloWorld/hello.xml
</pre>
 *
 * @author Ceki
 */
public class HelloWorld {
  public static void main(String[] args) throws Exception {
    
    // Create a simple rule store where pattern and action associations will
    // be kept.
    RuleStore ruleStore = new SimpleRuleStore();
    
    // Associate "hello-world" pattern with  HelloWorldAction
    ruleStore.addRule(
      new Pattern("hello-world"), new HelloWorldAction());
 
    // Create a new Joran Interpreter and hand it our simple rule store.
    Interpreter ji = new Interpreter(ruleStore);
    
    // Create a SAX parser
    SAXParserFactory spf = SAXParserFactory.newInstance();
    SAXParser saxParser =  spf.newSAXParser();
    
    // Parse the file given as the application's first argument and
    // set the SAX ContentHandler to the Joran Interpreter we just created.
    saxParser.parse(args[0], ji);  
  }
}
