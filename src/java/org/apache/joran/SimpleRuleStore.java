package org.apache.joran;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SimpleRuleStore implements RuleStore {

  HashMap rules = new HashMap();
  
	public void addRule(Pattern pattern, Action action) {

		//System.out.println("pattern to add is:" + pattern + "hashcode:" + pattern.hashCode());
     List a4p = (List) rules.get(pattern);
     
     if(a4p == null) {
     	a4p = new ArrayList();
		  rules.put(pattern, a4p);
     }
     a4p.add(action);
	}

	public List matchActions(Pattern pattern) {
	
		//System.out.println("pattern to search for:" + pattern + ", hashcode: " + pattern.hashCode());
		//System.out.println("rules:" + rules);
		
		ArrayList a4p = (ArrayList) rules.get(pattern);
	 
	  if(a4p != null) {
	  	return 	a4p;
	  } else {
	  	
		  Iterator patternsIterator = rules.keySet().iterator();
		  int max = 0;
		  Pattern longestMatch = null;
		  while(patternsIterator.hasNext()) {
		  	Pattern p = (Pattern)  patternsIterator.next();
		  	if((p.size() > 1) && p.get(0).equals("*")) {
		  	  int r = pattern.tailMatch(p);
		  	  //System.out.println("tailMatch " +r);
		  	  if(r > max) {
		  	  	//System.out.println("New longest match "+p);
		  	  	max = r;
		  	  	longestMatch = p;
		  	  }
		  	}
		  }
		  
		  if(longestMatch != null) {
		    return (ArrayList) rules.get(longestMatch);
		  } else {
		  	return null;
		  }
	  }
	
	}

}
