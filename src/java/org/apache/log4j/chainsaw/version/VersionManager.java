package org.apache.log4j.chainsaw.version;


/**
 * @author psmith
 *
 */
public class VersionManager
{

    private static final VersionManager instance = new VersionManager();
    
    private static final String VERSION_INFO = "1.99.99 (26 July 2004)";
    
    public static final VersionManager getInstance() {
        return instance;
    }
    
    public String getVersionNumber() {
        return VERSION_INFO;
    }
    
    /**
     * 
     */
    private VersionManager()
    {
    }

}
