
package org.apache.log4j.helpers;

import java.util.Hashtable;

public class ITLContext {

  static final int HT_SIZE = 7;

  boolean java1;
  
  Object tlm;
  
  public
  ITLContext() {
    java1 = Loader.isJava1();
    if(!java1) {
      tlm = new ThreadLocalMap();
    }
  }

  public
  void put(String key, Object o) {
    if(java1) {
      return;
    } else {
      Hashtable ht = (Hashtable) ((ThreadLocalMap)tlm).get();
      if(ht == null) {
	ht = new Hashtable(HT_SIZE);
	((ThreadLocalMap)tlm).set(ht);
      }    
      ht.put(key, o);
    }
  }
  

  public
  Object get(String key) {
    if(java1) {
      return null;
    } else {       
      Hashtable ht = (Hashtable) ((ThreadLocalMap)tlm).get();
      if(ht != null) {
	return ht.get(key);
      } else {
	return null;
      }
    }
  }



}
