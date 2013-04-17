import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/* Generated By:JJTree: Do not edit this line. SimpleNode.java Version 4.3 */
/* JavaCCOptions:MULTI=false,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public class SimpleNode implements Node {

	protected Node parent;
	protected Node[] children;
	protected int id;
	protected Object value;
	protected DFA_Generator parser;

	public static final int OR = 0;
	public static final int AND = 1;
	public static final int PLUS = 2;
	public static final int STAR = 3;
	public static final int QUESTIONM = 4;
	public static final int TIMES = 5;
	public static final int TIMESLEFT = 6;
	public static final int TIMESINT = 7;
	public static final int ID = 8;
	
	public static int currentState = 0;
	private static String beforeState = null;
	private static String lastState = "q0";

	// added
	//public int val;
	public int op = -1;
	public int timesType = -1;
	public int timLeft = -1;
	public int timRight = -1;
	public String identifier = null;

	public SimpleNode(int i) {
		id = i;
	}

	public SimpleNode(DFA_Generator p, int i) {
		this(i);
		parser = p;
	}

	public void jjtOpen() {
	}

	public void jjtClose() {
	}

	public void jjtSetParent(Node n) {
		parent = n;
	}

	public Node jjtGetParent() {
		return parent;
	}

	public void jjtAddChild(Node n, int i) {
		if (children == null) {
			children = new Node[i + 1];
		} else if (i >= children.length) {
			Node c[] = new Node[i + 1];
			System.arraycopy(children, 0, c, 0, children.length);
			children = c;
		}
		children[i] = n;
	}

	public Node jjtGetChild(int i) {
		return children[i];
	}

	public int jjtGetNumChildren() {
		return (children == null) ? 0 : children.length;
	}

	public void jjtSetValue(Object value) {
		this.value = value;
	}

	public Object jjtGetValue() {
		return value;
	}

	/*
	 * You can override these two methods in subclasses of SimpleNode to
	 * customize the way the node appears when the tree is dumped. If your
	 * output uses more than one line you should override toString(String),
	 * otherwise overriding toString() is probably all you need to do.
	 */

	public String toString() {
		return DFA_GeneratorTreeConstants.jjtNodeName[id];
	}

	public String toString(String prefix) {
		return prefix + toString();
	}

	/*
	 * Override this method if you want to customize how the node dumps out its
	 * children.
	 */

	public void dump(String prefix) {
		System.out.println(toString(prefix));
		switch(op) {
		case(OR):
			System.out.println("OR");
		break;
		case(AND):
			System.out.println("AND");
		break;
		case(ID):
			System.out.println("ID: " + identifier);
		default:
			break;
		}
		switch(timesType) {
		case(PLUS):
			System.out.println("ONCE OR MORE TIMES");
		break;
		case(STAR):
			System.out.println("ZERO OR MORE TIMES");
		break;
		case(QUESTIONM):
			System.out.println("ZERO OR ONCE");
		break;
		case(TIMES):
			System.out.println("TIMES {" + timLeft + "}");
		break;
		case(TIMESLEFT):
			System.out.println("TIMESLEFT {" + timLeft + ",}");
		break;
		case(TIMESINT):
			System.out.println("TIMESINT {" + timLeft + "," + timRight + "}");
		break;
		default:
			break;
		}
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				SimpleNode n = (SimpleNode) children[i];
				if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	}
	
	private static String getState() {
		currentState++;
		
		beforeState  = lastState;
		lastState = "q" + Integer.toString(currentState);
		
		return "q" + Integer.toString(currentState);
	}
	
	private static boolean verifySides(ArrayList<Boolean> sides)
	{
		for(int i = 0; i < sides.size(); i++)
			if(sides.get(i))
				return false;
		
		return true;
	}
	
	public void parseDFA(Set< String > alphabet, Set< String > states,
			HashMap< String, String > transitions, Set< String > accept_states,
			ArrayList<Boolean> sides, int lastOp) {
		
		int nextOp = -1;
		
		if(op == ID)
			alphabet.add(identifier);
		
		else if(op == OR)
		{
			if(lastOp == 0)
			{
				
			}
			
			else
			{
				
			}
			
			nextOp = 0;
		}
		
		else if(op == AND)
		{
			nextOp = 1;
		}
		
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				SimpleNode n = (SimpleNode) children[i];
				if (n != null) {
					
					if(i == 0)
						sides.add(true);
						
					else sides.add(false);
					
					n.parseDFA(alphabet, states, transitions, accept_states, sides, nextOp);
				}
			}
		}
		
		else if(verifySides(sides))
		{
			// adicionar aqui o estado final
		}
	}
}

/*
 * JavaCC - OriginalChecksum=6c31e328a7cdeb8911b1060e2a9e6c87 (do not edit this
 * line)
 */
