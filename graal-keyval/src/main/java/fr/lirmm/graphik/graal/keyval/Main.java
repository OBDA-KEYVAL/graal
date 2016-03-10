package fr.lirmm.graphik.graal.keyval;

import java.io.IOException;
import java.text.ParseException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			KeyValueStoreMongoDB store = new KeyValueStoreMongoDB("localhost", 27017, "test");
			store.importJsonIntoCollection("premierimport", "primer-dataset.json.txt");
			store.showCollections();
			System.out.println("Ca marche !!!");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
