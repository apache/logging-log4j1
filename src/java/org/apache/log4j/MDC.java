

package org.apache.log4j;

import java.util.Hashtable;

public class MDC {

  final static MappedContext context = new MappedContext();
  
  static final int HT_SIZE = 11;

  static
  public
  void put(String key, Object o) {
    Hashtable ht = getMap();
    ht.put(key, o);
  }
  
  static 
  public
  Object get(String key) {
    Hashtable ht = getMap();
    return ht.get(key);
  }

  private
  static
  Hashtable getMap() {
    Hashtable ht = (Hashtable) context.get();
    if(ht == null) {
      System.out.println("getMap creating new ht. [" + Thread.currentThread().getName()+
			 "]");
      ht = new Hashtable(HT_SIZE);
      context.set(ht);
    }
    return ht;
  }
}

class MappedContext extends InheritableThreadLocal {

  public
  Object childValue(Object parentValue) {
    Hashtable ht = (Hashtable) parentValue;
    System.out.println("childValue called. ["+Thread.currentThread().getName()+"]");
    return ht.clone();
  }

  public 
  void finalize() throws Throwable {
    System.out.println("finalize called. ["+Thread.currentThread().getName()+"]");
    super.finalize();
  }
}
