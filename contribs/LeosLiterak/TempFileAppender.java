 package org.apache.log4j;
 
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorHandler;

/**
   TempFileAppender creates new unique file for each logging statement.
 
   @author <a HREF="mailto:leos.literak@12snap.com">Leos Literak</a>
   @author Ceki G&uuml;lc&uuml;
 
*/
public class TempFileAppender extends AppenderSkeleton {
 
  /**
     A string constant used in naming the option for setting the
     directory where the log files will be created. Current value 
     of this string constant is <b>Path</b>. java.io.tmpdir directory
     will be used, if ommited.
   */
  static final public String PATH_OPTION = "Path";
  
  /**
     The default path is actual directory.
  */
  protected String path = null;
 
  /**
     A string constant used in naming the option for setting the
     prefix of the log files. It has to have at least 3 characters!
     Current value of this string constant is <b>Prefix</b>.
   */
  static final public String PREFIX_OPTION = "Prefix";
  
  /**
     The default path is actual directory.
  */
  protected String prefix = "l4j_";
 
  /**
     A string constant used in naming the option for setting the
     suffix of the log files. Current value of this string constant 
     is <b>Suffix</b>.
   */
  static final public String SUFFIX_OPTION = "Suffix";
  
  /**
     The default path is actual directory.
  */
  protected String suffix = ".tmp";
  
  /**
     Default dir
  */
  
  protected File dir = null;




  /**
     The default constructor simply calls its parent's constructor. 
  */
  public TempFileAppender() {
      super();
  }
 
  /**
     Retuns the option names for this component
  */
  public String[] getOptionStrings() {
      return OptionConverter.concatanateArrays(super.getOptionStrings(),
                 new String[] {PATH_OPTION,PREFIX_OPTION,SUFFIX_OPTION});
  }  

  /**
     Set TempFileAppender specific options.
 
     The recognized options are <b>Path</b>, <b>Prefix</b> and <b>Suffix</b>,
     i.e. the values of the string constants {@link #PATH_OPTION}, 
     {@link #PREFIX_OPTION} and respectively {@link #SUFFIX_OPTION}. 
     The options of the super class {@link AppenderSkeleton} are also 
     recognized.
  */
  
  public void setOption(String key, String value) {
      super.setOption(key, value);
      if(key.equalsIgnoreCase(PATH_OPTION)) {
	  path = value;
	  if(path==null) {
              errorHandler.error("Path cannot be empty!",null,0);
	  }

	  dir = new File(path);
	  if(!(dir.exists() && dir.isDirectory() && dir.canWrite())) {
              errorHandler.error("Cannot write to directory " + path + "!",null,0);
	  }
      }
      else if(key.equalsIgnoreCase(PREFIX_OPTION)) {
          if(value!=null && value.length()>=3) {
	      prefix = value;
	  } else {
              errorHandler.error("Prefix cannot be shorter than 3 characters!",
	                         null,0);
	  }
      }
      else if(key.equalsIgnoreCase(SUFFIX_OPTION)) {
          if(value!=null && value.length()>=1) {
	      suffix = value;
	  } else {
              errorHandler.error("Suffix cannot be empty!",null,0);
	  }
      }
  }

  /**
     This method is called by {@link AppenderSkeleton#doAppend}
     method.
 
     <p>Whenever this method is called, new unique file is created
     with specified prefix and suffix. The file is closed afterwards.
 
     <p>The format of the output will depend on this appender's
     layout.
 
  */ 
  public void append(LoggingEvent event) { 
      if(!checkEntryConditions()) {
          return;
      }
      subAppend(event);
  }
 
  /**
     This method determines if there is a sense in attempting to append.
  */
  protected boolean checkEntryConditions() {
      return true;
  }   

  /**
     This method does actual writing
  */
  protected void subAppend(LoggingEvent event) {
      try {
          File tmp = File.createTempFile(prefix,suffix,dir);
	  Writer out = new BufferedWriter(new FileWriter(tmp));
	  out.write(event.message);
	  out.close();
 /* this Appender is not supposed to be used for logging of Exceptions */
      } catch (Exception e) {
          errorHandler.error("Error during creation of temporary File!",e,1);
      }
  }
  
  public boolean requiresLayout() {
      return false;
  }
  
  public void close() {
  /* nothing to do */
  }
} 
/*
 * @author $Author$
 * @version $Revision$
 * @since $Date$
 *
 * $Log$
 * Revision 1.1  2001/04/20 17:38:31  ceki
 * Added LeosLiterak's TempFileAppender.java
 *
*/
