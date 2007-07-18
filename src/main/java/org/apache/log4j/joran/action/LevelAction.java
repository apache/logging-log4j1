package org.apache.log4j.joran.action;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.ErrorItem;

import org.xml.sax.Attributes;

import java.lang.reflect.Method;


public class LevelAction extends Action {
 
  static final String VALUE_ATTR = "value";
  static final String CLASS_ATTR = "class";
  static final String INHERITED = "INHERITED";
  static final String NULL = "NULL";
  static final String EMPTY_STR = "";
  static final Class[] ONE_STRING_PARAM = new Class[] { String.class };

  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    Object o = ec.peekObject();

    if (!(o instanceof Logger)) {
      getLogger().warn("Could not find a logger at the top of execution stack.");
      ec.addError(
        new ErrorItem(
          "For element <level>, could not find a logger at the top of execution stack."));

      return;
    }

    Logger l = (Logger) o;

    String loggerName = l.getName();

    String levelStr = attributes.getValue(VALUE_ATTR);
    getLogger().debug(
      "Encapsulating logger name is [" + loggerName + "], levelvalue is  ["
      + levelStr + "].");

    if (
      INHERITED.equalsIgnoreCase(levelStr) || NULL.equalsIgnoreCase(levelStr)) {
      l.setLevel(null);
    } else {
      String className = attributes.getValue(CLASS_ATTR);

      if ((className == null) || EMPTY_STR.equals(className)) {
        l.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
      } else {
        getLogger().debug("Desired Level sub-class: [" + className + ']');

        try {
          Class clazz = Loader.loadClass(className);
          Method toLevelMethod = clazz.getMethod("toLevel", ONE_STRING_PARAM);
          Level pri =
            (Level) toLevelMethod.invoke(null, new Object[] { levelStr });
          l.setLevel(pri);
        } catch (Exception oops) {
          getLogger().error(
            "Could not create level [" + levelStr
            + "]. Reported error follows.", oops);

          return;
        }
      }
    }

    getLogger().debug(loggerName + " level set to " + l.getLevel());
  }

  public void finish(ExecutionContext ec) {
  }

  public void end(ExecutionContext ec, String e) {
  }
}
