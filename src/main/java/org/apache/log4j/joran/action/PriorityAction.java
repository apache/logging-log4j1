package org.apache.log4j.joran.action;



import org.apache.log4j.joran.spi.ExecutionContext;
import org.xml.sax.Attributes;

/**
 * This action allows us to warn users that the Priority element has been
 * deprecated but is still accepted.
 * 
 * @author Ceki Gulcu
 */
public class PriorityAction extends LevelAction {
   public void begin(ExecutionContext ec, String name, Attributes attributes) {
     getLogger().warn("Priority element has been deprecated, please use <level> instead.");
     super.begin(ec, name, attributes);
  }
}
