package fr.lirmm.graphik.graal.keyval;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Predicate;

public class PathPredicateTest {

	@Before
	public void setUp() throws Exception {
		PathPredicate pp1 = new PathPredicate();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParamConst() {
		Predicate pre = new Predicate("testPre", 1);
		ArrayList<Predicate> arrPre = new ArrayList<Predicate>();
		arrPre.add(pre);
		PathPredicate pp2 = new PathPredicate(arrPre);
		assertEquals((Integer)1, pp2.getSizePath());
	}

}
