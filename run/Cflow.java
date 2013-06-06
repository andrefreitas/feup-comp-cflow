package run;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

import dfa.DFA;

import parser.RegexParser;
import parser.SimpleNode;
import preprocessor.PreProcessor;
import enfa.ENFA;


public class Cflow {
	public static ENFA automata = new ENFA();
	public static DFA automataOpt;
	public static TreeSet<String> states = new TreeSet<String>();
	public static ArrayList<String[]> log = new ArrayList<String[]>();
	public static String mainClass;

	public static void main(String args[]) {
		// (1) Convert file
		PreProcessor myFile = new PreProcessor();
		myFile.run(args);
		
		try {
			Process p = Runtime.getRuntime().exec("cmd /C javac -cp .;cflow.jar *_cflow.java");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			p = Runtime.getRuntime().exec("cmd /C java -cp .;cflow.jar " + Cflow.mainClass);
			in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void start(String regex) {
		regex = regex + "\n";
		InputStream is = new ByteArrayInputStream(regex.getBytes());
		RegexParser parser = new RegexParser(is);
		Cflow.automata = parser.getENFA();
		Cflow.automataOpt = Cflow.automata.optimize();
		Cflow.automataOpt.
		Cflow.states.add(Cflow.automata.get_initial_state());
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

	public static void show_result() {
		for (String[] message : log) {
			System.out.println(message[0] + ": " + message[1]);
		}

		for (String state : states) {
			String[] identifiers = new String[0];
			if (Cflow.automata.matchRecursive(state, identifiers, 0)) {
				System.out.println("Flow accepted.");
				return;
			}
		}

		System.out.println("Flow rejected.");
		return;
	}
}
