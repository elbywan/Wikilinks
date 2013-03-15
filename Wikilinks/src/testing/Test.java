package testing;
import graph.EfficientGraph;
import graph.Node;
import gui.GUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.SwingUtilities;
import parsing.FileThreadedWikiParser;
import utils.Toolz;

public class Test {

	static Calendar c;

	/*
	public enum Relation implements RelationshipType{
		LINKS
	}
	*/

	

	/*
	public static void close_database(GraphDatabaseService graphDb){

		Transaction tx = graphDb.beginTx();

		int i = 0;
		try{
			Iterable<Node> r = graphDb.getAllNodes();
			for(Node no : r){
				for(Relationship ro : no.getRelationships())
					ro.delete();
				no.delete();
				i++;
			}
			System.out.println("Deleted "+i+" nodes.");
			tx.success();

		} finally { tx.finish(); graphDb.shutdown();}

	}*/
	
	public static void old_graphdb_test(){
		/*
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase("database/");
		Index<Node> nodes;
		Node n;
		Node n2;
		
		
		Transaction tx = graphDb.beginTx();
		
		try {
			File f = new File("Test Files/graph_output.txt");
			BufferedReader r = new BufferedReader(new FileReader(f),4096);

			String s;
			int number = 0;
			
			nodes = graphDb.index().forNodes( "nodes" );
			
			while((s = r.readLine()) != null){
				
				number++;
				
				String[] splitted = s.split("[|]");

				n = graphDb.createNode();
				n.setProperty("name", splitted[0]);
				nodes.add(n, "name", splitted[0]);


				for(int i = 1; i < splitted.length; i++){
					
					n2 = graphDb.createNode();
					n2.setProperty("name", splitted[i]);
					nodes.add(n2, "name", splitted[i]);

					if(splitted[i].compareTo(splitted[0]) != 0)
						n.createRelationshipTo(n2, Relation.LINKS);
					splitted[i] = null;
				}
				number++;
				if(number%200 == 0){
					tx.success();
					tx.finish();
					tx = graphDb.beginTx();
					System.out.println(number);
				}
				
			}
			Iterable<Relationship> rel = nodes.get("name", "Algorithmique").getSingle().getRelationships();
			for(Relationship re : rel){
				System.out.println("["+re.getStartNode().getProperty("name")+"|->|"+re.getEndNode().getProperty("name")+"]");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally { tx.finish(); graphDb.shutdown(); }
		*/
		
		//close_database(graphDb);

		/*
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase("database/");
		Transaction tx = graphDb.beginTx();
		Index<Node> nodes = graphDb.index().forNodes( "nodes" );

		try{



			Node n = graphDb.createNode();
			n.setProperty("title","one");
			Node n2 = graphDb.createNode();
			n2.setProperty("title","two");
			n.createRelationshipTo(n2, Relation.LINKS);
			nodes.add(n,"title", "one");
			nodes.add(n2, "title", "two");

			Iterable<Relationship> rel = nodes.get("title", "two").getSingle().getRelationships();
			for(Relationship re : rel){
				System.out.println("["+re.getStartNode().getProperty("title")+"|->|"+re.getEndNode().getProperty("title")+"]");
			}


			Iterable<Node> r = graphDb.getAllNodes();

			for(Node no : r){
				for(Relationship ro : no.getRelationships())
					ro.delete();
				no.delete();
			}
			tx.success();

		} finally { tx.finish(); graphDb.shutdown();}
		 */
	}
	
	public static void test_file(){
		File f = new File("Test Files/frwiki-latest-pages-articles.xml");
		//File f = new File("Test Files/xmltest.xml");
		
		FileThreadedWikiParser p = new FileThreadedWikiParser(f,20);
		p.setPPT(2);
		p.setCapacity(2000);
		p.parse();
	}

	/*
	public static void old_ramtest(){
		System.out.println("Pre traitement [1] [DEBUT] @ ["+c.get(Calendar.HOUR_OF_DAY)+"H:"+c.get(Calendar.MINUTE)+"M:"+c.get(Calendar.SECOND)+"S]");
		RamThreadedWikiParser p = new RamThreadedWikiParser(f,6);
		p.setCapacity(200);
		p.parse();
		c = Calendar.getInstance();
		System.out.println("Pre traitement [1] [FIN]   @ ["+c.get(Calendar.HOUR_OF_DAY)+"H:"+c.get(Calendar.MINUTE)+"M:"+c.get(Calendar.SECOND)+"S]");

		try {
			int id = p.getGraph().get_id("Algorithmique");
			Set<DefaultEdge> s = p.getGraph().getGraphObject().edgesOf(id);
			for(DefaultEdge d : s){
				System.out.println("[ "+p.getGraph().get_name(p.getGraph().getGraphObject().getEdgeSource(d))+" -> "+p.getGraph().get_name(p.getGraph().getGraphObject().getEdgeTarget(d))+" ]");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	} */
	
	/*
	public static void jgrapht_test(){
		try {
			File f = new File("Test Files/graph_output.txt");
			BufferedReader r = new BufferedReader(new FileReader(f),4096);

			HashMap<String,Integer> map = new HashMap<String, Integer>();
			SimpleGraph<Integer, DefaultEdge> g = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
			String s;
			int number = 0;
			int max_index = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					System.out.println(number);
				
				String[] splitted = s.split("[|]");
				g.addVertex(max_index);
				map.put(splitted[0], max_index);
				max_index++;
				for(int i = 1; i < splitted.length; i++){
					if(!map.containsKey(splitted[i])){
						g.addVertex(max_index);
						map.put(splitted[i], max_index);
						max_index++;
					}
				}
			}
			
			r.close();
			
			System.out.println("======================");
			
			r = new BufferedReader(new FileReader(f),4096);
			number = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					System.out.println(number);
				//System.out.println(s);
				
				String[] splitted = s.split("[|]");
				
				for(int i = 1; i < splitted.length; i++){
					try{
						if(map.get(splitted[0]) != map.get(splitted[i]))
							g.addEdge(map.get(splitted[0]), map.get(splitted[i]));
						splitted[i] = null;
					} catch (IllegalArgumentException ill){
						ill.printStackTrace();
						System.out.println("["+map.get(splitted[0])+"]");
						System.out.println("["+splitted[0]+"]");
						System.out.println("["+map.get(splitted[i])+"]");
						System.out.println("["+splitted[i]+"]");
						System.exit(0);
					}
				}
				
			}
			
			Set<DefaultEdge> edge_set = g.edgesOf(map.get("Algorithmique"));
			for(DefaultEdge d : edge_set){
				System.out.println("[ "+g.getEdgeSource(d)+" -> "+g.getEdgeTarget(d)+" ]");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}*/
	
	public static void simplepsql_test(){
		String url = "jdbc:postgresql://localhost:20002/Wikilinks";
		Properties props = new Properties();
		props.setProperty("user","elby");
		props.setProperty("password","gnap");
		try {
			Connection connect = DriverManager.getConnection(url, props);
			Statement h;
			String s = new String("wikilinks_tableindex");
			try {
				h = connect.createStatement();
				h.executeUpdate("CREATE TABLE "+s+" (title varchar(100), id int)");
				
				connect.close();

			} catch (SQLException e) {
				System.out.println(">Update error.");
			}
		} catch (SQLException e) {
			System.out.println("Unable to connect to the database : "+e.getMessage());
			System.exit(1);
		} catch(Exception e){
			System.out.println( "Unable to load the driver class." );
			System.exit(1);
		}
	}
	
	public static String put_query(String s){
		return "INSERT INTO wikilinks_index (\"title\") VALUES ('"+s+"')";
	}
	
	public static String get_query(String s){
		return "SELECT id FROM wikilinks_index WHERE title='"+s+"'";
	}
	
	public static int get(Statement h,String s) throws SQLException{
		ResultSet set = h.executeQuery(get_query(s));
		set.next();
		return set.getInt("id");
	}
	
	public static void clear_table(Statement h) throws SQLException{
		h.executeUpdate("DELETE FROM wikilinks_index");
		h.executeQuery("SELECT SETVAL((SELECT pg_get_serial_sequence('wikilinks_index', 'id')), 1, false)");
	}
	
	/*
	public static void complex_test(){
		
		
		try {
			
			File f = new File("Test Files/graph_output.txt");
			String url = "jdbc:postgresql://localhost:20002/Wikilinks";
			Properties props = new Properties();
			props.setProperty("user","elby");
			props.setProperty("password","gnap");
			Connection connect = DriverManager.getConnection(url, props);
			Statement h = connect.createStatement();
			clear_table(h);
			
			BufferedReader r = new BufferedReader(new FileReader(f),4096);
			
			SimpleGraph<Integer, DefaultEdge> g = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
			String s;
			int number = 0;
			int max_index = 0;
			
			while((s = r.readLine()) != null){
				s = s.replace("'", "\\'");
				number++;
				if(number%50000 == 0)
					System.out.println(number);
				
				String[] splitted = s.split("[|]");
				
				ResultSet result = h.executeQuery(get_query(splitted[0]));
				if(!result.next()){
					max_index++;
					g.addVertex(max_index);
					h.executeUpdate(put_query(splitted[0]));
				}
				
				for(int i = 1; i < splitted.length; i++){
					result = h.executeQuery(get_query(splitted[i]));
					if(!result.next()){
						g.addVertex(max_index);
						h.executeUpdate(put_query(splitted[i]));
						max_index++;
					}
				}
			}
			
			r.close();
			
			System.out.println("======================");
			
			r = new BufferedReader(new FileReader(f),4096);
			number = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					System.out.println(number);
				//System.out.println(s);
				
				String[] splitted = s.split("[|]");
				
				for(int i = 1; i < splitted.length; i++){
					try{
						if(get(h,splitted[0]) != get(h,splitted[0]))
							g.addEdge(get(h,splitted[0]), get(h,splitted[i]));
						splitted[i] = null;
					} catch (IllegalArgumentException ill){
						ill.printStackTrace();
						System.out.println("["+get(h,splitted[0])+"]");
						System.out.println("["+splitted[0]+"]");
						System.out.println("["+get(h,splitted[i])+"]");
						System.out.println("["+splitted[i]+"]");
						System.exit(0);
					}
				}
				
			}
			
			Set<DefaultEdge> edge_set = g.edgesOf(get(h,"Algorithmique"));
			for(DefaultEdge d : edge_set){
				System.out.println("[ "+g.getEdgeSource(d)+" -> "+g.getEdgeTarget(d)+" ]");
			}
			
			connect.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	*/
	
	
	public static int step2(){
		
		int max_index = -1;
		
		try {
			File f = new File("Test Files/graph_output.txt");
			File f2 = new File("Test Files/graph_output_integers.txt");
			File f3 = new File("Test Files/graph_output_index.txt");

			if(f3.exists())
				f3.delete();
			f3.createNewFile();
			
			BufferedReader r = new BufferedReader(new FileReader(f),4096);
			BufferedWriter w = new BufferedWriter(new FileWriter(f3),4096);

			HashMap<String,Integer> map = new HashMap<String, Integer>();
			String s;
			int number = 0;
			max_index = 0;
			StringBuilder builder;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					Toolz.debug_println(number);
				
				builder = new StringBuilder();
				String[] splitted = s.split("[|]");
				map.put(splitted[0], max_index);
				builder.append(Integer.toString(max_index)); builder.append("|");
				builder.append(splitted[0]);  builder.append("\n");
				w.write(builder.toString());
				max_index++;
				for(int i = 1; i < splitted.length; i++){
					if(!map.containsKey(splitted[i])){
						builder = new StringBuilder();
						map.put(splitted[i], max_index);
						builder.append(Integer.toString(max_index)); builder.append("|");
						builder.append(splitted[i]); builder.append("\n");
						w.write(builder.toString());
						max_index++;
					}
				}
			}
			
			r.close();
			w.close();
			
			Toolz.debug_println("=========");
			
			if(f2.exists())
				f2.delete();
			f2.createNewFile();
			r = new BufferedReader(new FileReader(f),4096);
			w = new BufferedWriter(new FileWriter(f2),4096);
			number = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					Toolz.debug_println(number);
				
				String[] splitted = s.split("[|]");
				w.write(map.get(splitted[0]).toString());
				for(int i = 1; i < splitted.length; i++){
					w.write("|");
					w.write(map.get(splitted[i]).toString());
				}
				w.write("\n");
			}
			r.close();
			w.close();
			
			map.clear();
			map = null;
			
			Toolz.debug_println("=========");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return max_index;
			
	}
	/*
	public static SimpleGraph makeJGraphT(){
		
		SimpleGraph<Integer, DefaultEdge> g = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		
		try {
			File f = new File("Test Files/graph_output_index.txt");
			File f2 = new File("Test Files/graph_output_integers.txt");
			BufferedReader r = new BufferedReader(new FileReader(f),4096);

			String s;
			int number = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					Toolz.debug_println(number);
				
				String[] splitted = s.split("[|]");
				g.addVertex(Integer.parseInt(splitted[0]));
			}
			
			r.close();
			
			Toolz.debug_println("======================");
			
			r = new BufferedReader(new FileReader(f2),4096);
			number = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					Toolz.debug_println(number);
				//Toolz.debug_println(s);
				
				String[] splitted = s.split("[|]");
				int src = Integer.parseInt(splitted[0]);
				
				for(int i = 1; i < splitted.length; i++){
					int dest = Integer.parseInt(splitted[i]);
					if(src != dest)
						g.addEdge(src,dest);
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return g;
	}*/
	
	public static Node[] makeGraph(){
		
		
		int length = 0;
		
		try {
			File f = new File("Test Files/graph_output_index.txt");
			BufferedReader r = new BufferedReader(new FileReader(f),4096);
			while(r.readLine() != null)
				length++;
			r.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		
		
		Node[] graph = new Node[length];
		
		try {
			File f = new File("Test Files/graph_output_index.txt");
			File f2 = new File("Test Files/graph_output_integers.txt");
			BufferedReader r = new BufferedReader(new FileReader(f),4096);

			String s;
			int number = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					Toolz.debug_println(number);
				
				String[] splitted = s.split("[|]");
				graph[Integer.parseInt(splitted[0])] = new Node();
			}
			
			r.close();
			
			Toolz.debug_println("======================");
			
			r = new BufferedReader(new FileReader(f2),4096);
			number = 0;
			
			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					Toolz.debug_println(number);
				//Toolz.debug_println(s);
				
				String[] splitted = s.split("[|]");
				int src = Integer.parseInt(splitted[0]);
				
				for(int i = 1; i < splitted.length; i++){
					int dest = Integer.parseInt(splitted[i]);
					if(src != dest){
						graph[dest].add_link_from(src);
						graph[src].add_link_to(dest);
					}
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}

	public static int[] largeur(int starting_node, int distance, int[] graph){
		int[] result = new int[1];
		result[0] = starting_node;

		int pos = -1;

		for(int j = 0; j < graph.length; j++){
			if(graph[j] == starting_node){
				if(pos == -1){
					for(pos = Math.max(0, j-distance); pos < j; pos++){
						result = Arrays.copyOf(result, result.length+1);
						result[result.length-1] = graph[pos];
					}
					pos = j+1;
				} else {
					for(int old_pos = pos; pos < j && pos < old_pos + distance; pos++){
						result = Arrays.copyOf(result, result.length+1);
						result[result.length-1] = graph[pos];
					}
					pos = Math.max(pos,j-distance);
					for(; pos < j; pos++){
						result = Arrays.copyOf(result, result.length+1);
						result[result.length-1] = graph[pos];
					}
					pos++;
				}
			}
		}
		if(pos != -1){
			for(int old_pos = pos; pos < graph.length && pos < old_pos + distance; pos++){
				result = Arrays.copyOf(result, result.length+1);
				result[result.length-1] = graph[pos];
			}
		}


		//Arrays.sort(result);

		return result;
	}
	
	public static int[] req_test(String req, EfficientGraph g) throws IOException{
		Toolz.getTime("\t> Dealing with a subrequest {"+req+"}  \t@ ");
		int reference = 0;
		char mode = ' ';
		String word = "";
		int distance = 1;
		for(int k = 0; k < req.length(); k++){
			if(reference == 0 && req.charAt(k) == 'P')
				mode = 'P';
			else if(reference == 0 && req.charAt(k) == 'L')
				mode = 'L';
			else if(reference == 0 && req.charAt(k) == '<'){
				reference++;
				continue;
			}
			else if(reference == 1 && req.charAt(k) == '>'){
				reference++;
				continue;
			}
			else if(reference == 1){
				word += req.charAt(k);
			}
			else if(reference == 2){
				while(req.charAt(k) != '=')
					k++;
				k++;
				while(req.charAt(k) == ' ')
					k++;
				distance = Integer.parseInt(req.substring(k));
				break;
			}
		}
		
		Toolz.debug_println("\t["+mode+" <"+word+"> "+distance+"]");
		
		return null;
	}
	
	public static void main(String[] args) throws InterruptedException {
		/*
		Toolz.getTime("[Step one]   Parsing original ~7GB File --[TO]->   Trimmed down file] \t@ ");
		
		test_file();

		Toolz.getTime("[Step two]   Parsing trimmed file       --[TO]->   Index file & Graph file \t@ ");
		
		try {
			Fragment.fragmentFile(Parameters.file_firstStep, Parameters.file_Index, Parameters.file_Trimmed);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		/*
		Toolz.getTime("[Step three] Parsing graph file         --[TO]->   Making a Graph  \t@ ");
		
		EfficientGraph g = new EfficientGraph("Test Files/graph_output_index.txt","Test Files/graph_output_integers.txt");
		g.buildGraph();
		
		Toolz.getTime("[Step four] REQUESTS                    --[TO]->   Results	  \t@ ");
		
		try {
			int[] dist = g.PDistance(Fragment.getId("choline acétyltransférase",Parameters.file_Index), 3);
			for(int i = 0; i < dist.length; i++){
				System.out.print(Fragment.getTitle(dist[i], Parameters.file_Index));
				System.out.print(" | ");
			}
			System.out.println();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		
		System.out.println("-----------");
		System.out.println("Outgoing : ");
		int[] out = g.get_outgoing(0);
		for(int i = 0; i < out.length; i++){
			System.out.print(out[i]);
			System.out.print(" | ");
		}
		System.out.println();
		System.out.println("-----------");
		System.out.println("Incoming : ");
		int[] in = g.get_incoming(0);
		for(int i = 0; i < in.length; i++){
			System.out.print(in[i]);
			System.out.print(" | ");
		}
		System.out.println();
		System.out.println("-----------");
		*/
		/*
		try{
			System.out.println(Fragment.getId("Moulins (Allier)", Parameters.file_Index));
			System.out.println(Fragment.getId("Les Exilés", Parameters.file_Index));
			String[] res = Fragment.getTitle(new int[]{ 5987372, 550, 3034392 }, Parameters.file_Index);
			for(int i = 0; i < res.length; i++){
				System.out.println(res[i]);
			}
		} catch(IOException e){ e.printStackTrace(); }
		*/
		/*
		BufferedReader r_req = null;
		try{
			Toolz.getTime("Opening request file ["+Parameters.input_Requests+"]  \t@ ");
			File requests = new File(Parameters.input_Requests);
			r_req = new BufferedReader(new FileReader(requests));
			
			String line;
			while((line = r_req.readLine()) != null){
				String[] criteres = line.split(",");
				for(int i = 0; i < criteres.length; i++){
					System.out.print("|");
					int reference = 0;
					char mode = ' ';
					String word = "";
					int distance = 1;
					for(int k = 0; k < criteres[i].length(); k++){
						if(reference == 0 && criteres[i].charAt(k) == 'P')
							mode = 'P';
						else if(reference == 0 && criteres[i].charAt(k) == 'L')
							mode = 'L';
						else if(reference == 0 && criteres[i].charAt(k) == '<'){
							reference++;
							continue;
						}
						else if(reference == 1 && criteres[i].charAt(k) == '>'){
							reference++;
							continue;
						}
						else if(reference == 1){
							word += criteres[i].charAt(k);
						}
						else if(reference == 2){
							while(criteres[i].charAt(k) != '=')
								k++;
							k++;
							while(criteres[i].charAt(k) == ' ')
								k++;
							distance = Integer.parseInt(criteres[i].substring(k));
							break;
						}
					}
					System.out.print("#"+mode+"#"+word+"#"+distance+"===");
					System.out.print(criteres[i]);
				}
				System.out.println();
			}
			
			
			
		} catch(IOException i) {
			i.printStackTrace();
		} finally {
			try {
				r_req.close();
			} catch (IOException e) {}
		}*/
		/*
		File f = new File("Test Files/reponses-10.txt");
		try {
			Toolz.printFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		/*
		String a = " P <harry Somers> <= 2, P <harry Somers> <= 3, L <le Corbeau (film, 1963)> <= 4, P <le Corbeau (film, 1963)> <= 2, P <day the World Ended> <= 2, L <la Chambre des tortures (film, 1961)> <= 13, L <harry Homer> <= 23, P <harry Ayers> <= 3, L <championnat d'Allemagne de hockey sur glace 1995-1996> <= 28, L <harry Parke> <= 23";
		
		try {
			System.out.println(Fragment.getId(a, Parameters.file_Index));
		} catch (IOException e) { e.printStackTrace(); }
		*/
		/*
		String[] s = requestParser.splitting(a,',');
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
		 */
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});	
		
		System.out.println(System.getProperty("file.encoding"));
		//Toolz.getTime("[End]   \t@ ");
	}

}
