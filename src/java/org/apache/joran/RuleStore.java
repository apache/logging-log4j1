package org.apache.joran;

import java.util.List;

public interface RuleStore {

  public void addRule(Pattern pattern, Action action);
  
  public List matchActions(Pattern pattern);
}
