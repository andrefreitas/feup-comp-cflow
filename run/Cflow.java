package run;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

import parser.RegexParser;
import parser.SimpleNode;
import preprocessor.PreProcessor;
import enfa.ENFA;


public class Cflow {
	public static ENFA automata = new ENFA();
	public static TreeSet<String> states = new TreeSet<String>();
	public static ArrayList<String[]> log = new ArrayList<String[]>();
	public static String mainClass;

	public static void main(String args[]) {
		// (1) Convert file
		PreProcessor myFile = new PreProcessor();
		
		// (2) Create cflow dir
		try{
			if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				Runtime.getRuntime().exec("cmd /C mkdir cflow");
				Runtime.getRuntime().exec("cmd /C copy cflow.jar cflow");
			}
			else {
				Runtime.getRuntime().exec("cmd /C mkdir cflow");
				Runtime.getRuntime().exec("cmd /C cp cflow.jar cflow");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// (3) Convert files
		myFile.run(args);
		String regex = args[0] + "\n";
		start(regex);
		
		try {
			Process p = Runtime.getRuntime().exec("cmd /C javac -cp cflow\\.;cflow.jar cflow\\*.java");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			p = Runtime.getRuntime().exec("cmd /C java -cp cflow\\.;cflow.jar " + Cflow.mainClass);
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
		InputStream is = new ByteArrayInputStream(regex.getBytes());
		RegexParser parser = new RegexParser(is);
		Cflow.automata = parser.getENFA();
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
