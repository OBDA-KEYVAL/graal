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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PathAtom implements Atom{

	private PathPredicate pathPredicat;
	private Term term;
	
	public PathAtom (){
		pathPredicat = new PathPredicate();
		term = new KeyValueTerm();
	}
	
	public PathAtom (PathPredicate pp,Term tr){
		pathPredicat = pp;
		term = tr;
	}
	
	public PathPredicate getPathPredicate(){
		return this.pathPredicat;
	}
	
	public void setPathPredicate(PathPredicate pathPred){
		this.pathPredicat = pathPred;
	}
	
	public Term getTerm(){
		return this.term;
	}
	
	public void setTerm(Term term){
		this.term = term;
	}

	public int compareTo(Atom arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void appendTo(StringBuilder arg0) {
		// TODO Auto-generated method stub
		
	}

	public Predicate getPredicate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Term getTerm(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Term> getTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Term> getTerms(Type arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPredicate(Predicate arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setTerm(int arg0, Term arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public String toString(){
		return this.pathPredicat.toString() + " " + this.term.toString();
	}

	public Iterator<Term> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
