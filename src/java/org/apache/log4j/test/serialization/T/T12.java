
import java.io.*;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Hashtable;


// String categoryName
// String ndc
// boolan ndcLookupRequired
// String renderedMessage
// String threadName
// long timeStamp

// LocationInfo
// ThrowableInformation ti.
public class T12 {

  public
  byte[] serialize(Hashtable ht) {
    try {
      Category category = Category.getInstance((String) ht.get("categoryName"));
      

      LoggingEvent event = new LoggingEvent("org.apache.log4j.Category", 
					    category, 
					    Priority.toPriority((String)ht.get("priorityStr")),
					    ht.get("message"), 
					    (Throwable) ht.get("throwable"));
      event.getThreadName();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(event);
      oos.flush();
      return baos.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public
  Hashtable deserialize(byte[] buf) {
    try {
      System.out.println("deserialize called.");
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      ObjectInputStream si = new ObjectInputStream(bais);  
      LoggingEvent event = (LoggingEvent)  si.readObject();	    
      System.out.println("Desrialization looks successful.");

      return eventToHashtable(event);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  Hashtable eventToHashtable(LoggingEvent event) {
    Hashtable ht = new Hashtable();
    ht.put("categoryName", event.categoryName);
    ht.put("renderedMessage", event.getRenderedMessage());
    ht.put("priorityStr", event.level.toString());
    return ht;
  }
   
}

