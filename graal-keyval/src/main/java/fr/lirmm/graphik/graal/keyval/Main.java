package fr.lirmm.graphik.graal.keyval;

import java.io.Console;
import java.io.IOException;
import java.nio.channels.ShutdownChannelGroupException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;

public class Main {
	private static boolean Finish = false;
	private static ArrayList<PathQuery> arrPathQuery = new ArrayList<PathQuery>();

	private static KeyValueStoreMongoDB connect() {
		try {
			Scanner scan = new Scanner(System.in);
			System.out.print("\tSet Host : ");
			String host = scan.next();
			System.out.print("\tSet Port : ");
			String port = scan.next();
			System.out.print("\tChoose the database : ");
			String db = scan.next();
			KeyValueStoreMongoDB kvs = new KeyValueStoreMongoDB(host, Integer.parseInt(port), db);
			return kvs;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		KeyValueStoreMongoDB store = null;
		while (!Finish) {
			if (store == null) {
				System.out.print("\n@:@//> ");
			} else if (store.getCurrentCollection() == null) {
				System.out.print("\n" + store.getDatabase().getName() + ":@//> ");
			} else {
				System.out.print("\n" + store.getDatabase().getName() + ":"
						+ store.getCurrentCollection().getNamespace().getCollectionName() + "//> ");
			}
			Scanner scan = new Scanner(System.in);
			String input = scan.next();
			switch (input) {

			case "connect":
				try {
					store = connect();
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			case "check":
				if(arrPathQuery.isEmpty()){
					System.out.println("Pas de requête en mémoire");
				}
				System.out.println("Requête disponible : ");
				Integer cpt = 0;
				for (PathQuery pathQuery : arrPathQuery) {
					System.out.println(cpt+" :: "+pathQuery.getPathPredicate().toString()+" : "+pathQuery.getTerm(0).toString() + " -- "+(pathQuery.getTerm(0).isConstant()?"Cst":"Var"));
					cpt++;
				}
				System.out.print("Choose one : ");
				Integer ind = scan.nextInt();
				try {
					System.out.println(store.containsInCollection(arrPathQuery.get(ind), store.getCurrentCollection().getNamespace().getCollectionName()));
				} catch (AtomSetException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				break;
				
			case "createQuery":
				System.out.print("Json Query : ");
				String str = scan.next();
				PathQueryParser parser = new PathQueryParser();
				arrPathQuery.add(parser.getJavaQuery(new JSONObject(str)));
				break;
				
			case "get":
				if(arrPathQuery.isEmpty()){
					System.out.println("Pas de requête en mémoire");
				}
				System.out.println("Requête disponible : ");
				Integer cpt1 = 0;
				for (PathQuery pathQuery : arrPathQuery) {
					System.out.println(cpt1+" :: "+pathQuery.getPathPredicate().toString()+" : "+pathQuery.getTerm(0).toString() + " -- "+(pathQuery.getTerm(0).isConstant()?"Cst":"Var"));
					cpt1++;
				}
				System.out.print("Choose one : ");
				Integer ind1 = scan.nextInt();
				for (String str1 : store.get(arrPathQuery.get(ind1))) {
					System.out.println(str1);
				}
				break;
			
			case "showQuery":
				Integer cpt11 = 0;
				for (PathQuery pathQuery : arrPathQuery) {
					System.out.println(cpt11+" :: "+pathQuery.getPathPredicate().toString()+" : "+pathQuery.getTerm(0).toString() + " -- "+(pathQuery.getTerm(0).isConstant()?"Cst":"Var"));
					cpt11++;
				}
				break;
				
			case "importJson":
				MongoIterable<String> colls = store.getDatabase().listCollectionNames();
				System.out.println("Collections in database : " + store.getDatabase().getName());
				for (String string : colls) {
					System.out.println("\t-- " + string);
				}
					System.out.print("Choose one or create it : ");
					String col = scan.next();
					System.out.print("\tJson's path : ");
					String pathJson = scan.next();
				try {
					store.importJsonIntoCollection(store.getCurrentCollection().getNamespace().getCollectionName(), pathJson);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;

			case "setCollection":
				MongoIterable<String> colls1 = store.getDatabase().listCollectionNames();
				System.out.println("Collections in database : " + store.getDatabase().getName());
				for (String string : colls1) {
					System.out.println("\t-- " + string);
				}
				System.out.print("Choose one or create it : ");
				String col1 = scan.next();
				System.out.println(" ");
				store.setCurrentCollection(col1);
				break;
				
			case "showCollection":
				store.showCollection(store.getCurrentCollection());
				break;
				
			case "help":
				System.out.println("--connect : Connection à la DB\n"+ "--createQuery : Creer un objet de type PathQuery\n"
						+"--exit : Sortie de programme\n"+"--help : C'est ici\n"+"--importJson : Ajout d'un document dans la colleciton courante\n"
						+ "--setCollection : Selectionne la collection courante\n"+ "--showCollection : Retourne tout les document de la collection\n"
						+"--showQuery : Retourne l'ensemble des requête en mémoire\n");	
				break;

			case "exit":
				Finish = true;
				System.out.println("Good bye, see you later!");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				System.out.println("La commande incompatible : " + input);
				break;
			}

		}

		// try {
		// // Connection au Store MongoDB
		// store = new KeyValueStoreMongoDB("localhost", 27017,
		// "test","acteurs");
		//
		// // Construction d'un pathQuerye
		// KeyValueTerm tr = new KeyValueTerm(203, Type.CONSTANT);
		// Predicate pre1 = new Predicate("info", 1);
		// Predicate pre2 = new Predicate("x", 1);
		// ArrayList<Predicate> arrPredicates = new ArrayList<Predicate>();
		// arrPredicates.add(pre1);
		// arrPredicates.add(pre2);
		// PathPredicate pp = new PathPredicate(arrPredicates);
		// PathQuery pathQuery = new PathQuery(pp, tr);
		// // Interogation du Store
		// store.showCollections();
		// store.add(pathQuery);
		// System.out.println(store.contains(pathQuery));
		//
		// System.out.println(store.isEmpty());
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (AtomSetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
