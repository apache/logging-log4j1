package org.apache.log4j.test;

public class SpacePad {

  static String[] SPACES = {" ", "  ", "    ", "        ", //1,2,4,8 spaces
			    "                ", // 16 spaces
			    "                                " }; // 32

  static public void main(String[] args) {
    StringBuffer sbuf = new StringBuffer();

    for(int i = 0; i < 35; i++) {
      sbuf.setLength(0);
      sbuf.append("\"");
      spacePad(sbuf, i);
      sbuf.append("\"");
      System.out.println(sbuf.toString());
    }
    
    sbuf.setLength(0);
    sbuf.append("\"");
    spacePad(sbuf, 67);
    sbuf.append("\"");
    System.out.println(sbuf.toString());
    
  }
  static
  public
  void spacePad(StringBuffer sbuf, int length) {
    //LogLog.debug("Padding with " + length + " spaces.");
    while(length >= 32) {
      sbuf.append(SPACES[5]);
      length -= 32;
    }
    
    for(int i = 4; i >= 0; i--) {	
      if((length & (1<<i)) != 0) {
	sbuf.append(SPACES[i]);
      }
    }
  }
}
