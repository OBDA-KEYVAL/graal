package fr.lirmm.graphik.graal.keyval;

import java.text.ParseException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Ca marche !!!");
		KeyValueStore store = new KeyValueStore();
		try {
			store.connexionDB("localhost", 27017, "test");
			store.showCollections();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
