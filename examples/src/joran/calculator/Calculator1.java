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

package joran.calculator;

import java.util.List;

import org.apache.joran.Interpreter;
import org.apache.joran.Pattern;
import org.apache.joran.RuleStore;
import org.apache.joran.helper.SimpleRuleStore;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * This examples illustrates collaboration between multiple actions through the
 * common execution context stack.
 * 
 * The first and only argument of this application must be the path to
 * the XML file to interpret. There are sample XML files in the 
 * <em>examples/src/joran/calculator/</em> directory. 
 *
 * For example,
 *
<pre>
    java joran.calculator.Calculator1 examples/src/joran/calculator/calculator1.xml
</pre>
 *
 * Please refer to the comments in the source code for more information.
 * 
 * @author Ceki G&uuml;ulc&uuml;
 */
public class Calculator1 {
  
  
  public static void main(String[] args) throws Exception {
    // Create a simple rule store where pattern and action associations will
    // be kept. This is a basic requirement before invoking a Joran Interpreter.
    RuleStore ruleStore = new SimpleRuleStore();

    // Associate "/computation" pattern with  ComputationAction1
    ruleStore.addRule(new Pattern("/computation"), new ComputationAction1());

    // Other associations
    ruleStore.addRule(new Pattern("/computation/literal"), new LiteralAction());
    ruleStore.addRule(new Pattern("/computation/add"), new AddAction());
    ruleStore.addRule(new Pattern("/computation/multiply"), new MultiplyAction());
    
    // Create a new Joran Interpreter and hand it our simple rule store.
    Interpreter ji = new Interpreter(ruleStore);

    // Create a SAX parser
    SAXParserFactory spf = SAXParserFactory.newInstance();
    SAXParser saxParser = spf.newSAXParser();

    // Parse the file given as the application's first argument and
    // set the SAX ContentHandler to the Joran Interpreter we just created.
    saxParser.parse(args[0], ji);

    // The file has been parsed and interpreted. We now errors if any.
    List errorList = ji.getExecutionContext().getErrorList();
    if(errorList.size() > 0) {
      System.out.println("The following errors occured:");
      for(int i = 0; i < errorList.size(); i++) {
        System.out.println("\t"+errorList.get(i));
      }
    }
  }
}
