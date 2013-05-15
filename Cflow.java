import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import preprocessor.PreProcessor;
import enfa.ENFA;
import parser.RegexParser;
public class Cflow {
	public static ENFA automata;
	public static TreeSet<String> states = new TreeSet<String>();
	public static ArrayList<String[]> log = new ArrayList<String[]>();
	public static void main(String args[]) {
		// (1) Convert file
		PreProcessor myFile = new PreProcessor();
		myFile.run(args);
		// (2) Convert Regex to ENFA
		String regex = args[0]+"\n";
		InputStream is = new ByteArrayInputStream(regex.getBytes());
		RegexParser parser = new RegexParser(is);
		Cflow.automata = parser.getENFA();
		
		// (3) Set initial state
		Cflow.states.add(Cflow.automata.get_initial_state());
		
		//Test
		transition("a");
		transition("a");
		transition("a");
		transition("b");
		transition("c");
		transition("d");
		transition("a");
		
		showResult();		
	}
	
	public static void transition(String block) {
		states = Cflow.automata.step_forward(Cflow.states, block);
		if (states.isEmpty()) {
			String[] message = {block, "failed"};
			log.add(message);
		}
		else {
			String[] message = {block, "passed"};
			log.add(message);
		}
	}
	
	public static void showResult() {
		for (String[] message: log) {
			System.out.println(message[0]+": " + message[1]);	
		}
		
		for (String state: states) {
			String[] identifiers = {""};
			if (Cflow.automata.matchRecursive(state, identifiers, 0)) {
				System.out.println("Flow accepted.");
				return;
			}
		}

		System.out.println("Flow rejected.");
		return;
	}
}
