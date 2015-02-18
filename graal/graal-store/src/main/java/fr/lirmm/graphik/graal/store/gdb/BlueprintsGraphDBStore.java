/**
 * 
 */
package fr.lirmm.graphik.graal.store.gdb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * BlueprintsGraphDBStore wrap Blueprints API {@link http://blueprints.tinkerpop.com} into
 * an AtomSet. Blueprints API allows you to use many Graph Database like Neo4j,
 * Sparksee, OrientDB, Titan...
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BlueprintsGraphDBStore extends GraphDBStore {

	private final Graph graph;

	public BlueprintsGraphDBStore(Graph graph) {
		this.graph = graph;
		init();
	}

	private void init() {
		try {
			this.graph.getVertices("class", "");
		} catch (IllegalArgumentException e) {
			Vertex v = this.graph.addVertex(null);
			v.setProperty("class", "");
		}
	}

	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	// //////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public void close() {
		this.graph.shutdown();
	}

	@Override
	public boolean contains(Atom atom) {
		GraphQuery query = this.graph.query();
		query.has("class", "atom");
		query.has("predicate", predicateToString(atom.getPredicate()));

		int i = 0;
		for (Term t : atom) {
			query.has("term" + i++, termToString(t));
		}

		return query.vertices().iterator().hasNext();
	}

	@Override
	public Iterable<Predicate> getAllPredicates() {
		return new PredicateIterable(graph.getVertices("class", "predicate"));
	}

	@Override
	public Set<Term> getTerms() {
		Set<Term> terms = new TreeSet<Term>();
		for (Vertex v : this.graph.getVertices("class", "term")) {
			terms.add(vertexToTerm(v));
		}
		return terms;
	}

	@Override
	public Set<Term> getTerms(Type type) {
		Set<Term> terms = new TreeSet<Term>();
		GraphQuery query = this.graph.query();
		query.has("class", "term");
		query.has("type", type.toString());

		for (Vertex v : query.vertices()) {
			terms.add(vertexToTerm(v));
		}
		return terms;
	}

	@Override
	public boolean isSubSetOf(AtomSet atomset) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean isEmpty() {
		return !this.iterator().hasNext();
	}

	@Override
	public boolean add(Atom atom) {
		Vertex atomVertex = graph.addVertex(null);
		atomVertex.setProperty("class", "atom");
		atomVertex.setProperty("predicate",
				predicateToString(atom.getPredicate()));

		Vertex predicateVertex = this.add(atom.getPredicate());
		this.graph.addEdge(null, atomVertex, predicateVertex, "predicate");

		int i = 0;
		for (Term t : atom) {
			atomVertex.setProperty("term" + i, termToString(t));

			Vertex termVertex = this.add(t);
			Edge e = graph.addEdge(null, atomVertex, termVertex, "term");
			e.setProperty("index", i++);
		}

		return true;
	}

	private Vertex add(Predicate predicate) {
		Vertex v = null;

		GraphQuery query = this.graph.query();
		query.has("class", "predicate");
		query.has("value", predicate.getLabel());
		query.has("arity", predicate.getArity());
		Iterator<Vertex> it = query.vertices().iterator();

		if (it.hasNext()) {
			v = it.next();
		} else {
			v = graph.addVertex(null);
			v.setProperty("class", "predicate");
			v.setProperty("value", predicate.getLabel());
			v.setProperty("arity", predicate.getArity());
		}
		return v;
	}

	private Vertex add(Term term) {
		Vertex v = null;

		GraphQuery query = this.graph.query();
		query.has("class", "term");
		query.has("value", term.getValue().toString());
		query.has("type", term.getType().toString());
		Iterator<Vertex> it = query.vertices().iterator();

		if (it.hasNext()) {
			v = it.next();
		} else {
			v = this.graph.addVertex(null);
			v.setProperty("class", "term");
			v.setProperty("value", term.getValue().toString());
			v.setProperty("type", term.getType().toString());
		}
		return v;
	}

	@Override
	public boolean addAll(Iterable<? extends Atom> atoms) {
		for (Atom a : atoms) {
			this.add(a);
		}
		return true;
	}

	@Override
	public boolean remove(Atom atom) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean removeAll(Iterable<? extends Atom> atoms)
			throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public void clear() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public Iterator<Atom> iterator() {
		Iterator<Vertex> it = this.graph.getVertices("class", "atom")
				.iterator();
		return new AtomIterator(it);
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE STATIC METHODS
	// //////////////////////////////////////////////////////////////////////////

	private static Predicate vertexToPredicate(Vertex vertex) {
		String label = vertex.getProperty("value");
		int arity = vertex.getProperty("arity");
		return new Predicate(label, arity);
	}

	private static Term vertexToTerm(Vertex vertex) {
		return new Term(vertex.getProperty("value"), Term.Type.valueOf(vertex
				.getProperty("type").toString()));
	}

	private static Atom vertexToAtom(Vertex vertex) {
		List<Term> terms = new LinkedList<Term>();

		for (Edge e : vertex.getEdges(Direction.OUT, "term")) {
			Vertex t = e.getVertex(Direction.IN);
			terms.add(vertexToTerm(t));
		}

		Iterator<Edge> it = vertex.getEdges(Direction.OUT, "predicate")
				.iterator();
		Vertex predicateVertex = it.next().getVertex(Direction.IN);
		Predicate p = vertexToPredicate(predicateVertex);

		return new DefaultAtom(p, terms);
	}

	private static String predicateToString(Predicate p) {
		return p.getLabel() + "@" + p.getArity();
	}

	private static String termToString(Term t) {
		return t.getValue().toString() + "@" + t.getType().toString();
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// //////////////////////////////////////////////////////////////////////////

	private static class AtomIterator implements Iterator<Atom> {
		Iterator<Vertex> it;

		public AtomIterator(Iterator<Vertex> it) {
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			return this.it.hasNext();
		}

		@Override
		public Atom next() {
			return vertexToAtom(this.it.next());
		}

		@Override
		public void remove() {
			this.it.remove();
		}
	}

	private static class PredicateIterable implements Iterable<Predicate> {

		Iterable<Vertex> iterable;

		public PredicateIterable(Iterable<Vertex> vertices) {
			this.iterable = vertices;
		}

		@Override
		public Iterator<Predicate> iterator() {
			return new PredicateIterator(this.iterable.iterator());
		}
	}

	private static class PredicateIterator implements Iterator<Predicate> {

		Iterator<Vertex> it;

		public PredicateIterator(Iterator<Vertex> iterator) {
			this.it = iterator;
		}

		@Override
		public boolean hasNext() {
			return this.it.hasNext();
		}

		@Override
		public Predicate next() {
			return vertexToPredicate(this.it.next());
		}

		@Override
		public void remove() {
			this.it.remove();
		}

	}

}
