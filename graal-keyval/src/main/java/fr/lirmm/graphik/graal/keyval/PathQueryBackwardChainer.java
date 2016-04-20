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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.GIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
//queryGet et queryCheck ne doivent pas etre nul, utiliser le contructeur par défaut à la place
public class PathQueryBackwardChainer implements GIterator<PathQuery>, Profilable {
	
	private List<KeyValueStore> storeList;
	private PathQuery queryCheck;
	private PathQuery queryGet;
	private ArrayList<NoRule> noRules;
	private DataSummary dataSum;
	private GraphOfNORLRuleDependencies graph;
	
	public PathQueryBackwardChainer(List<KeyValueStore> storeList, PathQuery queryCheck, PathQuery queryGet, ArrayList<NoRule> noRules,
			DataSummary dataSum, GraphOfNORLRuleDependencies graph) {
		super();
		this.storeList = storeList;
		this.queryCheck = queryCheck;
		this.queryGet = queryGet;
		this.noRules = noRules;
		this.dataSum = dataSum;
		this.graph = graph;
	}
	
	public PathQueryBackwardChainer(List<KeyValueStore> storeList, PathQuery queryCheck, PathQuery queryGet, ArrayList<NoRule> noRules,
			DataSummary dataSum) {
		super();
		this.storeList = storeList;
		this.queryCheck = queryCheck;
		this.queryGet = queryGet;
		this.noRules = noRules;
		this.dataSum = dataSum;
		ArrayList<Rule> rules = new ArrayList<Rule>();
		for(NoRule r : noRules){
			rules.add(r);
		}
		this.graph = new GraphOfNORLRuleDependencies(rules);
	}
	
	public PathQueryBackwardChainer(PathQuery queryCheck, PathQuery queryGet, ArrayList<NoRule> noRules) {
		super();
		this.queryCheck = queryCheck;
		this.queryGet = queryGet;
		this.noRules = noRules;
		ArrayList<Rule> rules = new ArrayList<Rule>();
		for(NoRule r : noRules){
			rules.add(r);
		}
		this.graph = new GraphOfNORLRuleDependencies(rules);
	}
	
	//renvoie les reformulations de requete
	//sous forme de couple (check, get) tous les check sont combinés avec tous les get
	public ArrayList<ArrayList<PathQuery>> backwardNaif(){
		ArrayList<ArrayList<PathQuery>> reform_check_get = reform_check_get();
		ArrayList<PathQuery> reformCheck = reform_check_get.get(0);
		ArrayList<PathQuery> reformGet = reform_check_get.get(1);
		System.out.println("Nb reform check = " + reformCheck.size());
		System.out.println("Nb reform get = " + reformGet.size());
		ArrayList<ArrayList<PathQuery>> result = new ArrayList<ArrayList<PathQuery>>(); 
		for(PathQuery check : reformCheck){
			for(PathQuery get : reformGet){
				ArrayList<PathQuery> couple = new ArrayList<PathQuery>();
				couple.add(check);
				couple.add(get);
				result.add(couple);
			}
		}
		return result;
	}

	//renvoie les reformulations de check et les reformulation de get
	public ArrayList<ArrayList<PathQuery>> reform_check_get(){
		ArrayList<PathQuery> reformGet = new ArrayList<PathQuery>();
		reformGet.add(this.queryGet);
		ArrayList<PathQuery> reformCheck = new ArrayList<PathQuery>();
		reformCheck.add(this.queryCheck);
		
		if(testEnd()){
			ArrayList<PathQuery> newReformGet = new ArrayList<PathQuery>();
			newReformGet.add(this.queryGet);
			ArrayList<PathQuery> newReformCheck = new ArrayList<PathQuery>();
			newReformCheck.add(this.queryCheck);
			
			while(!newReformGet.isEmpty() || !newReformCheck.isEmpty()){
				ArrayList<PathQuery> tempReform = newReformGet;
				newReformGet = new ArrayList<PathQuery>();
				if(!tempReform.isEmpty()){
					for(PathQuery q1 : tempReform){
						for(NoRule r : this.noRules){
							ArrayList<PathQuery> reform = reformValeur(q1, r);
							if(!reform.isEmpty()){
								for(PathQuery q2 : reform){
									if(!existReform(reformGet, q2)){
										reformGet.add(q2);
										newReformGet.add(q2);
									}
								}
							}
						}
					}
				}
				
				tempReform = newReformCheck;
				newReformCheck = new ArrayList<PathQuery>();
				if(!tempReform.isEmpty()){
					for(PathQuery q1 : tempReform){
						for(NoRule r : this.noRules){
							ArrayList<PathQuery> reform = reformValeur(q1, r);
							if(!reform.isEmpty()){
								for(PathQuery q2 : reform){
									if(!existReform(reformCheck, q2) && !subsume(reformCheck, q2)){
										reformCheck.add(q2);
										newReformCheck.add(q2);
									}
								}
							}							
							reform = reformBool(q1, r);
							if(!reform.isEmpty()){
								for(PathQuery q2 : reform){
									if(!existReform(reformCheck, q2) && !subsume(reformCheck, q2)){
										reformCheck.add(q2);
										newReformCheck.add(q2);
									}
								}
							}
						}
					}
				}
			}
		}
		reformCheck = removeSubsumees(reformCheck);
		ArrayList<ArrayList<PathQuery>> result = new ArrayList<ArrayList<PathQuery>>();
		result.add(reformCheck);
		result.add(reformGet);
		return result;
	}
	
	//renvoie vrai si la requete q existe dans tabQ sinon faux
	public boolean existReform(ArrayList<PathQuery> tabQ, PathQuery q){
		for(PathQuery p : tabQ){
			if(equalPathAtom(p, q)){
				return true;
			}
		}
		return false;
	}
	
	//enleve les requetes subsumé par les requetes de tabQ
	public ArrayList<PathQuery> removeSubsumees(ArrayList<PathQuery> tabQ){
		ArrayList<PathQuery> result = new ArrayList<PathQuery>();
		ArrayList<PathQuery> temp;
		for(PathQuery q : tabQ){
			temp = new ArrayList<PathQuery>();
			for(PathQuery p : tabQ){
				if(!equalPathAtom(q, p)){
					temp.add(p);
				}
			}
			if(!subsume(temp, q)){
				result.add(q);
			}
		}
		return result;
	}
	
	//renvoie vrai si q est subsumé par l'une des requetes de tabQ sinon faux
	public boolean subsume(ArrayList<PathQuery> tabQ, PathQuery q){
		for(PathQuery p : tabQ){
			if(subsume(p, q)){
				return true;
			}
		}
		return false;
	}
	
	//renvoie vrai si q1 subsume q2 sinon faux
	//q1 et q2 sont des requetes bouléennes
	public boolean subsume(PathQuery q1, PathQuery q2){
		Term termQ1 = q1.getTerm();
		ArrayList<String> keysQ1 = q1.getPathPredicate().predicatesToStrings();
		
		Term termQ2 = q2.getTerm();
		ArrayList<String> keysQ2 = q2.getPathPredicate().predicatesToStrings();
		
		if(keysQ2.size() < keysQ1.size()){
			return false;
		}
		else{
			if((termQ1.getType().equals(Type.CONSTANT) && termQ2.getType().equals(Type.CONSTANT)
					&& !termQ1.getLabel().equals(termQ2.getLabel())) ||
					(termQ1.getType().equals(Type.CONSTANT) && termQ2.getType().equals(Type.VARIABLE))){
				return false;
			}
			else{
				for(int i = 0; i < keysQ1.size(); i++){
					if(!keysQ1.get(i).equals(keysQ2.get(i))){
						return false;
					}
				}
				return true;
			}
		}
	}
	
	//renvoie les reformulations de q (check ou get) par la regle r (non existentielle)
	public ArrayList<PathQuery> reformValeur(PathQuery q, NoRule r){
		PathAtom body = r.getBodyPathAtom();
		Term bodyTerm = body.getTerm();
		PathAtom head = r.getHeadPathAtom();
		Term headTerm = head.getTerm();
		
		if(bodyTerm.getType().equals(headTerm.getType())
				&& bodyTerm.getLabel().equals(headTerm.getLabel())){
				
			ArrayList<String> keysBody = body.getPathPredicate().predicatesToStrings();
			ArrayList<String> keysHead = head.getPathPredicate().predicatesToStrings();
			ArrayList<String> keysQ = q.getPathPredicate().predicatesToStrings();
			
			ArrayList<Integer> posDebut = new ArrayList<Integer>(); 
			
			for(int i = 0; i < keysQ.size(); i++){
				if(i+keysHead.size() <= keysQ.size()){
					int cpt = 0;
					for(int j = 0; j < keysHead.size(); j++){
						if(keysQ.get(i+j).equals(keysHead.get(j))){
							cpt++;
						}
					}
					if(cpt == keysHead.size()){
						posDebut.add(i);
					}
				}
			}
			
			ArrayList<PathQuery> result = new ArrayList<PathQuery>(); 
			
			for(int pos : posDebut){
				PathPredicate reformPthPred = new PathPredicate();
				for(int i = 0; i < pos; i++){
					reformPthPred.addPredicate(new Predicate(keysQ.get(i), 1));
				}
				for(int i = 0; i < keysBody.size(); i++){
					reformPthPred.addPredicate(new Predicate(keysBody.get(i), 1));
				}
				for(int i = pos+keysHead.size(); i < keysQ.size(); i++){
					reformPthPred.addPredicate(new Predicate(keysQ.get(i), 1));
				}
				PathQuery reformQuery = new PathQuery(reformPthPred, q.getTerm());
				result.add(reformQuery);
			}
			return result;
		}
		return new ArrayList<PathQuery>();
	}
	
	//renvoie les reformulations de q (check) par la regle r
	public ArrayList<PathQuery> reformBool(PathQuery q, NoRule r){
		PathAtom body = r.getBodyPathAtom();
		PathAtom head = r.getHeadPathAtom();
		
		if(q.getTerm().isConstant()){
			return new ArrayList<PathQuery>();
		}
		
		ArrayList<String> keysBody = body.getPathPredicate().predicatesToStrings();
		ArrayList<String> keysHead = head.getPathPredicate().predicatesToStrings();
		ArrayList<String> keysQ = q.getPathPredicate().predicatesToStrings();
		
		ArrayList<Integer> posDebut = new ArrayList<Integer>(); 
		
		for(int i = keysQ.size()-1; i >= 0; i--){
			if(i+keysHead.size() >= keysQ.size()){
				int cpt = 0;
				for(int j = 0; i+j < keysQ.size(); j++){
					if(keysQ.get(i+j).equals(keysHead.get(j))){
						cpt++;
					}
				}
				if(i+cpt == keysQ.size()){
					posDebut.add(i);
				}
			}
		}
		
		ArrayList<PathQuery> result = new ArrayList<PathQuery>(); 
		
		for(int pos : posDebut){
			PathPredicate reformPthPred = new PathPredicate();
			for(int i = 0; i < pos; i++){
				reformPthPred.addPredicate(new Predicate(keysQ.get(i), 1));
			}
			for(int i = 0; i < keysBody.size(); i++){
				reformPthPred.addPredicate(new Predicate(keysBody.get(i), 1));
			}
			PathQuery reformQuery = new PathQuery(reformPthPred, q.getTerm());
			result.add(reformQuery);
		}
		return result;
	}
	
	//test si graph présente un cycle contenant une règle NO-RL(2)
	//renvoie vrai s'il ne possede pas un tel cycle sinon faux
	public boolean testEnd(){
		ArrayList<NoRule> rules_norl2 = this.graph.getRules_NoRL2();
		for(NoRule r : rules_norl2){
			Stack<NoRule> pile = new Stack<NoRule>();
			ArrayList<NoRule> vue = new ArrayList<NoRule>();
			pile.add(r);
			while(!pile.isEmpty()){
				NoRule x = pile.lastElement();
				pile.pop();
				boolean est_vue = false;
				for(NoRule r1 : vue){
					if(equalRule(r1, x)){
						est_vue = true;
						break;
					}
				}
				if(!est_vue){
					vue.add(x);
					ArrayList<NoRule> fils = this.graph.getChild(x);
					for(NoRule y : fils){
						pile.add(y);
					}
				}
				else{
					if(equalRule(x, r)){
						System.out.println("L'algorithme de backward ne pourra pas s'éxécuter");
						return false;
					}
				}
			}
		}
		System.out.println("L'algorithme de backward peut s'éxécuter");
		return true;
	}
	
	//renvoie vrai si r1 <=> r2 sinon faux
	public boolean equalRule(NoRule r1, NoRule r2){
		PathAtom r1Body = r1.getBodyPathAtom();
		PathAtom r1Head = r1.getHeadPathAtom();
		PathAtom r2Body = r1.getBodyPathAtom();
		PathAtom r2Head = r1.getHeadPathAtom();
		
		return equalPathAtom(r1Body, r2Body) && equalPathAtom(r1Head, r2Head); 
	}
	
	//renvoi vrai si pthAt1 et pthAt2 sont unifiables sinon faux
	public boolean equalPathAtom(PathAtom pthAt1, PathAtom pthAt2){
		Term termPthAt1 = pthAt1.getTerm();
		ArrayList<String> keysPthAt1 = pthAt1.getPathPredicate().predicatesToStrings();

		Term termPthAt2 = pthAt2.getTerm();
		ArrayList<String> keysPthAt2 = pthAt2.getPathPredicate().predicatesToStrings();
		
		if(keysPthAt1.size() != keysPthAt2.size()){
			return false;
		}
		else{
			if(termPthAt1.getType().equals(Type.CONSTANT)
					&& termPthAt2.getType().equals(Type.CONSTANT)
					&& !termPthAt1.getLabel().equals(termPthAt2.getLabel())){
				return false;
			}
			else{
				for(int i = 0; i < keysPthAt1.size(); i++){
					if(!keysPthAt1.get(i).equals(keysPthAt2.get(i))){
						return false;
					}
				}
				return true;
			}
		}
	}
	
	public List<KeyValueStore> getStoreList() {
		return storeList;
	}

	public void setStoreList(List<KeyValueStore> storeList) {
		this.storeList = storeList;
	}

	public PathQuery getQueryGet() {
		return queryGet;
	}

	public void setQueryGet(PathQuery queryGet) {
		this.queryGet = queryGet;
	}

	public PathQuery getQueryCheck() {
		return queryCheck;
	}

	public void setQueryCheck(PathQuery queryCheck) {
		this.queryCheck = queryCheck;
	}

	public ArrayList<NoRule> getNoRules() {
		return noRules;
	}

	public void setNoRules(ArrayList<NoRule> noRules) {
		this.noRules = noRules;
	}

	public DataSummary getDataSum() {
		return dataSum;
	}

	public void setDataSum(DataSummary dataSum) {
		this.dataSum = dataSum;
	}

	public GraphOfNORLRuleDependencies getGraph() {
		return graph;
	}

	public void setGraph(GraphOfNORLRuleDependencies graph) {
		this.graph = graph;
	}
	
	public void remove() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public void setProfiler(Profiler profiler) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public Profiler getProfiler() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public boolean hasNext() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	
	public PathQuery next() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}
}
