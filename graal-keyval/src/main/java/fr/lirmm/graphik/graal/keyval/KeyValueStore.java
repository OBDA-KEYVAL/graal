/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.lirmm.graphik.graal.keyval;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;

import org.bson.*;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class KeyValueStore implements Store {

	public boolean contains(Atom atom) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public Set<Predicate> getPredicates() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public Set<Term> getTerms() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	public Set<Term> getTerms(Type type) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	public boolean isSubSetOf(AtomSet atomset) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	public boolean isEmpty() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public boolean add(Atom atom) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public boolean addAll(Iterator<? extends Atom> atoms) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public boolean addAll(AtomSet atoms) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public boolean remove(Atom atom) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public boolean removeAll(Iterator<? extends Atom> atoms) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public boolean removeAll(AtomSet atoms) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public void clear() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public CloseableIterator<Atom> iterator() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public CloseableIterator<Atom> match(Atom atom) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}
	
	public static void connexionDB() throws ParseException {
		MongoClient mongoclient = new MongoClient();
		MongoDatabase db = mongoclient.getDatabase("test");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		db.getCollection("restaurants").insertOne(
		        new Document("address",
		                new Document()
		                        .append("street", "2 Avenue")
		                        .append("zipcode", "10075")
		                        .append("building", "1480")
		                        .append("coord", asList(-73.9557413, 40.7720266)))
		                .append("borough", "Manhattan")
		                .append("cuisine", "Italian")
		                .append("grades", asList(
		                        new Document()
		                                .append("date", format.parse("2014-10-01T00:00:00Z"))
		                                .append("grade", "A")
		                                .append("score", 11),
		                        new Document()
		                                .append("date", format.parse("2014-01-16T00:00:00Z"))
		                                .append("grade", "B")
		                                .append("score", 17)))
		                .append("name", "Vella")
		                .append("restaurant_id", "41704620"));
	}

	
	public void close() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	public static void main(String[] args) {
		System.out.println("Hello Word, tentative de connexion ...");
		try {
			connexionDB();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
