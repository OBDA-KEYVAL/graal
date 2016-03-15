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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Set;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import static com.mongodb.client.model.Filters.*;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class KeyValueStoreMongoDB extends KeyValueStore {

	private MongoClient client;
	private MongoDatabase db;

	// public static MongoDatabase connexionDB(String address, Integer port,
	// String databaseName) throws ParseException {
	// MongoClient mongoclient = new MongoClient(address, port);
	// MongoDatabase mongdb = mongoclient.getDatabase(databaseName);
	// System.out.println("Connect to \"" + databaseName + "\" succefully on " +
	// address + "@" + port);
	// return mongdb;
	// }

	public KeyValueStoreMongoDB() throws ParseException {
		client = new MongoClient();
	}

	public KeyValueStoreMongoDB(String add, Integer port) throws ParseException {
		client = new MongoClient(add, port);
	}

	public KeyValueStoreMongoDB(String add, Integer port, String dbname) throws ParseException {
		client = new MongoClient(add, port);
		db = client.getDatabase(dbname);
	}
	
	// Import dans la collection choisie un fichier JSON qui ce doit d'être minifié.
	public void importJsonIntoCollection(String collname, String jsonFile) throws IOException {
		MongoCollection<Document> collection = this.db.getCollection(collname);
		BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
		try {
			String json;

			while ((json = reader.readLine()) != null) {
				collection.insertOne(Document.parse(json));
			}
		} finally {
			reader.close();
		}

	}

	public void showCollections() {
		MongoIterable<String> colls = this.db.listCollectionNames();
		System.out.println("Collections :");
		for (String str : colls) {
			System.out.println("\t" + str);
		}
	}
	
	public void showCollectionsDeeper(){
		MongoCursor<Document> itrCol = db.listCollections().iterator();
		while(itrCol.hasNext()){
			System.out.println(itrCol.next().getString("name"));
		}
	}

	public boolean contains(PathAtom pathAtom) throws AtomSetException {
		// On initialise nos variable
		Boolean result = false;
		ListCollectionsIterable<Document> listColl = db.listCollections();
		MongoCursor<Document> itrCol = listColl.iterator();

		// On itére sur les collection de la DB
		while (itrCol.hasNext() && !result) {

			// On test si la pathAtome posséde un homomorphisme dans la
			// collection
			if (containsInCollection(pathAtom, itrCol.next().getString("name"))) {
				result = true;
			}
		}
		return result;
	}

	public boolean containsInCollection(PathAtom pathAtom, String nameCol) throws AtomSetException {
		// On initilise nos variables
		Document docResult = new Document();
		
		//On varie la requête en fonction du type du term de pathAtom
		if(pathAtom.getTerm(0).getType() == Type.VARIABLE){
			docResult = db.getCollection(nameCol).find(exists(pathAtom.getPathPredicate().toFieldName())).first();
		}
		else if(pathAtom.getTerm(0).getType() == Type.CONSTANT){
			//TODO finir la transformation de pathAtom -> MongoDBQuery
			docResult = db.getCollection(nameCol).find(eq(pathAtom.getPathPredicate().toFieldName(), pathAtom.getTerm(0).getIdentifier())).first();
			System.out.println(docResult+":"+pathAtom.getTerm(0).getLabel());
		}
		else{
			throw new AtomSetException("Le Term ne peut être de type Literal");
		}
		return docResult==null?false:true;
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

	public void close() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

}
