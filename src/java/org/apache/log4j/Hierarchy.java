/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

// WARNING This class MUST not have references to the Category or
// WARNING RootCategory classes in its static initiliazation neither 
// WARNING directly nor indirectly.

// Contributors:
//                Luke Blanshard <luke@quiq.com>
//                Mario Schomburg - IBM Global Services/Germany
//                Anders Kristensen
//                Igor Poteryaev
 
package org.apache.log4j;


import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.Appender;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

/**
   This class is specialized in retrieving categories by name and
   also maintaining the category hierarchy.

   <p><em>The casual user should not have to deal with this class
   directly.</em> In fact, up until version 0.9.0, this class had
   default package access. 

   <p>The structure of the category hierarchy is maintained by the
   {@link #getInstance} method. The hierarchy is such that children
   link to their parent but parents do not have any pointers to their
   children. Moreover, categories can be instantiated in any order, in
   particular descendant before ancestor.

   <p>In case a descendant is created before a particular ancestor,
   then it creates a provision node for the ancestor and adds itself
   to the provision node. Other descendants of the same ancestor add
   themselves to the previously created provision node.

   @author Ceki G&uuml;lc&uuml; 

*/
public class Hierarchy {

  private CategoryFactory defaultFactory;
  private Vector listeners;

  Hashtable ht;
  Category root;
  RendererMap rendererMap;
  
  int enableInt;
  Level enable;

  boolean emittedNoAppenderWarning = false;
  boolean emittedNoResourceBundleWarning = false;  

  /**
     Create a new Category hierarchy.

     @param root The root of the new hierarchy.

   */
  public
  Hierarchy(Category root) {
    ht = new Hashtable();
    listeners = new Vector(1);
    this.root = root;
    // Enable all level levels by default.
    enable(Level.ALL);
    this.root.setHierarchy(this);
    rendererMap = new RendererMap();
    defaultFactory = new DefaultCategoryFactory();
  }

  /**
     Add an object renderer for a specific class.       
   */
  public
  void addRenderer(Class classToRender, ObjectRenderer or) {
    rendererMap.put(classToRender, or);
  }
  
  public 
  void addHierarchyEventListener(HierarchyEventListener listener) {
    if(listeners.contains(listener)) {
      LogLog.warn("Ignoring attempt to add an existent listener.");
    } else {
      listeners.add(listener);
    }
  }

  /**
     This call will clear all category definitions from the internal
     hashtable. Invoking this method will irrevocably mess up the
     category hierarchy.
     
     <p>You should <em>really</em> know what you are doing before
     invoking this method.

     @since 0.9.0 */
  public
  void clear() {
    //System.out.println("\n\nAbout to clear internal hash table.");
    ht.clear();
  }

  /**
     Check if the named category exists in the hierarchy. If so return
     its reference, otherwise returns <code>null</code>.
     
     @param name The name of the category to search for.
     
  */
  public
  Category exists(String name) {    
    Object o = ht.get(new CategoryKey(name));
    if(o instanceof Category) {
      return (Category) o;
    } else {
      return null;
    }
  }


  /**
     Similar to {@link #disable(Level)} except that the level
     argument is given as a String.  

     @deprecated Replaced with the {@link #enable(Level)} familiy
     of methods
  */
  public
  void disable(String levelStr) {
    Level p = Level.toLevel(levelStr, null);
    if(p != null) {
      disable(p);
    } else {
      LogLog.warn("Could not convert ["+levelStr+"] to Level.");
    }
  }


  /**
     Disable all logging requests of level <em>equal to or
     below</em> the level parameter <code>p</code>, for
     <em>all</em> categories in this hierarchy. Logging requests of
     higher level then <code>p</code> remain unaffected.

     <p>The "disable" family of methods are there for speed. They
     allow printing methods such as debug, info, etc. to return
     immediately after an integer comparison without walking the
     category hierarchy. In most modern computers an integer
     comparison is measured in nanoseconds where as a category walk is
     measured in units of microseconds.

     <p>Other configurators define alternate ways of overriding the
     disable override flag. See {@link PropertyConfigurator} and
     {@link org.apache.log4j.xml.DOMConfigurator}.

     @deprecated Please use the {@link #enable} familiy of methods instead.

     @since 0.8.5 */
  public
  void disable(Level p) {
    if(p != null) {    
      switch(p.level) {
      case Level.ALL_INT: enable(Level.ALL); break;      
      case Level.DEBUG_INT: enable(Level.INFO); break;      
      case Level.INFO_INT: enable(Level.WARN); break;
      case Level.WARN_INT: enable(Level.ERROR); break;      
      case Level.ERROR_INT: enable(Level.FATAL); break;      
      case Level.FATAL_INT: enable(Level.OFF); break;      
      case Level.OFF_INT: enable(Level.OFF); break;      
      default: 
      }
    }
  }
  
  /**
     Disable all logging requests regardless of category and level.
     This method is equivalent to calling {@link #disable} with the
     argument {@link Level#FATAL}, the highest possible level.

     @deprecated Please use the {@link #enable} familiy of methods instead.

     @since 0.8.5 */
  public
  void disableAll() {
    disable(Level.FATAL);
  }


  /**
     Disable all logging requests of level DEBUG regardless of
     category.  Invoking this method is equivalent to calling {@link
     #disable} with the argument {@link Level#DEBUG}.

     @deprecated Please use the {@link #enable} familiy of methods instead.

     @since 0.8.5 */
  public
  void disableDebug() {
    disable(Level.DEBUG);
  }


  /**
     Disable all logging requests of level INFO and below
     regardless of category. Note that DEBUG messages are also
     disabled.  

     <p>Invoking this method is equivalent to calling {@link
     #disable(Level)} with the argument {@link Level#INFO}.

     @deprecated Please use the {@link #enable} familiy of methods instead.

     @since 0.8.5 */
  public
  void disableInfo() {
    disable(Level.INFO);
  }  

  /**
     Undoes the effect of calling any of {@link #disable}, {@link
     #disableAll}, {@link #disableDebug} and {@link #disableInfo}
     methods. More precisely, invoking this method sets the Category
     class internal variable called <code>disable</code> to its
     default "off" value.

     @since 0.8.5 */
  public
  void enableAll() {
    enable(Level.ALL);
  }

  /**
     Enable logging for events with level p or higher.

	  @param p the minimum level for which events are sent to
	         their appenders.
	  @since 1.1.3 */
  public 
  void enable(Level p) {
    if(p != null) {
      enableInt = p.level;
      enable = p;
    }
  }


  
  void fireAddAppenderEvent(Category category, Appender appender) {
    if(listeners != null) {
      int size = listeners.size();
      HierarchyEventListener listener;
      for(int i = 0; i < size; i++) {
	listener = (HierarchyEventListener) listeners.elementAt(i);
	listener.addAppenderEvent(category, appender);
      }
    }        
  }


  void fireRemoveAppenderEvent(Category category, Appender appender) {
    if(listeners != null) {
      int size = listeners.size();
      HierarchyEventListener listener;
      for(int i = 0; i < size; i++) {
	listener = (HierarchyEventListener) listeners.elementAt(i);
	listener.removeAppenderEvent(category, appender);
      }
    }        
  }

  /**
     Returns a {@link Level} representation of the
     <code>enable</code> state.  

     @since 1.2
  */
  public
  Level getEnable() {
    return enable;
  }

  /**
     Return a new category instance named as the first parameter using
     the default factory. 
     
     <p>If a category of that name already exists, then it will be
     returned.  Otherwise, a new category will be instantiated and
     then linked with its existing ancestors as well as children.
     
     @param name The name of the category to retrieve.

 */
  public
  Category getInstance(String name) {
    return getInstance(name, defaultFactory);
  }

 /**
     Return a new category instance named as the first parameter using
     <code>factory</code>.
     
     <p>If a category of that name already exists, then it will be
     returned.  Otherwise, a new category will be instantiated by the
     <code>factory</code> parameter and linked with its existing
     ancestors as well as children.
     
     @param name The name of the category to retrieve.
     @param factory The factory that will make the new category instance.

 */
  public
  Category getInstance(String name, CategoryFactory factory) {
    //System.out.println("getInstance("+name+") called.");
    CategoryKey key = new CategoryKey(name);    
    // Synchronize to prevent write conflicts. Read conflicts (in
    // getChainedLevel method) are possible only if variable
    // assignments are non-atomic.
    Category category;
    
    synchronized(ht) {
      Object o = ht.get(key);
      if(o == null) {
	category = factory.makeNewCategoryInstance(name);
	category.setHierarchy(this);
	ht.put(key, category);      
	updateParents(category);
	return category;
      } else if(o instanceof Category) {
	return (Category) o;
      } else if (o instanceof ProvisionNode) {
	//System.out.println("("+name+") ht.get(this) returned ProvisionNode");
	category = factory.makeNewCategoryInstance(name);
	category.setHierarchy(this); 
	ht.put(key, category);
	updateChildren((ProvisionNode) o, category);
	updateParents(category);	
	return category;
      }
      else {
	// It should be impossible to arrive here
	return null;  // but let's keep the compiler happy.
      }
    }
  }


  /**
     Returns all the currently defined categories in this hierarchy as
     an {@link java.util.Enumeration Enumeration}.

     <p>The root category is <em>not</em> included in the returned
     {@link Enumeration}.  */
  public
  Enumeration getCurrentCategories() {
    // The accumlation in v is necessary because not all elements in
    // ht are Category objects as there might be some ProvisionNodes
    // as well.
    Vector v = new Vector(ht.size());
    
    Enumeration elems = ht.elements();
    while(elems.hasMoreElements()) {
      Object o = elems.nextElement();
      if(o instanceof Category) {
	v.addElement(o);
      }
    }
    return v.elements();
  }

  /**
     Get the renderer map for this hierarchy.
  */
  public
  RendererMap getRendererMap() {
    return rendererMap;
  }


  /**
     Get the root of this hierarchy.
     
     @since 0.9.0
   */
  public
  Category getRoot() {
    return root;
  }

  /**
     @deprecated See {@link #getEnable} for similar functionality.
   */

  public
  boolean isDisabled(int level) {    
    return enableInt >  level;
  }

  /**
     @deprecated Deprecated with no replacement.
  */
  public
  void overrideAsNeeded(String override) {
    LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
  }

  /**
     Reset all values contained in this hierarchy instance to their
     default.  This removes all appenders from all categories, sets
     the level of all non-root categories to <code>null</code>,
     sets their additivity flag to <code>true</code> and sets the level
     of the root category to {@link Level#DEBUG DEBUG}.  Moreover,
     message disabling is set its default "off" value.

     <p>Existing categories are not removed. They are just reset.

     <p>This method should be used sparingly and with care as it will
     block all logging until it is completed.</p>

     @since 0.8.5 */
  public
  void resetConfiguration() {

    getRoot().setLevel(Level.DEBUG);
    root.setResourceBundle(null);
    enableAll();
    
    // the synchronization is needed to prevent JDK 1.2.x hashtable
    // surprises
    synchronized(ht) {    
      shutdown(); // nested locks are OK    
    
      Enumeration cats = getCurrentCategories();
      while(cats.hasMoreElements()) {
	Category c = (Category) cats.nextElement();
	c.setLevel(null);
	c.setAdditivity(true);
	c.setResourceBundle(null);
      }
    }
    rendererMap.clear();
  }

  /**
     Set the default CategoryFactory instance.

     @since 1.1
   */
  public void setCategoryFactory(CategoryFactory factory) {
    if (factory != null) {
      defaultFactory = factory;
    }
  }

  /**
     Does mothing.
 
     @deprecated Deprecated with no replacement.
   */
  public
  void setDisableOverride(String override) {
    LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");    
  }

  /**
     Shutting down a hierarchy will <em>safely</em> close and remove
     all appenders in all categories including the root category.
     
     <p>Some appenders such as {@link org.apache.log4j.net.SocketAppender}
     and {@link AsyncAppender} need to be closed before the
     application exists. Otherwise, pending logging events might be
     lost.

     <p>The <code>shutdown</code> method is careful to close nested
     appenders before closing regular appenders. This is allows
     configurations where a regular appender is attached to a category
     and again to a nested appender.
     

     @since 1.0 */
  public 
  void shutdown() {
    Category root = getRoot();    

    // begin by closing nested appenders
    root.closeNestedAppenders();

    synchronized(ht) {
      Enumeration cats = this.getCurrentCategories();
      while(cats.hasMoreElements()) {
	Category c = (Category) cats.nextElement();
	c.closeNestedAppenders();
      }

      // then, remove all appenders
      root.removeAllAppenders();
      cats = this.getCurrentCategories();
      while(cats.hasMoreElements()) {
	Category c = (Category) cats.nextElement();
	c.removeAllAppenders();
      }      
    }
  }


  /**
     This method loops through all the *potential* parents of
     'cat'. There 3 possible cases:

     1) No entry for the potential parent of 'cat' exists

        We create a ProvisionNode for this potential parent and insert
        'cat' in that provision node.

     2) There entry is of type Category for the potential parent.

        The entry is 'cat's nearest existing parent. We update cat's
        parent field with this entry. We also break from the loop
        because updating our parent's parent is our parent's
        responsibility.
	 
     3) There entry is of type ProvisionNode for this potential parent.

        We add 'cat' to the list of children for this potential parent.
   */
  final
  private
  void updateParents(Category cat) {
    String name = cat.name;
    int length = name.length();
    boolean parentFound = false;
    
    //System.out.println("UpdateParents called for " + name);
    
    // if name = "w.x.y.z", loop thourgh "w.x.y", "w.x" and "w", but not "w.x.y.z" 
    for(int i = name.lastIndexOf('.', length-1); i >= 0; 
	                                 i = name.lastIndexOf('.', i-1))  {
      String substr = name.substring(0, i);

      //System.out.println("Updating parent : " + substr);
      CategoryKey key = new CategoryKey(substr); // simple constructor
      Object o = ht.get(key);
      // Create a provision node for a future parent.
      if(o == null) {
	//System.out.println("No parent "+substr+" found. Creating ProvisionNode.");
	ProvisionNode pn = new ProvisionNode(cat);
	ht.put(key, pn);
      } else if(o instanceof Category) {
	parentFound = true;
	cat.parent = (Category) o;
	//System.out.println("Linking " + cat.name + " -> " + ((Category) o).name);
	break; // no need to update the ancestors of the closest ancestor
      } else if(o instanceof ProvisionNode) {
	((ProvisionNode) o).addElement(cat);
      } else {
	Exception e = new IllegalStateException("unexpected object type " + 
					o.getClass() + " in ht.");
	e.printStackTrace();			   
      }
    }
    // If we could not find any existing parents, then link with root.
    if(!parentFound) 
      cat.parent = root;
  }

  /** 
      We update the links for all the children that placed themselves
      in the provision node 'pn'. The second argument 'cat' is a
      reference for the newly created Category, parent of all the
      children in 'pn'

      We loop on all the children 'c' in 'pn':

         If the child 'c' has been already linked to a child of
         'cat' then there is no need to update 'c'.

	 Otherwise, we set cat's parent field to c's parent and set
	 c's parent field to cat.

  */
  final
  private
  void updateChildren(ProvisionNode pn, Category cat) {
    //System.out.println("updateChildren called for " + cat.name);
    final int last = pn.size();

    for(int i = 0; i < last; i++) {
      Category c = (Category) pn.elementAt(i);
      //System.out.println("Updating child " +p.name);

      // Unless this child already points to a correct (lower) parent,
      // make cat.parent point to c.parent and c.parent to cat.
      if(!c.parent.name.startsWith(cat.name)) {
	cat.parent = c.parent;
	c.parent = cat;      
      }
    }
  }    

}


