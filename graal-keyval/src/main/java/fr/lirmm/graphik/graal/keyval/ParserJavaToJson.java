package fr.lirmm.graphik.graal.keyval;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;

//Cette classe transforme une requete PathQuery en JSONObject
//et une regle NoRule en JSONObject
public class ParserJavaToJson extends Parser{
	
	//Parse une requete PathQuery en json
	@Override
	public Object parseQuery(Object obj) {
		PathQuery pthQuery = (PathQuery) obj;
		
		ArrayList<Predicate> listPreds = pthQuery.getPathPredicate().getPredicates();
		ArrayList<String> listKeys = new ArrayList<String>();
		for(Predicate pred : listPreds){
			listKeys.add((String) pred.getIdentifier());
		}
		 Term term = pthQuery.getTerm();
		
		JSONObject queryJson = constructJson(new JSONObject(), listKeys, listKeys.size(), term);
		
		return queryJson;
	}
	
	//Parse une regle NoRule en json
	@Override
	public Object parseRule(Object obj) {
		NoRule noRule = (NoRule) obj;
		
		PathAtom premisse = noRule.getPremisse();		
		ArrayList<Predicate> listPreds1 = premisse.getPathPredicate().getPredicates();
		ArrayList<String> listKeys1 = new ArrayList<String>();
		for(Predicate pred : listPreds1){
			listKeys1.add((String) pred.getIdentifier());
		}
		
		Term term1 = premisse.getTerm();
		
		PathAtom conclusion = noRule.getConclusion();		
		ArrayList<Predicate> listPreds2 = conclusion.getPathPredicate().getPredicates();
		ArrayList<String> listKeys2 = new ArrayList<String>();
		for(Predicate pred : listPreds2){
			listKeys2.add((String) pred.getIdentifier());
		}
		
		Term term2 = conclusion.getTerm();
		
		JSONObject bodyJson = constructJson(new JSONObject(), listKeys1, listKeys1.size(), term1); 
		JSONObject headJson = constructJson(new JSONObject(), listKeys2, listKeys2.size(), term2);
		
		JSONObject ruleJson = new JSONObject();
		try {
			ruleJson.put("body", bodyJson);
			ruleJson.put("head", headJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ruleJson;
	}

	
	private JSONObject constructJson(JSONObject json, ArrayList<String> listKeys, int nbKeys, Term term){
		if(listKeys.size() == nbKeys){
			try {
				if(term.getType().equals(Type.VARIABLE)){
					json.put(listKeys.get(nbKeys - 1), "?" + term.getLabel());
				}
				else{
					json.put(listKeys.get(nbKeys - 1), term.getLabel());
				}
				ArrayList<String> listKeys2 = new ArrayList<String>();
				for(int i = 0; i < listKeys.size() - 1; i++){
					listKeys2.add(listKeys.get(i));
				}
				return constructJson(json, listKeys2, nbKeys, term);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			if(listKeys.size() > 0){
				try {
					JSONObject json2 = new JSONObject();
					json2.put(listKeys.get(listKeys.size() - 1), json);
					json = json2;
					ArrayList<String> listKeys2 = new ArrayList<String>();
					for(int i = 0; i < listKeys.size() - 1; i++){
						listKeys2.add(listKeys.get(i));
					}
					return constructJson(json, listKeys2, nbKeys, term);				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				return json;
			}
		}
		return json;
	}
}

