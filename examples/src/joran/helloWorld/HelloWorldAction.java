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

import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;

import org.xml.sax.Attributes;


/**
 * A trivial action that writes "Hello world" on the console.
 * 
 * See the HelloWorld class for integrating with Joran.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class HelloWorldAction extends Action {
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    System.out.println("Hello World");
  }

  public void end(ExecutionContext ec, String name) {
  }
}
