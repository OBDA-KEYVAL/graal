package fr.lirmm.graphik.graal.keyval;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.nio.channels.ShutdownChannelGroupException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xml.serialize.XMLSerializer;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;
import static com.mongodb.client.model.Projections.*;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.FileNameCompleter;

public class InterpreterMongoDB {
	private static boolean Finish = false;
	private static ArrayList<PathQuery> arrPathQuery = new ArrayList<PathQuery>();
	private static ArrayList<ArrayList<PathQuery>> arrCheckGet = new ArrayList<ArrayList<PathQuery>>();
	private static ArrayList<NoRule> arrRules = new ArrayList<NoRule>();
	
	

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	

	private static KeyValueStoreMongoDB connect() {
		try {
			Scanner scan = new Scanner(System.in);
			System.out.print(ANSI_CYAN + "\tSet Host : " + ANSI_RESET);
			String host = scan.next();
			System.out.print(ANSI_CYAN + "\tSet Port : " + ANSI_RESET);
			String port = scan.next();
			System.out.print(ANSI_CYAN + "\tChoose the database : " + ANSI_RESET);
			String db = scan.next();
			KeyValueStoreMongoDB kvs = new KeyValueStoreMongoDB(host, Integer.parseInt(port), db);
			return kvs;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void prompt(KeyValueStoreMongoDB store) {
		if (store == null) {
			System.out.print("\n" + ANSI_YELLOW + "@:@//> " + ANSI_RESET);
		} else if (store.getCurrentCollection() == null) {
			System.out.print("\n" + ANSI_YELLOW + store.getDatabase().getName() + ":@//> " + ANSI_RESET);
		} else {
			System.out.print("\n" + ANSI_YELLOW + store.getDatabase().getName() + ":"
					+ store.getCurrentCollection().getNamespace().getCollectionName() + "//> " + ANSI_RESET);
		}

	}

	private static Integer showQuery() {
		Integer cpt1 = 0;
		for (PathQuery pathQuery : arrPathQuery) {
			System.out.println(ANSI_GREEN + cpt1 + " :: " + ANSI_CYAN + pathQuery.getPathPredicate().toFieldName()
					+ ANSI_YELLOW + " => " + ANSI_CYAN + pathQuery.getTerm().toString() + " -- "
					+ (pathQuery.getTerm().isConstant() ? "Cst" : "Var") + ANSI_RESET);
			cpt1++;
		}
		System.out.print(ANSI_YELLOW+"Choose One :"+ANSI_RESET);
		return cpt1;
	}
	
	private static Integer showRule() {
		Integer cpt3 = 0;
		for (NoRule rule : arrRules) {
			System.out.println(ANSI_GREEN + cpt3 + " :: " + ANSI_CYAN + rule.toField()+ ANSI_RESET);
			cpt3++;
		}
		System.out.print(ANSI_YELLOW+"Choose One :"+ANSI_RESET);
		return cpt3;
	}
	
	private static Integer showCheckGet(){
		Integer cpt11 = 0;
		for (ArrayList<PathQuery> pathQueryChkGet : arrCheckGet) {
			System.out.println(ANSI_GREEN + cpt11 + ANSI_YELLOW + "  Check" + ANSI_CYAN + " :: "
					+ pathQueryChkGet.get(0).getPathPredicate().toFieldName() + ANSI_YELLOW + " => "
					+ ANSI_CYAN + pathQueryChkGet.get(0).getTerm().toString() + " -- "
					+ (pathQueryChkGet.get(0).getTerm().isConstant() ? "Cst" : "Var") + " || " + ANSI_YELLOW
					+ "GET" + ANSI_CYAN + " :: " + pathQueryChkGet.get(1).getPathPredicate().toFieldName()
					+ ANSI_YELLOW + " => " + ANSI_CYAN + pathQueryChkGet.get(1).getTerm().toString()
					+ " -- " + (pathQueryChkGet.get(1).getTerm().isConstant() ? "Cst" : "Var")
					+ ANSI_RESET);
			cpt11++;
		}
		System.out.print(ANSI_YELLOW+"Choose One :"+ANSI_RESET);
		return cpt11;
	}

	public static void main(String[] args) throws IOException {

		KeyValueStoreMongoDB store = null;
		Scanner scan = new Scanner(System.in);
		while (!Finish) {
			prompt(store);
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

			case "defaultConnect":
				try {
					store = new KeyValueStoreMongoDB("localhost", 27017, "test");
					Thread.sleep(2000);
				} catch (InterruptedException | ParseException e) {
					e.printStackTrace();
				}
				break;

			case "check":
				if (arrPathQuery.isEmpty()) {
					System.out.println(ANSI_RED + "Pas de requête en mémoire" + ANSI_RESET);
				} else {
					System.out.println(ANSI_CYAN + "Requête disponible : " + ANSI_RESET);
					showQuery();
					Integer ind = scan.nextInt();
					try {
						System.out
								.println(ANSI_GREEN
										+ store.containsInCollection(arrPathQuery.get(ind),
												store.getCurrentCollection().getNamespace().getCollectionName())
										+ ANSI_RESET);
					} catch (AtomSetException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				break;
				
			case "checkget":
				if (arrCheckGet.isEmpty()) {
					System.out.println(ANSI_RED + "No query in cache ..." + ANSI_RESET);
				} else {
					System.out.println("Result Collection :");

					MongoIterable<String> colls1 = store.getDatabase().listCollectionNames();
					System.out.println(
							ANSI_CYAN + "Collections in database : " + store.getDatabase().getName() + ANSI_RESET);
					for (String string : colls1) {
						System.out.println(ANSI_GREEN + "\t-- " + string + ANSI_RESET);
					}
					System.out.print(ANSI_CYAN + "Choose one or create it : " + ANSI_RESET);
					String resColname = scan.next();
					System.out.println(" ");

					MongoCollection<Document> resCol = store.getDatabase().getCollection(resColname);

					for (ArrayList<PathQuery> ind1 : arrCheckGet) {

						try {
							ArrayList<String> reschkget = store.checkGet(ind1.get(0), ind1.get(1),
									store.getCurrentCollection().getNamespace().getCollectionName());
							for (String strreschkget : reschkget) {
								resCol.insertOne(Document.parse(strreschkget));
							}
							System.out.println(ANSI_GREEN + reschkget);
						} catch (AtomSetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				break;
				
			case "bufTest":
				store.test("show");
				break;
			
			case "bufTestFlush":
				store.test("flush");
				break;
				
			case "bufTestWrite":
				store.test("writeTest");
				break;
				
			
			case "formatResults":
					store.formatResult();
				break;
				
			case "xmarkImport":
				try{
					
					System.out.print(ANSI_CYAN + "\tXML's path : " + ANSI_RESET);
					String pathXml = scan.next();
					String xml = FileUtils.readFileToString(new File(pathXml));
					System.out.println(org.json.XML.toJSONObject(xml).toString());
					store.getCurrentCollection().insertOne(Document.parse(org.json.XML.toJSONObject(xml).toString()));
//					JSONObject xmlJsonObj = XML.toJSONObject(file.readLine());
//					String pretyStr = xmlJsonObj.toString();
//					System.out.println(xmlJsonObj.toString());
				}catch (JSONException e){
					
					System.out.println(e.toString());
				}
				break;

//			case "checkgetAll":
//				if(arrCheckGet.isEmpty()){
//					System.out.println(ANSI_RED+"No query in cache ..."+ANSI_RESET);
//				}else{
//					try{
//						for (ArrayList<PathQuery> arr : arrCheckGet) {
//							System.out.println(ANSI_GREEN+store.checkGet(arr.get(0),arr.get(1) , store.getCurrentCollection().getNamespace().getCollectionName()));
//						}
//						
//					}catch(AtomSetException e){
//						e.printStackTrace();
//					}
//				}
//				break;

			case "createQuery":
				System.out.print(ANSI_CYAN + "Json Query : " + ANSI_RESET);
				String str = scan.next();
				PathQueryParser parser = new PathQueryParser();
				arrPathQuery.add(parser.getJavaQuery(new JSONObject(str)));
				break;

			case "createCheckGet":
				if (arrPathQuery.isEmpty()) {
					System.out.println(ANSI_RED + "Pas de requête en mémoire" + ANSI_RESET);
				} else {
					System.out.println(ANSI_WHITE + "Requête disponible" + ANSI_YELLOW + " CHECK : " + ANSI_RESET);
					showQuery();
					Integer ind1 = scan.nextInt();
					System.out.println(ANSI_WHITE + "Requête disponible " + ANSI_YELLOW + "GET : " + ANSI_RESET);
					showQuery();
					Integer ind2 = scan.nextInt();
					ArrayList<PathQuery> queryCheckGet = new ArrayList<PathQuery>();
					queryCheckGet.add(arrPathQuery.get(ind1));
					queryCheckGet.add(arrPathQuery.get(ind2));
					arrCheckGet.add(queryCheckGet);
				}
				break;

			case "createNoRule":
				Parser p1 = new ParserJsonToJava();
				System.out.print(ANSI_CYAN + "Body : " + ANSI_RESET);
				String body = scan.next();
				System.out.print(ANSI_CYAN + "Head : " + ANSI_RESET);
				String head = scan.next();
				String res = "{\"body\": " + body + ", \"head\": " + head + "}";
				NoRule n1 = (NoRule) p1.parseRule(new JSONObject(res));
				arrRules.add(n1);
				break;

			case "showNoRules":
				if (arrRules.isEmpty()) {
					System.out.println(ANSI_RED + "Pas de régle en mémoire" + ANSI_RESET);
				} else {
					for (NoRule rls : arrRules) {
						System.out.println(rls.toString());
					}
				}
				break;

			case "refCheckGet":
				if (arrCheckGet.isEmpty() || arrRules.isEmpty()) {
					System.out.println(ANSI_RED + "No query or rule in cache ...");
				} else {

					showCheckGet();
					Integer i1 = scan.nextInt();
//					System.out.println("Record it in current collection ? (Yes/No)");
//					String str1 = scan.next();
					PathQueryBackwardChainer pthQBC = new PathQueryBackwardChainer(arrCheckGet.get(i1).get(0),
							arrCheckGet.get(i1).get(1), arrRules);
					ArrayList<ArrayList<PathQuery>> reform = pthQBC.backwardNaif();
					arrCheckGet.addAll(reform);
//					if(str1 == "Yes"){
//						for(ArrayList<PathQuery> arr : reform){
//							try {
//								store.add(arr);
//							} catch (AtomSetException e) {
//								e.printStackTrace();
//							}
//						}
//					}
					
				}
				break;
				
			case "refCheckGetOpti":
				if (arrCheckGet.isEmpty() || arrRules.isEmpty()) {
					System.out.println(ANSI_RED + "No query or rule in cache ...");
				} else {

					showCheckGet();
					Integer i1 = scan.nextInt();
//					System.out.println("Record it in current collection ? (Yes/No)");
//					String str1 = scan.next();
					PathQueryBackwardChainer pthQBC = new PathQueryBackwardChainer(arrCheckGet.get(i1).get(0),
							arrCheckGet.get(i1).get(1), arrRules);
					ArrayList<ArrayList<PathQuery>> reform = pthQBC.backwardOpti();
					arrCheckGet.addAll(reform);
//					if(str1 == "Yes"){
//						for(ArrayList<PathQuery> arr : reform){
//							try {
//								store.add(arr);
//							} catch (AtomSetException e) {
//								e.printStackTrace();
//							}
//						}
//					}
					
				}
				break;

			case "get":
				if (arrPathQuery.isEmpty()) {
					System.out.println(ANSI_RED + "Pas de requête en mémoire" + ANSI_RESET);
				} else {
					System.out.println(ANSI_CYAN + "Requête disponible : " + ANSI_RESET);
					showQuery();
					Integer ind1 = scan.nextInt();
					for (String str1 : store.get(arrPathQuery.get(ind1))) {
						System.out.println(ANSI_GREEN + str1 + ANSI_RESET);
					}
				}
				break;

			case "dropCol":
				if (store.getCurrentCollection() == null) {
					System.out.println("nothing to drop ...");
				} else {
					try {
						store.dropCollection();
					} catch (AtomSetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(ANSI_GREEN + "Current collection droped..." + ANSI_RESET);
				}
				break;

			case "dropMem":
				arrCheckGet = new ArrayList<ArrayList<PathQuery>>();
				arrPathQuery = new ArrayList<PathQuery>();
				System.out.println(ANSI_GREEN + "Cache clear" + ANSI_RESET);
				break;
				
			case "dropCheckGet":
				arrCheckGet = new ArrayList<ArrayList<PathQuery>>();
				System.out.println(ANSI_GREEN + "Cache clear" + ANSI_RESET);
				break;

			case "showQuery":
				if (arrPathQuery.isEmpty()) {
					System.out.println(ANSI_RED + "No query in cache ..." + ANSI_RESET);
				} else {
					showQuery();
				}
				break;
				
				
			case "showRule":
				if(arrRules.isEmpty()){
					System.out.println(ANSI_RED + "No rules in cache ..." + ANSI_RESET);
				} else{
					showRule();
				}
				break;

			case "showCheckGet":
				if (arrCheckGet.isEmpty()) {
					System.out.println(ANSI_RED + "No query in cache ..." + ANSI_RESET);
				} else {
					showCheckGet();
				}
				break;

			case "importJson":
				if (store.getCurrentCollection() == null) {
					System.out.println("Before import set a current collection ...");
				} else {
					System.out.print(ANSI_CYAN + "\tJson's path : " + ANSI_RESET);
					String pathJson = scan.next();
					try {
						store.importJsonIntoCollection(store.getCurrentCollection().getNamespace().getCollectionName(),
								pathJson);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				break;

			case "saveQuery":
				for (PathQuery pquery : arrPathQuery) {
					try {
						store.add(pquery);
					} catch (AtomSetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;

			case "saveCheckGet":
				for (ArrayList<PathQuery> chkget : arrCheckGet) {
					Document doc = new Document().append("@check", JSON.parse(chkget.get(0).exportJson().toString())).append("@get", JSON.parse(chkget.get(1).exportJson().toString()));
					store.getCurrentCollection().insertOne(doc);
				}
				break;

			case "loadQuery":
				arrPathQuery.clear();
				PathQueryParser pars = new PathQueryParser();
				MongoCursor<Document> cur = store.getCurrentCollection().find().projection(excludeId()).iterator();
				while (cur.hasNext()) {
					Document document = (Document) cur.next();
					arrPathQuery.add(pars.getJavaQuery(new JSONObject(document.toJson())));
				}
				break;

			case "loadCheckGet":
				arrCheckGet.clear();
				PathQueryParser pars1 = new PathQueryParser();
				MongoCursor<Document> cur1 = store.getCurrentCollection().find().iterator();
				while (cur1.hasNext()) {
					Document document = (Document) cur1.next();
					ArrayList<PathQuery> arrCk = new ArrayList<PathQuery>();
					arrCk.add(pars1.getJavaQuery(new JSONObject(((Document)document.get("@check")).toJson())));
					arrCk.add(pars1.getJavaQuery(new JSONObject(((Document)document.get("@get")).toJson())));
					arrCheckGet.add(arrCk);

				}
				break;
				
			case "loadRule":
				arrRules.clear();
				RuleParser parsRul = new RuleParser();
				MongoCursor<Document> cur2 = store.getCurrentCollection().find().projection(excludeId()).iterator();
				while (cur2.hasNext()) {
					Document document = (Document) cur2.next();
					arrRules.add(parsRul.getJavaRule(new JSONObject(document.toJson())));
				}
				break;

			case "setCollection":
				MongoIterable<String> colls1 = store.getDatabase().listCollectionNames();
				System.out
						.println(ANSI_CYAN + "Collections in database : " + store.getDatabase().getName() + ANSI_RESET);
				for (String string : colls1) {
					System.out.println(ANSI_GREEN + "\t-- " + string + ANSI_RESET);
				}
				System.out.print(ANSI_CYAN + "Choose one or create it : " + ANSI_RESET);
				String col1 = scan.next();
				System.out.println(" ");
				store.setCurrentCollection(col1);
				break;

			case "showCollection":
				if (store.getCurrentCollection() == null) {
					System.out.println(ANSI_RED + "Pas de collection courante ...." + ANSI_RESET);
				} else {
					store.showCollection(store.getCurrentCollection());
				}
				break;

			case "help":
				System.out.println("--connect : Connection à la DB\n"
						+ "-- check : vérfie la présence d'un PathPredicat dans une collection\n"
						+ "-- checkget : intéroge le store sous la forme CheckGet\n"
						+ "-- createCheckGet : Creer un objet de type ArrayList<PathQuery>\n"
						+ "-- createQuery : Creer un objet de type PathQuery\n" 
						+ "-- exit : Sortie de programme\n"
						+ "-- defaultConnect : Connection localhost:27017@Test\n" 
						+ "-- dropMem : Efface le cache\n"
						+ "-- dropCol : Efface la collection coutante\n"
						+ "-- get : retourne toute les documents satisfaits par la requête\n" 
						+ "-- help : C'est ici\n"
						+ "-- importJson : Ajout d'un document dans la colleciton courante\n"
						+ "-- refCheckGet : Reformulation de la requête checkget"
						+ "-- setCollection : Selectionne la collection courante\n"
						+ "-- showCheckGet : Retourne toute les requêtes checkGet en mémoire\n "
						+ "-- showCollection : Retourne tout les document de la collection\n"
						+ "-- loadRule  : Charge la collection courante en régles\n"
						+ "-- loadQuery : Charge la collection courante en requêtes\n"
						+ "-- saveQuery : Ajout toutes les requêtes en mémoire dans la collection courante\n"
						+ "-- showQuery : Retourne l'ensemble des requête en mémoire\n");
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
