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
import org.apache.log4j.Appender;
import org.apache.log4j.helpers.LogLog;

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

  Hashtable ht;
  Category root;
  
  int enableInt;
  Priority enable;

  boolean emittedNoAppenderWarning = false;
  boolean emittedNoResourceBundleWarning = false;  

  /**
     Create a new Category hierarchy.

     @param root The root of the new hierarchy.

   */
  public
  Hierarchy(Category root) {
    ht = new Hashtable();
    this.root = root;
    // Enable all priority levels by default.
    enable(Priority.ALL);
    this.root.setHierarchy(this);
  }

  /**
     Enable all logging priorities for this hierarchy. This is the
     default.
     
   */
  public
  void enableAll() {
    enable(Priority.ALL);
  }

  public 
  void enable(Priority p) {
    if(p != null) {
      enableInt = p.level;
      enable = p;
    }
  }

  /**
     Returns the string representation of the internal
     <code>enable</code> state.  

     @since 1.2
  */
  public
  Priority getEnable() {
    return enable;
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
  Category getInstance(String name) {
    //System.out.println("getInstance("+name+") called.");
    CategoryKey key = new CategoryKey(name);    
    // Synchronize to prevent write conflicts. Read conflicts (in
    // getChainedPriority method) are possible only if variable
    // assignments are non-atomic.
    Category category;
    
    synchronized(ht) {
      Object o = ht.get(key);
      if(o == null) {
	category = new Category(name);
	category.setHierarchy(this);
	ht.put(key, category);      
	updateParents(category);
	return category;
      } else if(o instanceof Category) {
	return (Category) o;
      } else if (o instanceof ProvisionNode) {
	category = new Category(name);
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
     Get the root of this hierarchy.
     
     @since 0.9.0
   */
  public
  Category getRoot() {
    return root;
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


