package org.apache.log4j.lbel;

class Node {
	
	public static final int FALSE = 1;
	public static final int TRUE = 2;
  public static final int COMPARATOR = 3;

	public static final int OR = 1000;
	public static final int AND = 1100;
	public static final int NOT = 1200;
	
  Node left;
	Node right;

  final int type;
  final Object value;
  
  Node(int type) {
  	this(type, null);
  }
  
  Node(int type, Object value) {
  	this.type = type;
  	this.value = value;
  }
  
  public int getType() {
	  return type;
  }
  
  public Object getValue() {
  	return value;
  }

	public Node getLeft() {
		return left;
	}
	public void setLeft(Node leftSide) {
		if(this.left != null) {
		  throw new IllegalStateException("The left side already set. (old="+this.left+", new="+leftSide+")");
		}
		this.left = leftSide;
	}

  public Node getRight() {
		return right;
	}
	public void setRight(Node rightSide) {
		if(this.right != null) {
			throw new IllegalStateException("The right side already set. (old="+this.right+", new="+rightSide+")");
		}
		this.right = rightSide;
	}
	
	public String toString() {
		return "Node: type="+type+", value="+value;
	}
	
	public void leftFirstDump(String offset) {
		
		System.out.println(offset + this);
		
		offset += "  ";
		
		Node l = this.getLeft();
		if(l != null) {
		  System.out.println(offset +"Printing left side");
		  l.leftFirstDump(offset);
		}

		//Node m = this.getMiddle();
		//if(m != null) {
			//System.out.println(offset +"Printing middle");
			//m.leftFirstDump(offset);
		//} 
				
		Node r = this.getRight();
		if(r != null) {
  		System.out.println(offset +"Printing right side");
	  	r.leftFirstDump(offset);
		}
	}
}
