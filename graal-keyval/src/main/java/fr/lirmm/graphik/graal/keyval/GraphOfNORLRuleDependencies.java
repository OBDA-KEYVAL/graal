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

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class GraphOfNORLRuleDependencies extends GraphOfRuleDependencies {

	private ArrayList<Rule> rules;
	
	public GraphOfNORLRuleDependencies(ArrayList<Rule> rules){
		super();
		this.rules = rules;
		for(Rule r : rules) {
			addRule(r);
		}
		for(Rule r1 : rules){
			for(Rule r2 : rules){
				computeDependency(r1, r2, DependencyChecker.DEFAULT);
			}
		}
	}
	
	@Override
    protected void computeDependency(Rule r1, Rule r2, DependencyChecker checker) {
		// TODO implement this method		
		if(existDependency((NoRule)r1, (NoRule)r2)){
			addDependency(r1, r2);
		}
	}
	
	public boolean existDependency(NoRule r1, NoRule r2){
		ArrayList<String> keysPredHeadR1 = r1.getHeadPathAtom().getPathPredicate().predicatesToStrings();
		ArrayList<String> keysPredBodyR2 = r2.getBodyPathAtom().getPathPredicate().predicatesToStrings();
		
		for(String s1 : keysPredHeadR1){
			for(String s2 : keysPredBodyR2){
				if(s1.equals(s2)){
					return true;
				}
			}
		}
		
		return false;
	}

	public ArrayList<NoRule> getRules_NoRL2(){
		ArrayList<NoRule> rules_norl2 = new ArrayList<NoRule>();
		for(Rule r : rules){
			NoRule norl_r = (NoRule)r;
			if(norl_r.isNoRL2()){
				rules_norl2.add(norl_r);
			}
		}
		return rules_norl2;
	}
	
	public ArrayList<NoRule> getChild(NoRule r){
		ArrayList<NoRule> fils = new ArrayList<NoRule>();
		Iterable<Integer> tabInt = getOutgoingEdgesOf(r);
		for(Integer i : tabInt){
			fils.add((NoRule)getEdgeTarget(i));
		}		
		return fils;
	}
}

