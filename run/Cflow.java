package run;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

import dfa.DFA;
import dfa.DeadState;

import parser.RegexParser;
import parser.SimpleNode;
import preprocessor.PreProcessor;
import enfa.ENFA;


public class Cflow {
	public static ENFA automaton = new ENFA();
	public static DFA optimized_automaton;
	public static String current_state;
	public static ArrayList<String[]> log = new ArrayList<String[]>();
	public static String main_class;
	public static String stream = "";

	public static void main(String args[]) {
		if(args.length < 2) {
			System.out.println("Use: java -jar cflow <REGULAR EXPRESSION> <FILE1> <FILE2> ...");
			return;
		}
		
		// (1) Convert file
		PreProcessor myFile = new PreProcessor();
		myFile.run(args);
		
		try {
			Process p = Runtime.getRuntime().exec("cmd /C javac -cp .;cflow.jar *.java");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			p = Runtime.getRuntime().exec("cmd /C java -cp .;cflow.jar " + Cflow.main_class);
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
		Cflow.automaton = parser.getENFA();
		
		try {
			Cflow.optimized_automaton = Cflow.automaton.optimize();
			Cflow.optimized_automaton.drawGraph();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		current_state = Cflow.optimized_automaton.get_initial_state();
	}

	public static void transition(String block) {
		if(stream.isEmpty())
			Cflow.stream += block;
		else
			Cflow.stream += "." + block;
		try {
			current_state = Cflow.optimized_automaton.get_next_state(current_state, block);
			String[] message = { block, "passed" };
			log.add(message);
		} catch(DeadState ds) {
			String[] message = { block, "failed" };
			log.add(message);
		}
	}

	public static void show_result() {
		System.out.println("\n===== CFLOW RESULTS =====\n");
		for (String[] message : log) {
			System.out.println(message[0] + ": " + message[1]);
		}
		
		if(Cflow.optimized_automaton.match(stream)) {
			System.out.println("\nFlow accepted.");
			return;
		}
		System.out.println("\nFlow rejected.");
		System.out.println("\n=========================");
	}
}
