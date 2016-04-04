package fr.lirmm.graphik.graal.keyval;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// Connection au Store MongoDB
			KeyValueStoreMongoDB store = new KeyValueStoreMongoDB("localhost", 27017, "test","acteurs");

			// Construction d'un pathQuerye
			KeyValueTerm tr = new KeyValueTerm(203, Type.CONSTANT);
			Predicate pre1 = new Predicate("info", 1);
			Predicate pre2 = new Predicate("x", 1);
			ArrayList<Predicate> arrPredicates = new ArrayList<Predicate>();
			arrPredicates.add(pre1);
			arrPredicates.add(pre2);
			PathPredicate pp = new PathPredicate(arrPredicates);
			PathQuery pathQuery = new PathQuery(pp, tr);

			// Interogation du Store 
			store.showCollections();
			store.add(pathQuery);
			System.out.println(store.contains(pathQuery));
			
			System.out.println(store.isEmpty());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AtomSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
