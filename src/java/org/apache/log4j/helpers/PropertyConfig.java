package org.log4j.helpers;

import java.net.URL;
import java.util.Properties;
import org.log4j.helpers.LogLog;
import org.log4j.spi.PropertyConfiguratorInterface;

/**
   Loads a log4j properties configuration file and decides which
   configurator implementation should handle it. The "log4j.configurator"
   property, if present, specifies the name of the
   {@link org.log4j.spi.PropertyConfiguratorInterface} class to be
   used to interpret the configuration. The default is to use
   {@link org.log4j.PropertyConfigurator}.
   
   <p>Example use (e.g. in file log4j.properties):
   <pre>
   log4j.configurator=com.example.log4j.MyPropConfigurator
   </pre>
   
   @see org.log4j.spi.PropertyConfiguratorInterface
   @see org.log4j.PropertyConfigurator
   @since 0.9.2
   @author Anders Kristensen
 */
public abstract class PropertyConfig {
    
  /**
     The value of this property is the name of the configurator class
     which is used to configure log4j given a Properties instance.
     If this is not present, PropertyConfigurator itself is used.
     
     @since 0.9.2
   */
  static final String CONFIGURATOR          = "log4j.configurator";
  static final String DEFAULT_CONFIGURATOR  = "org.log4j.PropertyConfigurator";

  public
  static
  void configure(URL configURL) {
    Properties props = new Properties();
    LogLog.debug("Reading configuration from URL " + configURL);
    try {
      props.load(configURL.openStream());
    }
    catch (java.io.IOException e) {
      LogLog.error("Could not read configuration file from URL [" + configURL 
		   + "].", e);
      LogLog.error("Ignoring configuration file [" + configURL +"].");
      return;
    }
    
    String clazz = props.getProperty(CONFIGURATOR, DEFAULT_CONFIGURATOR);
    try {
      Class c = Class.forName(clazz);
      PropertyConfiguratorInterface configurator =
          (PropertyConfiguratorInterface) c.newInstance();
      configurator.doConfigure(props);
    } catch (ClassNotFoundException e) {
      LogLog.error("Configurator class not found: " + clazz);
    } catch (InstantiationException e) {
      LogLog.error("Configurator class not instantiable");
    } catch (IllegalAccessException e) {
      LogLog.error("Configurator class or initializer is not accessible.");
    } catch (ClassCastException e) {
      LogLog.error("Configurator class doesn't implement PropertyConfiguratorInterface");
    }
  }
}