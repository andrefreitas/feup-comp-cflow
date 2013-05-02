package enfa;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.*;

public class TestENFA extends TestCase {

	public void test_constructor_match() throws Exception{

		Set<String> states = new TreeSet<String>();
		states.add("q1");
		states.add("q2");

		Set<String> alphabet = new TreeSet<String>();
		alphabet.add("a");
		alphabet.add("b");

		ArrayList<String[]> transitions = new ArrayList<String[]>();
		String[] t1 = { "q1", "a", "q2" };
		String[] t2 = { "q2", "b", "q2" };
		transitions.add(t1);
		transitions.add(t2);

		String initial_state = "q1";

		Set<String> accept_states = new TreeSet<String>();
		accept_states.add("q2");
		ENFA d1 = new ENFA(states, alphabet, transitions, initial_state,
				accept_states);
		
		assertTrue(d1.match("a.b"));
		assertTrue(d1.match("a.b.b.b.b.b"));
		assertFalse(d1.match("a.c"));
		assertFalse(d1.match("j.h.g.r.u.i.g.h.e.r.u.i.h"));
		assertFalse(d1.match(""));
		assertFalse(d1.match("a.a.a.a.b.b.b.b.b.b"));
	}


}