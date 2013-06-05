package parser;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import enfa.ENFA;

public class testparser {

	@Test
	public void test() {
		String regex = "A*.(B|C)\n";
		InputStream is = new ByteArrayInputStream(regex.getBytes());
		RegexParser r = new RegexParser(is);
		
		ENFA t = r.getENFA();
		String[] identifiers = new String[0] ;
		boolean b = t.matchRecursive("q1", identifiers, 0);
		boolean c = t.match("A.A.A.B");
		assertTrue(b);
		assertTrue(c);
	}

}
