package org.apache.log4j.lbel;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class TokenStreamTest extends TestCase {

	Token t;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public TokenStreamTest(String arg0) {
		super(arg0);
	}

	public void testSingleDigit() throws IOException, ScanError {
	  StringReader sr = new StringReader("9");
	  TokenStream ts = new TokenStream(sr);

	  assertNull(ts.getCurrent());
    ts.next();
	  
	  t = ts.getCurrent();
	  assertEquals(TokenStream.NUMBER, t.getType());
	  assertEquals(9, ((Long) t.getValue()).longValue());
	  
	  ts.next();
	  t = ts.getCurrent();
	  assertEquals(TokenStream.EOF, t.getType());
	}

	public void testLongerDigit() throws IOException, ScanError {
	  StringReader sr = new StringReader(" 980 ");
	  TokenStream ts = new TokenStream(sr);
    ts.next();
	  t = ts.getCurrent();
	  assertEquals(TokenStream.NUMBER, t.getType());
	  assertEquals(980, ((Long) t.getValue()).longValue());
	  
	  ts.next(); t = ts.getCurrent();
	  assertEquals(TokenStream.EOF, t.getType());
	}
	
	public void testComparison() throws IOException, ScanError {
	  StringReader sr = new StringReader(" time >= 17");
	  TokenStream ts = new TokenStream(sr);
    ts.next();
	  t = ts.getCurrent();
	  
	  assertEquals(TokenStream.IDENTIFIER, t.getType());
	  assertEquals("time", t.getValue());
	  
	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.OPERATOR, t.getType());
	  assertEquals(">=", t.getValue());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.NUMBER, t.getType());
	  assertEquals(17, ((Long) t.getValue()).longValue());
	  		
	  ts.next(); t = ts.getCurrent();
	  assertEquals(TokenStream.EOF, t.getType());
	}
	
	public void testFull() throws IOException, ScanError {
	  StringReader sr = new StringReader(" time >= 19 NOT \"hello world\" ");
	  TokenStream ts = new TokenStream(sr);
    ts.next();
	  t = ts.getCurrent();
	  
	  assertEquals(TokenStream.IDENTIFIER, t.getType());
	  assertEquals("time", t.getValue());
	  
	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.OPERATOR, t.getType());
	  assertEquals(">=", t.getValue());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.NUMBER, t.getType());
	  assertEquals(19, ((Long) t.getValue()).longValue());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.NOT, t.getType());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.IDENTIFIER, t.getType());
	  assertEquals("hello world", t.getValue());

	  		
	  ts.next(); t = ts.getCurrent();
	  assertEquals(TokenStream.EOF, t.getType());
	}
	
	public void testNoSpaceFull() throws IOException, ScanError {
	  StringReader sr = new StringReader(" time>=19 NOT \"hello world\" ");
	  TokenStream ts = new TokenStream(sr);
    ts.next();
	  t = ts.getCurrent();
	  
	  assertEquals(TokenStream.IDENTIFIER, t.getType());
	  assertEquals("time", t.getValue());
	  
	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.OPERATOR, t.getType());
	  assertEquals(">=", t.getValue());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.NUMBER, t.getType());
	  assertEquals(19, ((Long) t.getValue()).longValue());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.NOT, t.getType());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.IDENTIFIER, t.getType());
	  assertEquals("hello world", t.getValue());

	  		
	  ts.next(); t = ts.getCurrent();
	  assertEquals(TokenStream.EOF, t.getType());
	}
	
	public void testSingleQuote() throws IOException, ScanError {
	  StringReader sr = new StringReader(" logger ~ 'hello world' ");
	  TokenStream ts = new TokenStream(sr);
    ts.next();
	  t = ts.getCurrent();
	  
	  assertEquals(TokenStream.IDENTIFIER, t.getType());
	  assertEquals("logger", t.getValue());
	  
	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.OPERATOR, t.getType());
	  assertEquals("~", t.getValue());

	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.IDENTIFIER, t.getType());
	  assertEquals("hello world", t.getValue());

	  		
	  ts.next(); t = ts.getCurrent();
	  assertEquals(TokenStream.EOF, t.getType());
	}
	
	public void testTrueOrFalse() throws IOException, ScanError {
	  StringReader sr = new StringReader(" true OR false");
	  TokenStream ts = new TokenStream(sr);
   
	  ts.next(); t = ts.getCurrent();
	  assertEquals(TokenStream.TRUE, t.getType());
	  
	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.OR, t.getType());
	  
	  ts.next();  t = ts.getCurrent();
	  assertEquals(TokenStream.FALSE, t.getType());
	  		
	  ts.next(); t = ts.getCurrent();
	  assertEquals(TokenStream.EOF, t.getType());
	}
	
}
