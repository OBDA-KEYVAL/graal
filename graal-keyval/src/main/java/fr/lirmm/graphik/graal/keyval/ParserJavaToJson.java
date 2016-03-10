package fr.lirmm.graphik.graal.keyval;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

//Cette classe transforme une requete PathQuery en JSONObject
//et une regle NoRule en JSONObject
public class ParserJavaToJson extends Parser{
	
	//Parse une requete PathQuery en json
	@Override
	public Object parseQuery(Object obj) {
		PathQuery pthQuery = (PathQuery) obj;
		
		ArrayList<String> listKeys = pthQuery.getPathPredicate().getKeys();
		String labelTerm = pthQuery.getTerm().getLabel();
		
		JSONObject queryJson = constructJson(new JSONObject(), listKeys, listKeys.size(), labelTerm);
		
		return queryJson;
	}
	
	//Parse une regle NoRule en json
	@Override
	public Object parseRule(Object obj) {
		NoRule noRule = (NoRule) obj;
		
		PathAtom premisse = noRule.getPremisse();
		ArrayList<String> listKeys1 = premisse.getPathPredicate().getKeys();
		String labelTerm1 = premisse.getTerm().getLabel();
		
		PathAtom conclusion = noRule.getConclusion();
		ArrayList<String> listKeys2 = conclusion.getPathPredicate().getKeys();
		String labelTerm2 = conclusion.getTerm().getLabel();
		
		JSONObject bodyJson = constructJson(new JSONObject(), listKeys1, listKeys1.size(), labelTerm1); 
		JSONObject headJson = constructJson(new JSONObject(), listKeys2, listKeys2.size(), labelTerm2);
		
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

	
	private JSONObject constructJson(JSONObject json, ArrayList<String> listKeys, int nbKeys, String term){
		if(listKeys.size() == nbKeys){
			try {
				json.put(listKeys.get(nbKeys - 1), "?" + term);
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

