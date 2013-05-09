package parser;
import enfa.ENFA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

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

	private static int numberState = 0;
	private static String startState = "q0";
	private static String currentState = "q0";

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

	public ENFA parseENFA() throws Exception {
		ENFA tempENFA = new ENFA();
		
		switch(op) {
		case(ID): {
			System.out.println("ID: " + identifier);
			Set<String> states = new TreeSet<String>();
			states.add("q0");
			states.add("q1");

			Set<String> alphabet = new TreeSet<String>();
			alphabet.add(identifier);

			ArrayList<String[]> transitions = new ArrayList<String[]>();
			String[] t1 = { "q0", identifier, "q1" };
			transitions.add(t1);

			String initial_state = "q0";

			Set<String> accept_states = new TreeSet<String>();
			accept_states.add("q1");

			tempENFA = new ENFA(states, alphabet, transitions, initial_state, accept_states);
		}

		default:
			break;
		}

		ENFA enfa1 = new ENFA();
		boolean isOp = false;
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				SimpleNode n = (SimpleNode) children[i];
				if (n != null) {
					switch(op) {
						case(OR): {
							isOp = true;
							if (i == 0) {
								System.out.println("OR");
								enfa1 = n.parseENFA();
							}
							else {
								ENFA enfa2 = n.parseENFA();
								tempENFA = ENFA.operator_or(enfa1, enfa2);
							}
						}
						break;
						
						case(AND): {
							isOp = true;
							if (i == 0) {
								System.out.println("AND");
								enfa1 = n.parseENFA();
							}
							else {
								ENFA enfa2 = n.parseENFA();
								tempENFA = ENFA.operator_and(enfa1, enfa2);
							}
						}
						break;
						
						default:
							break;
					}

					if (!isOp)
						tempENFA = n.parseENFA();
					
					if (i < children.length-1) {
						SimpleNode n2 = (SimpleNode) children[i+1];
						if (n2.timesType != -1) {
							i++;
							switch(n2.timesType) {
							case(PLUS): {
								tempENFA = ENFA.operator_plus(tempENFA);
								System.out.println("ONCE OR MORE TIMES");
							}

							break;
							case(STAR): {
								tempENFA = ENFA.operator_star(tempENFA);
								System.out.println("ZERO OR MORE TIMES");
							}

							break;
							case(QUESTIONM): {
								tempENFA = ENFA.operator_questionm(tempENFA);
								System.out.println("ZERO OR ONCE");
							}

							break;
							case(TIMES): {
								tempENFA = ENFA.operator_times(tempENFA, n2.timLeft);
								System.out.println("TIMES {" + n2.timLeft + "}");
							}

							break;
							case(TIMESLEFT): {
								tempENFA = ENFA.operator_timesleft(tempENFA, n2.timLeft);
								System.out.println("TIMESLEFT {" + n2.timLeft + ",}");
							}
							break;
							case(TIMESINT): {
								tempENFA = ENFA.operator_timesint(tempENFA, n2.timLeft, n2.timRight);
								System.out.println("TIMESINT {" + n2.timLeft + "," + n2.timRight + "}");
							}

							break;
							default:
								break;
							}
						}
					}
				}
			}
		}
		return tempENFA;
	}

	private static String getState() {
		numberState++;

		currentState = "q" + Integer.toString(numberState);

		return currentState;
	}

	public void parseDFA(Set< String > alphabet, Set< String > states,
			HashMap< String, String > transitions, Set< String > accept_states) {

		if(op == ID)
			alphabet.add(identifier);

		else if(op == OR)
		{

		}

		else if(op == AND)
		{

		}

		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				SimpleNode n = (SimpleNode) children[i];
				if (n != null) {				
					n.parseDFA(alphabet, states, transitions, accept_states);
				}
			}
		}
	}
}

/*
 * JavaCC - OriginalChecksum=6c31e328a7cdeb8911b1060e2a9e6c87 (do not edit this
 * line)
 */
