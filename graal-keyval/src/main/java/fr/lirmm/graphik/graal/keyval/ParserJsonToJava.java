package fr.lirmm.graphik.graal.keyval;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term.Type;


//Cette classe transforme une requete JSONObject en PathQuery
//et une regle JSONObject en NoRule
public class ParserJsonToJava extends Parser {
	
	//Parse une requete json en PathQuery
	@Override
	public Object parseQuery(Object obj){
		JSONObject queryJs = (JSONObject) obj;
		
		ArrayList<String> listKey = new ArrayList<String>();
		PathPredicate pthPred = null;
		KeyValueTerm term = null;
		PathQuery pthQuery = null;
		
		JSONObject tempJs = queryJs;
		String tempKey;
		
		while(true){
			tempKey = tempJs.keys().next().toString();
			listKey.add(tempKey);
			try {
				if(tempJs.get(tempKey) instanceof String){
					String tmpLabel = tempJs.get(tempKey).toString();
					if(tmpLabel.contains("?")){
						term = new KeyValueTerm(tmpLabel.substring(1), Type.VARIABLE);
					}
					else{
						term = new KeyValueTerm(tmpLabel, Type.CONSTANT);
					}
					break;
				}
				else{
					tempJs = (JSONObject) tempJs.get(tempKey);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		pthPred = new PathPredicate();
		for(String str : listKey){
			pthPred.addPredicate(new Predicate(str, 1));
		}
		pthQuery = new PathQuery(pthPred, term);
		
		return pthQuery;
	}
	
	//Parse une regle json en NoRule
	@Override
	public Object parseRule(Object obj) {
		JSONObject ruleJs = (JSONObject) obj;
		
		ArrayList<String> listKey1 = new ArrayList<String>();
		PathPredicate pthPred1 = null;
		KeyValueTerm term1 = null;
		PathAtom pthAtom1 = null;
		
		ArrayList<String> listKey2 = new ArrayList<String>();
		PathPredicate pthPred2 = null;
		KeyValueTerm term2 = null;
		PathAtom pthAtom2 = null;
		
		NoRule rule = null;
		
		JSONObject tempH = null;
		JSONObject tempB = null;
		try {
			tempH = (JSONObject) ruleJs.get("head");
			tempB = (JSONObject) ruleJs.get("body");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		JSONObject tempJs;
		String tempKey;
		
		ArrayList<String> tempListKey;
		KeyValueTerm tempTerm;
	
		for(int i = 0; i < 2; i++){
			tempListKey = new ArrayList<String>();
			if(i == 1){
				tempJs = tempH;
			}
			else{
				tempJs = tempB;
			}
			while(true){
				tempKey = tempJs.keys().next().toString();
				tempListKey.add(tempKey);
				try {
					if(tempJs.get(tempKey) instanceof String){
						String tmpLabel = tempJs.get(tempKey).toString();
						if(tmpLabel.contains("?")){
							tempTerm = new KeyValueTerm(tmpLabel.substring(1), Type.VARIABLE);
						}
						else{
							tempTerm = new KeyValueTerm(tmpLabel, Type.CONSTANT);
						}
						
						if(i == 1){
							listKey2 = tempListKey;
							term2 = tempTerm;
						}
						else{
							listKey1 = tempListKey;
							term1 = tempTerm;
						}
						break;
					}
					else{
						tempJs = (JSONObject) tempJs.get(tempKey);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		pthPred1 = new PathPredicate();
		for(String str : listKey1){
			pthPred1.addPredicate(new Predicate(str, 1));
		}
		pthAtom1 = new PathQuery(pthPred1, term1);
		
		pthPred2 = new PathPredicate();
		for(String str : listKey2){
			pthPred2.addPredicate(new Predicate(str, 1));
		}
		pthAtom2 = new PathQuery(pthPred2, term2);
		
		rule = new NoRule(pthAtom1, pthAtom2);
		
		return rule;
	}	
}
