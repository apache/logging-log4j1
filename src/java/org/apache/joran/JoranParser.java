package org.apache.joran;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JoranParser {

	private RuleStore ruleStore;

	JoranParser(RuleStore rs) {
		ruleStore = rs;
	}

	public void parse(Document document) {

		Pattern pattern = new Pattern();
		Element e = document.getDocumentElement();
		loop(e, pattern);
	}

	void loop(Node n, Pattern pattern) {
		if (n == null) {
			return;
		}

		try {
			pattern.push(n.getNodeName());
			if (n instanceof Element) {
				System.out.println("pattern is " + pattern);
			}

			List applicableActionList = ruleStore.matchActions(pattern);

			if(applicableActionList != null) {
			 callBeginAction(applicableActionList, n);
			}
			
			if (n.hasChildNodes()) {
				for (Node c = n.getFirstChild();
					c != null;
					c = c.getNextSibling()) {
					loop(c, pattern);
				}
			}
			if(applicableActionList != null) {
			  callEndAction(n);
			}
		} finally {
			pattern.pop();
		}
	}

	void callBeginAction(List applicableActionList, Node n) {
		if(applicableActionList == null) {
		  return;
		}
		
		short type = n.getNodeType();

		if(type != Node.ELEMENT_NODE) {
			return;
		}

		Element e = (Element) n;
		String localName = n.getNodeName();
  	System.out.println("New node: <" + localName + ">");
		System.out.println("  node is an element");
		System.out.println(
				"  element attribs: " + e.getAttributes());
    
		Iterator i = applicableActionList.iterator();

		while (i.hasNext()) {
			Action action = (Action) i.next();
			action.begin(e);
		}
	}

	void callEndAction(Node n) {
	}

}
