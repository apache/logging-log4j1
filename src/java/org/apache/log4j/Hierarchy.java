/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

// WARNING This class MUST not have a static initiliazer that
// WARNING references the Category or RootCategory classes neither 
// WARNING directly nor indirectly.

// Contributors:
//                Luke Blanshard <luke@quiq.com>
//                Mario Schomburg - IBM Global Services/Germany

package org.apache.log4j;


import java.util.Hashtable;
import java.util.Enumeration;

import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.or.ObjectRenderer;

/**
   This class is specialized in retreiving categories by name and
   also maintaining the category hierarchy.

   <p><em>The casual user should never have to deal with this class
   firectly.</em> In fact, up until version 0.9.0, this class had
   default package access. 

   <p>The structure of the category hierachy is maintained by the
   {@link #getInstance} method. The hierrachy is such that children
   link to their parent but parents do not have any pointers to their
   children. Moreover, categories can be instantiated in any order, in
   particular decendant before ancestor.

   <p>In case a decendant is created before a particular ancestor,
   then it creates a provision node for the ancestor and adds itself
   to the provision node. Other decendants of the same ancestor add
   themselves to the previously created provision node.

   <p>See the code below for further details.
   
   @author Ceki G&uuml;lc&uuml; 

*/
public class Hierarchy {
  
  static 
  private
  CategoryFactory defaultFactory = new DefaultCategoryFactory();

  Hashtable ht;
  Category root;
  RendererMap rendererMap; 

  /**
     Create a new Category hierarchy.

     @param root The root of the new hierarchy.

   */
  public
  Hierarchy(Category root) {
    ht = new Hashtable();
    this.root = root;
    this.root.setHierarchy(this);
    rendererMap = new RendererMap();
  }

  /**
     Add an object renderer for a specific class.       
   */
  public
  void addRenderer(Class classToRender, ObjectRenderer or) {
    rendererMap.put(classToRender, or);
  }
  

  /**
     This call will clear all category definitions from the internal
     hashtable. Invoking this method will irrevocably mess up the
     category hiearchy.
     
     <p>You should <em>really</em> know what you are doing before
     invoking this method.

     @since 0.9.0 */
  public
  void clear() {
    //System.out.println("\n\nAbout to clear internal hash table.");
    ht.clear();
  }

  /**
     Check if the named category exists in the hirarchy. If so return
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
     Return a new category instance named as the first parameter using
     the default factory. 
     
     <p>If a category of that name already exists, then it will be
     returned.  Otherwise, a new category will be instantiated and
     lthen inked with its existing ancestors as well as children.
     
     @param name The name of the category to retreive.

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
     
     @param name The name of the category to retreive.
     @param factory The factory that will make the new category instance.

 */
  public
  Category getInstance(String name, CategoryFactory factory) {
    //System.out.println("getInstance("+name+") called.");
    CategoryKey key = new CategoryKey(name);    
    // Synchronize to prevent write conflicts. Read conflicts (in
    // getChainedPriority method) are possible only if variable
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
    
    // if name = "x.y.z", loop thourgh "x.y" and "x", but not "x.y.z"    
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
      }
      else if(o instanceof Category) {
	parentFound = true;
	cat.parent = (Category) o;
	//System.out.println("Linking " + cat.name + " -> " + ((Category) o).name);
	break;	
      }
      else if(o instanceof ProvisionNode) {
	((ProvisionNode) o).addElement(cat);
      }
      else {
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

	 Otherwise, we loop until we find the nearest parent of 'c'
	 (not excluding 'c') below 'cat' and nearest to 'cat'.

	 Say 'x' is this category. We set cat's parent field to x's
	 parent and set x's parent field to cat.

  */
  final
  private
  void updateChildren(ProvisionNode pn, Category cat) {
    //System.out.println("updateChildren called for " + cat.name);
    final int last = pn.size();

    childLoop:
    for(int i = 0; i < last; i++) {
      Category c = (Category) pn.elementAt(i);
      //System.out.println("Updating child " +p.name);

      // Skip this child if it already points to a correct (lower) parent.
      // In pre-0.8.4c versions we skipped this test. As a result, under certain
      // rare circumstances the while loop below would never exit.

      // Thanks to Mario Schomburg from IBM Global Services/Hannover for
      // identifying this problem.
      if(c.parent != null && c.parent.name.startsWith(cat.name)) {
	continue childLoop;
      }

      // Loop until c points to an *existing* category just below 'cat' in the
      // hierarchy.
      while(c.parent != null && c.parent.name.startsWith(cat.name)) {
	c = c.parent;
      }
      //System.out.println("1-Linking " + cat.name + " -> " +
      //      (p.parent != null ? p.parent.name : "null"));
      cat.parent = c.parent;

      //System.out.println("2-Linking " + p.name + " -> " +
      //      cat.name);
      c.parent = cat;      
    }
  }    

  /**
     Shutting down a hiearchy will <em>safely</em> close and remove
     all appenders in all the categories including root.
     
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
      Enumeration cats = Category.getCurrentCategories();
      while(cats.hasMoreElements()) {
	Category c = (Category) cats.nextElement();
	c.closeNestedAppenders();
      }

      // then, remove all appenders
      root.removeAllAppenders();
      cats = Category.getCurrentCategories();
      while(cats.hasMoreElements()) {
	Category c = (Category) cats.nextElement();
	c.removeAllAppenders();
      }      
    }
  }
}


