package org.apache.log4j.helpers;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.Layout;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;
import java.text.DateFormat;
import java.util.Date;

// Contributors:   Nelson Minar <(nelson@monkey.org>
//                 Igor E. Poteryaev <jah@mail.ru>  
//                 Reinhard Deschler <reinhard.deschler@web.de> 

/**
   Most of the work of the {@link org.apache.log4j.PatternLayout} class
   is delegated to the PatternParser class.

   @author <a href=mailto:"cakalijp@Maritz.com">James P. Cakalic</a>
   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen       

   @since 0.8.2
*/
public class PatternParser {

  private static final char ESCAPE_CHAR = '%';
  
  private static final int LITERAL_STATE = 0;
  private static final int CONVERTER_STATE = 1;
  private static final int MINUS_STATE = 2;
  private static final int DOT_STATE = 3;
  private static final int MIN_STATE = 4;
  private static final int MAX_STATE = 5;


  static final int RELATIVE_TIME_CONVERTER = 2000;
  static final int THREAD_CONVERTER = 2001;
  static final int PRIORITY_CONVERTER = 2002;
  //static final int NDC_CONVERTER = 2003;
  static final int MESSAGE_CONVERTER = 2004;

  int state;
  protected StringBuffer currentLiteral = new StringBuffer(32);
  protected int patternLength;
  protected int i;
  PatternConverter head;
  PatternConverter tail;
  protected FormattingInfo formattingInfo = new FormattingInfo();
  protected String pattern;

  public
  PatternParser(String pattern) {
    this.pattern = pattern;
    patternLength =  pattern.length();    
    state = LITERAL_STATE;
  }

  private
  void  addToList(PatternConverter pc) {
    if(head == null) {
      head = tail = pc;
    } else {
      tail.next = pc;
      tail = pc;    
    }
  }

  private
  String extractOption() {
    if((i < patternLength) && (pattern.charAt(i) == '{')) {
      int end = pattern.indexOf('}', i);	
      if (end > i) {
	String r = pattern.substring(i + 1, end);
	i = end+1;
	return r;
      }
    }
    return null;
  }


  /**
     The option is expected to be in decimal and positive. In case of
     error, zero is returned.  */
  private
  int extractPrecisionOption() {
    String opt = extractOption();
    int r = 0;
    if(opt != null) {
      try {
	r = Integer.parseInt(opt);
	if(r <= 0) {
	    LogLog.error(opt + " <= 0");
	    r = 0;
	}
      }      
      catch (NumberFormatException e) {
	LogLog.error(opt+" invalid", e);
      }      
    }
    return r;    
  }
      
  public 
  PatternConverter parse() {
    char c;
    i = 0;
    while(i < patternLength) {
      c = pattern.charAt(i++);
      switch(state) {
      case LITERAL_STATE: 
        // In literal state, the last char is always a literal.
        if(i == patternLength) {
          currentLiteral.append(c);
          continue;
        }
        if(c == ESCAPE_CHAR) {      
          // peek at the next char. 
          switch(pattern.charAt(i)) {
          case ESCAPE_CHAR:
            currentLiteral.append(c);
            i++; // move pointer
            break;
          case 'n':
            currentLiteral.append(Layout.LINE_SEP);
            i++; // move pointer
            break;
          default:
            if(currentLiteral.length() != 0) {
              addToList(new LiteralPatternConverter(
                                                  currentLiteral.toString()));
              //LogLog.debug("Parsed LITERAL converter: \"" 
              //           +currentLiteral+"\".");
            }
            currentLiteral.setLength(0); 
            currentLiteral.append(c); // append %
            state = CONVERTER_STATE;
            formattingInfo.reset();
          }
        }
        else {
          currentLiteral.append(c);
        }
        break;
      case CONVERTER_STATE:
	currentLiteral.append(c);
	switch(c) {
	case '-':
	  formattingInfo.leftAlign = true;
	  break;
	case '.':
	  state = DOT_STATE;
	  break;
	default:
	  if(c >= '0' && c <= '9') {
	    formattingInfo.min = c - '0';
	    state = MIN_STATE;
	  }
	  else 
	    finalizeConverter(c);	    
	} // switch
	break;
      case MIN_STATE:
	currentLiteral.append(c);
	if(c >= '0' && c <= '9') 
	  formattingInfo.min = formattingInfo.min*10 + (c - '0');
	else if(c == '.')
	  state = DOT_STATE;
	else {
	  finalizeConverter(c);
	}
	break;
      case DOT_STATE:
	currentLiteral.append(c);
	if(c >= '0' && c <= '9') {
	  formattingInfo.max = c - '0';
	   state = MAX_STATE;
	}
	else {
	  LogLog.error("["+c+"] at pos "+i+" not a digit");
	  state = LITERAL_STATE;
	}
	break;
      case MAX_STATE:
	currentLiteral.append(c);
	if(c >= '0' && c <= '9') 
	  formattingInfo.max = formattingInfo.max*10 + (c - '0');
	else {
	  finalizeConverter(c);
	  state = LITERAL_STATE;
	}
	break;
      } // switch
    } // while
    if(currentLiteral.length() != 0) {
      addToList(new LiteralPatternConverter(currentLiteral.toString()));
      //LogLog.debug("Parsed LITERAL converter: \""+currentLiteral+"\".");
    }
    return head;
  }

  protected
  void finalizeConverter(char c) {
    PatternConverter pc = null;
    switch(c) {
    case 'c':
      pc = new CategoryPatternConverter(formattingInfo,
					extractPrecisionOption());	
      //LogLog.debug("CATEGORY converter.");
      //formattingInfo.dump();      
      currentLiteral.setLength(0);
      break;
    case 'm':
      pc = new BasicPatternConverter(formattingInfo, MESSAGE_CONVERTER);
      //LogLog.debug("MESSAGE converter.");
      //formattingInfo.dump();      
      currentLiteral.setLength(0);
      break;
    case 'p':
      pc = new BasicPatternConverter(formattingInfo, PRIORITY_CONVERTER);
      //LogLog.debug("PRIORITY converter.");
      //formattingInfo.dump();
      currentLiteral.setLength(0);
      break;
    case 'r':
      pc = new BasicPatternConverter(formattingInfo, 
					 RELATIVE_TIME_CONVERTER);
      //LogLog.debug("RELATIVE time converter.");
      //formattingInfo.dump();
      currentLiteral.setLength(0);
      break;
    case 't':
      pc = new BasicPatternConverter(formattingInfo, THREAD_CONVERTER);
      //LogLog.debug("THREAD converter.");
      //formattingInfo.dump();      
      currentLiteral.setLength(0);
      break;
      /*case 'u':
      if(i < patternLength) {
	char cNext = pattern.charAt(i);
	if(cNext >= '0' && cNext <= '9') {
	  pc = new UserFieldPatternConverter(formattingInfo, cNext - '0');
	  LogLog.debug("USER converter ["+cNext+"].");
	  formattingInfo.dump();      
	  currentLiteral.setLength(0);
	  i++;
	}
	else 
	  LogLog.error("Unexpected char" +cNext+" at position "+i);
      }
      break;*/
      //case 'x':
      //pc = new BasicPatternConverter(formattingInfo, NDC_CONVERTER);
      //currentLiteral.setLength(0);
      //break;
    default:
      LogLog.error("Unexpected ["+c+"] at "+i+" in ConvPattern");
      pc = new LiteralPatternConverter(currentLiteral.toString());
      currentLiteral.setLength(0);
    }
    
    addConverter(pc);
  }

  protected
  void addConverter(PatternConverter pc) {
    currentLiteral.setLength(0);
    // Add the pattern converter to the list.
    addToList(pc);
    // Next pattern is assumed to be a literal.
    state = LITERAL_STATE;
    // Reset formatting info
    formattingInfo.reset();
  }

  // ---------------------------------------------------------------------
  //                      PatternConverters
  // ---------------------------------------------------------------------
    
  private static class BasicPatternConverter extends PatternConverter {
    int type;
    
    BasicPatternConverter(FormattingInfo formattingInfo, int type) {
      super(formattingInfo);     
      this.type = type;
    }

    public
    String convert(LoggingEvent event) {
      switch(type) {
      case RELATIVE_TIME_CONVERTER: 
	return (Long.toString(event.timeStamp - LoggingEvent.getStartTime()));
      case THREAD_CONVERTER:
	return event.getThreadName();
      case PRIORITY_CONVERTER:
	return event.priority.toString();
	//case NDC_CONVERTER:  
	//return event.getNDC();
      case MESSAGE_CONVERTER: {
	return event.message;
      }
      default: return null;
      }
    }
  }
  
  private static class LiteralPatternConverter extends PatternConverter {
    private String literal;
  
    LiteralPatternConverter(String value) {
      literal = value;
    }

    public
    final
    void format(StringBuffer sbuf, LoggingEvent event) {
      sbuf.append(literal);
    }
    
    public    
    String convert(LoggingEvent event) {
      return literal;
    }
  }

  private static  class CategoryPatternConverter extends PatternConverter {
    int precision;
    
    CategoryPatternConverter(FormattingInfo formattingInfo, int precision) {
      super(formattingInfo);
      this.precision =  precision;      
    }

    //String getFullyQualifiedName(LoggingEvent event) {
    //return 
    //}
    
    public
    String convert(LoggingEvent event) {
      String n = event.categoryName;
      if(precision <= 0)
	return n;
      else {
	int len = n.length();

	// We substract 1 from 'len' when assigning to 'end' to avoid out of
	// bounds exception in return r.substring(end+1, len). This can happen if
	// precision is 1 and the category name ends with a dot. 
	int end = len -1 ;
	for(int i = precision; i > 0; i--) {	  
	  end = n.lastIndexOf('.', end-1);
	  if(end == -1)
	    return n;
	}
	return n.substring(end+1, len);
      }      
    }
  }
}

