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


//      Contributors:      Dan Milstein 
//                         Ray Millard
//                         Ray DeCampo
package org.apache.log4j;

import java.util.Stack;


/**
   The NDC class implements <i>nested diagnostic contexts</i> as
   defined by Neil Harrison in the article "Patterns for Logging
   Diagnostic Messages" part of the book "<i>Pattern Languages of
   Program Design 3</i>" edited by Martin et al.

   <p>A Nested Diagnostic Context, or NDC in short, is an instrument
   to distinguish interleaved log output from different sources. Log
   output is typically interleaved when a server handles multiple
   clients near-simultaneously.

   <p>Interleaved log output can still be meaningful if each log entry
   from different contexts had a distinctive stamp. This is where NDCs
   come into play.

   <p><em><b>Note that NDCs are managed on a per thread
   basis</b></em>. NDC operations such as {@link #push push}, {@link
   #pop}, {@link #clear}, {@link #getDepth} and {@link #setMaxDepth}
   affect the NDC of the <em>current</em> thread only. NDCs of other
   threads remain unaffected.

   <p>For example, a servlet can build a per client request NDC
   consisting the clients host name and other information contained in
   the the request. <em>Cookies</em> are another source of distinctive
   information. To build an NDC one uses the {@link #push push}
   operation. Simply put,

   <p><ul>
     <li>Contexts can be nested.

     <p><li>When entering a context, call <code>NDC.push</code>. As a
     side effect, if there is no nested diagnostic context for the
     current thread, this method will create it.

     <p><li>When leaving a context, call <code>NDC.pop</code>.

     <p><li>As of log4j 1.3, it is no longer necessary to call {@link #remove
     NDC.remove()} when exiting a thread.</b>.
   </ul>

   <p>There is no penalty for forgetting to match each
   <code>push</code> operation with a corresponding <code>pop</code>,
   except the obvious mismatch between the real application context
   and the context set in the NDC.

   <p>If configured to do so, {@link PatternLayout} and {@link
   TTCCLayout} instances automatically retrieve the nested diagnostic
   context for the current thread without any user intervention.
   Hence, even if a servlet is serving multiple clients
   simultaneously, the logs emanating from the same code (belonging to
   the same category) can still be distinguished because each client
   request will have a different NDC tag.

   <p>A thread may inherit the nested diagnostic context of another
   (possibly parent) thread using the {@link #inherit inherit}
   method. A thread may obtain a copy of its NDC with the {@link
   #cloneStack cloneStack} method and pass the reference to any other
   thread, in particular to a child.

   @author Ceki G&uuml;lc&uuml;
   @since 0.7.0

*/
public class NDC {
  // The synchronized keyword is not used in this class. This may seem
  // dangerous, especially since the class will be used by
  // multiple-threads.
  // This is OK since java Stacks are thread safe.
  // More importantly, when inheriting diagnostic contexts the child
  // thread is handed a clone of the parent's NDC.  It follows that
  // each thread has its own NDC (i.e. stack).
  private static final ThreadLocal tl = new ThreadLocal();

  // No instances allowed.
  private NDC() {
  }

  /**
     Clear any nested diagnostic information if any. This method is
     useful in cases where the same thread can be potentially used
     over and over in different unrelated contexts.

     <p>This method is equivalent to calling the {@link #setMaxDepth}
     method with a zero <code>maxDepth</code> argument.

     @since 0.8.4c */
  public static void clear() {
    Stack stack = (Stack) tl.get();

    if (stack != null) {
      stack.setSize(0);
    }
  }

  /**
     Clone the diagnostic context for the current thread.

     <p>Internally a diagnostic context is represented as a stack.  A
     given thread can supply the stack (i.e. diagnostic context) to a
     child thread so that the child can inherit the parent thread's
     diagnostic context.

     <p>The child thread uses the {@link #inherit inherit} method to
     inherit the parent's diagnostic context.

     @return Stack A clone of the current thread's  diagnostic context.

  */
  public static Stack cloneStack() {
    Object o = tl.get();

    if (o == null) {
      return null;
    } else {
      Stack stack = (Stack) o;

      return (Stack) stack.clone();
    }
  }

  /**
     Inherit the diagnostic context of another thread.

     <p>The parent thread can obtain a reference to its diagnostic
     context using the {@link #cloneStack} method.  It should
     communicate this information to its child so that it may inherit
     the parent's diagnostic context.

     <p>The parent's diagnostic context is cloned before being
     inherited. In other words, once inherited, the two diagnostic
     contexts can be managed independently.

     <p>In java, a child thread cannot obtain a reference to its
     parent, unless it is directly handed the reference. Consequently,
     there is no client-transparent way of inheriting diagnostic
     contexts. Do you know any solution to this problem?

     @param stack The diagnostic context of the parent thread.

  */
  public static void inherit(Stack stack) {
    if (stack != null) {
      tl.set(stack);
    }
  }

  /**
     <font color="#FF4040"><b>Never use this method directly, use the {@link
     org.apache.log4j.spi.LoggingEvent#getNDC} method instead</b></font>.
  */
  public static String get() {
    Stack s = (Stack) tl.get();

    if ((s != null) && !s.isEmpty()) {
      return ((DiagnosticContext) s.peek()).fullMessage;
    } else {
      return null;
    }
  }

  /**
     Get the current nesting depth of this diagnostic context.

     @see #setMaxDepth
     @since 0.7.5
   */
  public static int getDepth() {
    Stack stack = (Stack) tl.get();

    if (stack == null) {
      return 0;
    } else {
      return stack.size();
    }
  }

  /**
     Clients should call this method before leaving a diagnostic
     context.

     <p>The returned value is the value that was pushed last. If no
     context is available, then the empty string "" is returned.

     @return String The innermost diagnostic context.

     */
  public static String pop() {
    Stack stack = (Stack) tl.get();

    if ((stack != null) && !stack.isEmpty()) {
      return ((DiagnosticContext) stack.pop()).message;
    } else {
      return "";
    }
  }

  /**
     Looks at the last diagnostic context at the top of this NDC
     without removing it.

     <p>The returned value is the value that was pushed last. If no
     context is available, then the empty string "" is returned.

     @return String The innermost diagnostic context.

     */
  public static String peek() {
    Stack stack = (Stack) tl.get();

    if ((stack != null) && !stack.isEmpty()) {
      return ((DiagnosticContext) stack.peek()).message;
    } else {
      return "";
    }
  }

  /**
     Push new diagnostic context information for the current thread.

     <p>The contents of the <code>message</code> parameter is
     determined solely by the client.

     @param message The new diagnostic context information.  */
  public static void push(String message) {
    Stack stack = (Stack) tl.get();

    if (stack == null) {
      DiagnosticContext dc = new DiagnosticContext(message, null);
      stack = new Stack();
      tl.set(stack);
      stack.push(dc);
    } else if (stack.isEmpty()) {
      DiagnosticContext dc = new DiagnosticContext(message, null);
      stack.push(dc);
    } else {
      DiagnosticContext parent = (DiagnosticContext) stack.peek();
      stack.push(new DiagnosticContext(message, parent));
    }
  }

  /**
     Remove the diagnostic context for this thread.

     <p>As of log4j 1.3, the <code>NDC</code> class uses {@link ThreadLocal}
     technology to store the context.  It is no longer necessary to call this
     method.  It remains for backwards compatibility.
  */
  public static void remove() {
  }

  /**
     Set maximum depth of this diagnostic context. If the current
     depth is smaller or equal to <code>maxDepth</code>, then no
     action is taken.

     <p>This method is a convenient alternative to multiple {@link
     #pop} calls. Moreover, it is often the case that at the end of
     complex call sequences, the depth of the NDC is
     unpredictable. The <code>setMaxDepth</code> method circumvents
     this problem.

     <p>For example, the combination
     <pre>
       void foo() {
       &nbsp;  int depth = NDC.getDepth();

       &nbsp;  ... complex sequence of calls

       &nbsp;  NDC.setMaxDepth(depth);
       }
     </pre>

     ensures that between the entry and exit of foo the depth of the
     diagnostic stack is conserved.

     @see #getDepth
     @since 0.7.5 */
  public static void setMaxDepth(int maxDepth) {
    Stack stack = (Stack) tl.get();

    if ((stack != null) && (maxDepth < stack.size())) {
      stack.setSize(maxDepth);
    }
  }

  // =====================================================================
  private static class DiagnosticContext {
    String fullMessage;
    String message;

    DiagnosticContext(String message, DiagnosticContext parent) {
      this.message = message;

      if (parent != null) {
        fullMessage = parent.fullMessage + ' ' + message;
      } else {
        fullMessage = message;
      }
    }
  }
}
