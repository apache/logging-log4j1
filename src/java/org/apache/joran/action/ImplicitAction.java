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

package org.apache.joran.action;

import org.apache.joran.ExecutionContext;

/**
 * ImplcitActions are like normal (explicit) actions except that are applied
 * by the parser when no other pattern applies. Since there can be many implcit 
 * actions, each action is asked whether it applies in the given context. The 
 * first impplcit action to respond postively will be applied. See also the
 * {@link #isApplicable} method.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public abstract class ImplicitAction extends Action {

  public abstract boolean isApplicable(ExecutionContext ec, String nestedElementTagName);
}
