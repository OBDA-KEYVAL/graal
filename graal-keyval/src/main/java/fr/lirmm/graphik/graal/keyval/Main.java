package fr.lirmm.graphik.graal.keyval;

import java.text.ParseException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			KeyValueStoreMongoDB store = new KeyValueStoreMongoDB("localhost", 27017, "test");
			store.showCollections();
			System.out.println("Ca marche !!!");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
