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

package joran.implicit;

import org.apache.joran.Interpreter;
import org.apache.joran.Pattern;
import org.apache.joran.RuleStore;
import org.apache.joran.helper.SimpleRuleStore;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * This example illustrates the usage of implcit actions.
 * 
 * The crucial point to remember about implicit actions is that they
 * are not associated with a pattern. Moreover, they are added directly to
 * a Joran Interpreter instead of a rule store.
 * 
 * @author Ceki G&uuml;ulc&uuml;
 */
public class PrintMe {
  
  
  public static void main(String[] args) throws Exception {
    // Uncomment the following line in order to enable log statements generated
    // from joran itself.
    
    // org.apache.log4j.BasicConfigurator.configure();
    
    RuleStore ruleStore = new SimpleRuleStore();

    // we start with the rule for the top-most (root) element
    ruleStore.addRule(new Pattern("*/foo"), new NOPAction());


    // Create a new Joran Interpreter and hand it our simple rule store.
    Interpreter ji = new Interpreter(ruleStore);

    // --------------------------+
    // Add an implicit action.   |
    // --------------------------+
    ji.addImplicitAction(new PrintMeImplicitAction());
    
    // Create a SAX parser
    SAXParserFactory spf = SAXParserFactory.newInstance();
    SAXParser saxParser = spf.newSAXParser();

    // Parse the file given as the application's first argument and
    // set the SAX ContentHandler to the Joran Interpreter we just created.
    saxParser.parse(args[0], ji);

    // The file has been parsed and interpreted. We now print any errors that 
    // might have occured.
    List errorList = ji.getExecutionContext().getErrorList();

    if (errorList.size() > 0) {
      System.out.println("The following errors occured:");

      for (int i = 0; i < errorList.size(); i++) {
        System.out.println("\t" + errorList.get(i));
      }
    }
  }
}
