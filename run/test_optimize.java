package run;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import dfa.DFA;

import parser.RegexParser;
import enfa.ENFA;

public class test_optimize {

	@Test
	public void test() {
		String regex = "A*.(B|C)\n";
		Cflow.start(regex);
		Cflow.transition("A");
		Cflow.show_result();
		System.out.println("Over"); 
		System.out.println("Ovasdsaer");
	}

}
