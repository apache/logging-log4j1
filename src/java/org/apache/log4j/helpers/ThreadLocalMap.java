
package org.apache.log4j.helpers;

import java.util.Hashtable;


final public class ThreadLocalMap extends InheritableThreadLocal {

  public
  final
  Object childValue(Object parentValue) {
    Hashtable ht = (Hashtable) parentValue;
    if(ht != null) {
      return ht.clone();
    } else {
      return null;
    }
  }
}
