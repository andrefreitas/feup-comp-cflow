import java.io.ByteArrayInputStream;
import java.io.InputStream;

import preprocessor.PreProcessor;
import enfa.ENFA;
import parser.RegexParser;
public class Cflow {
	public static ENFA automata;
	public static void main(String args[]) {
		// (1) Convert file
		PreProcessor myFile = new PreProcessor();
		myFile.run(args);
		// (2) Convert Regex to ENFA
		String regex = args[0];
		InputStream is = new ByteArrayInputStream(regex.getBytes());
		RegexParser parser = new RegexParser(is);
		Cflow.automata = parser.par
	}
}
