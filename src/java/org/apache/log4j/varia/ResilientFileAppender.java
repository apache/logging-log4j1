package org.apache.log4j.varia;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;

/**
   This appender is resilient against moving of the log output
   file. Before every write operation, it checks whether the target
   log file has been moved by an external process. If that is the
   case, a new output file is created.

   <p>This method of detecting externally triggered roll overs has the
   following disadvantages:

   <ul> 

   <li>It adds the overhead of calling File.exists method before each
   write.
    
   <li>It is not guaranteed to work on all environments. For example,
   on Windows NT, it is not possible to move an already open file.

   <li>Since there is no proper synchronization between the external
   process and the JVM doing the logging, race conditions are possible
   and can result in the loss of log records.

   </ul>

   For all these reasons, it is recommended that you use the {@link
   ExternallyRolledFileAppender}.

   @author Ceki G&uuml;lc&uuml;
   @since version 0.9.0 */
public class ResilientFileAppender extends FileAppender {


  File file;

  /**
     Thia default constructor does nothing but call its super-class
     constructor.  */
  public
  ResilientFileAppender() {    
  }

  
  /**
     This constructor does nothing but call its super-class constructor
     with the same parameters.  */
  public
  ResilientFileAppender(Layout layout, Writer writer) {
    super(layout, writer);
  }                    


  /**
     
   */
  protected
  boolean checkEntryConditions() {
    if(!super.checkEntryConditions()) {
      return false;
    }
    
    
    if((file != null) && !file.exists()) {
      // the file has been removed under our feet.
      // we call our super-classes' setFile method to close the current writer
      // reference and open a new file.
      file = new File(fileName);
      try {
	setFile(fileName);
      } catch(java.io.IOException e) {
	LogLog.error("setFile("+fileName+","+ fileAppend+") call failed.", e);
      }
    }
    return true;
  }


  public
  void setFile(String fileName, boolean append) throws IOException {
    super.setFile(fileName, append);
    file = new File(fileName);
  }


  protected
  void reset() {
    super.reset();
    file = null;
  }
  
}
