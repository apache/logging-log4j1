/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.lbel;


import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;


public class TokenStream  {
	
  // keywordMap contains a map of keywords
  // key = keyword string
  // value = token corresponding to the keyword
  private static Map keywordMap = new HashMap();
  
  static {
  	keywordMap.put("true", new Token(Token.TRUE));
  	keywordMap.put("false", new Token(Token.FALSE));
  	keywordMap.put("not", new Token(Token.NOT));
  	keywordMap.put("and", new Token(Token.AND));
  	keywordMap.put("or", new Token(Token.OR));
  	keywordMap.put("childof", new Token(Token.OPERATOR, "childof"));
    keywordMap.put("logger", new Token(Token.LOGGER, "logger"));
    keywordMap.put("message", new Token(Token.MESSAGE, "message"));
    keywordMap.put("level", new Token(Token.LEVEL, "level"));
    keywordMap.put("thread", new Token(Token.THREAD, "thread"));
    keywordMap.put("property", new Token(Token.PROPERTY, "property"));
    keywordMap.put("date", new Token(Token.DATE, "date"));
  }
  
  StreamTokenizer tokenizer;
	int token;
  Token current;
	
  
  public TokenStream(Reader reader) {
  	tokenizer = new StreamTokenizer(reader);
  	tokenizer.resetSyntax();
  	tokenizer.whitespaceChars(' ', ' ');
  	tokenizer.whitespaceChars('\t', '\t');
  	tokenizer.whitespaceChars('\n', '\n');
  	tokenizer.whitespaceChars('\r', '\r');
  	
  	tokenizer.wordChars('a', 'z');
  	tokenizer.wordChars('A', 'Z');
  	tokenizer.wordChars('0', '9');

  	tokenizer.quoteChar('"');
  	tokenizer.quoteChar('\'');
  	tokenizer.parseNumbers();
  }

  public Token getCurrent() {
    return current;
  }

  public void next() throws IOException, ScanError {
  	int token2;
  	
  	if(token != StreamTokenizer.TT_EOF) {
  		token = tokenizer.nextToken();
  		switch(token) {
  		case	StreamTokenizer.TT_EOF:
  			current = new Token(Token.EOF);
  			break;
  		case StreamTokenizer.TT_NUMBER:
   			double nval = tokenizer.nval;
		    current = new Token(Token.NUMBER, new Long((long) nval));
		    break;
  		case StreamTokenizer.TT_WORD:
  			String key = tokenizer.sval;
  		  Token result = (Token) keywordMap.get(key.toLowerCase());
  		  if(result != null) {
  		  	current = result;
  		  } else {
  		  	current = new Token(Token.LITERAL, tokenizer.sval);
  		  }
        break;  		
  		case '"':
  	  case '\'':
  		  current = new Token(Token.LITERAL, tokenizer.sval);
  		  break;
  		case '>':
   			token2 = tokenizer.nextToken();
  			if(token2 == '=') {
  			  current = new Token(Token.OPERATOR, ">=");		
  			} else {
  			  current = new Token(Token.OPERATOR, ">");
  			  tokenizer.pushBack();
  			}
        break;
  		case '<':
  			 token2 = tokenizer.nextToken();
  			if(token2 == '=') {
  			  current = new Token(Token.OPERATOR, "<=");		
  			} else {
  			  current = new Token(Token.OPERATOR, "<");
  			  tokenizer.pushBack();
  			}
        break;
  		case '=':
			  current = new Token(Token.OPERATOR, "=");		
        break; 
  		case '~':
			  current = new Token(Token.OPERATOR, "~");		
        break; 
      case '!':	 
      	token2 = tokenizer.nextToken();
			  if(token2 == '=') {
			    current = new Token(Token.OPERATOR, "!=");		
			  } else if (token2 == '~') {
			    current = new Token(Token.OPERATOR, "!~");
			    tokenizer.pushBack();
			  } else {
			  	throw new ScanError("Unrecogized token "+token+". The '!' character must be followed by = or '~'");
			  }
      break;
      case '(':
      	current = new Token(Token.LP);
      	break;
      case ')':
      	current = new Token(Token.RP);
      	break;
  		default:
  		  	throw new ScanError("Unrecogized token ["+(char)token+"]");
  		}
  		
  	}
  }
}
