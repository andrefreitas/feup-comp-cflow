import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		
		// (2) Create cflow dir
		try{
			Runtime.getRuntime().exec("cmd /C mkdir cflow");
			Runtime.getRuntime().exec("cmd /C cp cflow.jar cflow");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// (3) Convert files
		myFile.run(args);
		
		try {
			Process p = Runtime.getRuntime().exec("cmd /C javac cflow\\*.java");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// (2) Set initial state

	}

	public static void start(String regex) {
		Cflow.states.add(Cflow.automata.get_initial_state());
		InputStream is = new ByteArrayInputStream(regex.getBytes());
		RegexParser parser = new RegexParser(is);
		Cflow.automata = parser.getENFA();
	}

	public static void transition(String block) {
		states = Cflow.automata.step_forward(Cflow.states, block);
		if (states.isEmpty()) {
			String[] message = { block, "failed" };
			log.add(message);
		} else {
			String[] message = { block, "passed" };
			log.add(message);
		}
	}

	public static void showResult() {
		for (String[] message : log) {
			System.out.println(message[0] + ": " + message[1]);
		}

		for (String state : states) {
			String[] identifiers = { "" };
			if (Cflow.automata.matchRecursive(state, identifiers, 0)) {
				System.out.println("Flow accepted.");
				return;
			}
		}

		System.out.println("Flow rejected.");
		return;
	}
}
