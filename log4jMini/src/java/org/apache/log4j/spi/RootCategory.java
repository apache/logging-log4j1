
package org.apache.log4j.spi;

import  org.apache.log4j.Category;
import  org.apache.log4j.Priority;
import  org.apache.log4j.helpers.LogLog;


/**
   RootCategory sits at the top of the category hierachy. It is a
   regular category except that it provides several guarantees.

   <p>First, it cannot be assigned a <code>null</code>
   priority. Second, since root category cannot have a parent, the
   {@link #getChainedPriority} method always returns the value of the
   priority field without walking the hierarchy.

   @author Ceki G&uuml;lc&uuml;

 */
final public class RootCategory extends Category {

  /**
     The root category names itself as "root". However, the root
     category cannot be retrieved by name.  
  */
  public
  RootCategory(Priority priority) {
    super("root");
    setPriority(priority);
  }

  
  /**
     Return the assigned priority value without walking the category
     hierarchy.
  */
  final
  public 
  Priority getChainedPriority() {
    return priority;
  }

  /**
     Setting a null value to the priority of the root category may have catastrophic
     results. We prevent this here.

     @since 0.8.3 */
  final  
  public
  void setPriority(Priority priority) {
    if(priority == null) {
      LogLog.error("null priority disallowed", new Throwable());
    }
    else {
      this.priority = priority;
    }
  }
}
