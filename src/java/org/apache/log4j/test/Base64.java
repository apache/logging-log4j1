//      Copyright 1996-1999, International Business Machines 
//      Corporation. All Rights Reserved.


package org.apache.log4j.test;

class Base64 {

  final static int MAX_LINE = 76;
  
  static byte[] asciiEncoding =
    //A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P   Q  
    {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
    //R  S ...                        Z   a   b   c    d    e    f    g
    82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103,
    //h    i    j    k    l    m    n    o    p    q    r    s   t     u
    104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117,
    //v    w    x    y    z   0   1   2   3   4   5   6   7   8   9   +   /
    118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47}; 

  static char[] charEnc = new char[64];

  static {
    for(int i = 0; i < 26; i ++) {
      charEnc[i] = (char) ('A' + i);
      charEnc[i+26] = (char) ('a' + i);
    }
    for(int i = 0; i < 10; i ++) {
      charEnc[i+52] = (char) ('0' + i);
    }
    charEnc[62] = '+';
    charEnc[63] = '/';
  }

  
  
  static
  String toString(byte[] inbuf) {
    return toString(inbuf, 0, inbuf.length);
  }
  /**
     
   */
  static
  String toString(byte[] inbuf, int offset, int length) {
    
    StringBuffer output = new StringBuffer((length)*4/3 + 1);

    int in;
    char[] out = new char[4];
    
    int i = offset;
    boolean ended = false;
    int last = offset + length;
    int j;
    int bitsRead;
        
    while(!ended) {
      in = 0;
      bitsRead = 0;
      for(j = 0; j < 3; j++) {
	if(i == last) {
	  ended = true;
	  break;
	}
	in = (in << 8) | (inbuf[i++] & 0xFF);
	bitsRead += 8;
      }

      while(bitsRead >= 6) {
	bitsRead -= 6;
	output.append(charEnc[(in >>> bitsRead) & 0x3F]);
      }

      if(bitsRead == 4) {
	output.append(charEnc[(in & 0x0F) << 2]);
	output.append("=");
      }
      else if (bitsRead == 2) {
	output.append(charEnc[(in & 0x03) << 4]);
	output.append("==");
      }
    }
    return output.toString();
  }


  public static void main(String[] args) {

    byte[] inbuf = new byte[MAX_LINE];

    while(true) {
      try {
	int read = System.in.read(inbuf, 0, MAX_LINE);
	if(read == -1) break;
	System.out.println("Read " + read + " chars.");
	System.out.println(Base64.toString(inbuf, 0, read));
      }
      catch (Exception e) {
	System.out.println("Exception " + e);
      }

    }
  }
}
