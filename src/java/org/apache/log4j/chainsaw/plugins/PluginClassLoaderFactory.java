
package org.apache.log4j.chainsaw.plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.helpers.LogLog;

/**
 * A factory class to create a Classloader that can refenerence jars/classes/resources
 * within a users plugin directory.
 * 
 * Currently a work in progress to see if this allows external jars required by
 * some 3rd party vendors for things like the JMSReceiver.
 *  
 * @author psmith
 *
 * 
 */
public class PluginClassLoaderFactory {

	/**
	 * @param urls
	 */
	private PluginClassLoaderFactory() {
	}
    
    /**
     * Creates a Classloader that will be able to access any of the classes found
     * in any .JAR file contained within the specified directory path, PLUS
     * the actual Plugin directory itself, so it acts like the WEB-INF/classes directory,
     * any class file in the directory will be accessible
     * 
     * @param pluginDirectory
     * @throws IllegalArgumentException if the pluginDirectory is null, does not exist, or cannot be read
     * @throws RuntimeException if turning a File into a URL failed, which would be very unexpected
     * @return
     */
    public static final ClassLoader create(File pluginDirectory) {
        if(pluginDirectory == null || !pluginDirectory.exists() || !pluginDirectory.canRead()) {
         throw new IllegalArgumentException("pluginDirectory cannot be null, and it must exist and must be readable");   
        }
        
        String[] strings = pluginDirectory.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".JAR");
			}});
        
      
        List list = new ArrayList();
        // add the plugin directory as a resource loading path
        try {
			list.add(pluginDirectory.toURL());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        if (strings !=null) {
			for (int i = 0; i < strings.length; i++) {
				String name = strings[i];
				File file = new File(pluginDirectory, name);
				try {
					list.add(file.toURL());
					LogLog.info("Added " + file.getAbsolutePath()
							+ " to Plugin class loader list");
				} catch (Exception e) {
					LogLog.error("Failed to retrieve the URL for file: "
							+ file.getAbsolutePath());
					throw new RuntimeException(e);
				}
			}
		}
        URL[] urls = (URL[]) list.toArray(new URL[list.size()]);
        return new URLClassLoader(urls);
    }

}
