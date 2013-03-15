package options;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Parameters {

	public static boolean	 	debug				= false;
	
	public static String 		file_firstStep		= "Test Files/graph_output.txt";
	public static String 		file_Index			= "Test Files/graph_output_index.txt";
	public static String 		file_Trimmed		= "Test Files/graph_output_integers.txt";
	public static String		file_Content		= "Test Files/frwiki-latest-pages-articles.xml";

	
	public static String 		input_Requests		= "Test Files/requetes-10.txt";
	public static String 		output_Responses	= "Test Files/reponses-10.new.txt";
	public static String 		file_Timer			= "Test Files/requetes_timer.txt";
	
	public static int 			parse_nbThreads 	=  6;
	public static int 			parse_maxPages 		= -1;
	public static int 			parse_PPT			=  2;
	public static int 			parse_capacity	 	=  2000;
	
	public static boolean		request_memfirst	= false;
	
	public static String		properties_path     = "options.txt";
	
	public static String		icon_path			= "wklnk.png";
	public static String		char_encoding		= "UTF-8";
	
	
	private static void set_default_opts(){
		
		debug = false;
		
		file_firstStep		= "Test Files/graph_output.txt";
		file_Index			= "Test Files/graph_output_index.txt";
		file_Trimmed		= "Test Files/graph_output_integers.txt";
		file_Content		= "Test Files/frwiki-latest-pages-articles.xml";

		input_Requests		= "Test Files/requetes-10.txt";
		output_Responses	= "Test Files/reponses-10.new.txt";
		file_Timer			= "Test Files/requetes_timer.txt";
		
		parse_nbThreads 	=  6;
		parse_maxPages 		= -1;
		parse_PPT			=  2;
		parse_capacity	 	=  2000;
		
		char_encoding		= "UTF-8";
		
		request_memfirst	= false;
		
	}

	/**
	 * Initialise les paramètres à partir d'un fichier d'options.
	 * Par défaut ce fichier se trouve à la racine du programme et se nomme options.txt
	 * 
	 * Si ce fichier n'existe pas (ou si la lecture échoue),on crée un fichier avec les options par défaut.
	 * 
	 * @throws IOException Si une erreur d'entrée / sortie se produit durant la lecture ou l'écriture du fichier options.
	 */
	public static void init_params() throws IOException {
		
		Properties props = new Properties();
		
		File file_props = new File(properties_path);
		
		if(!file_props.exists()){
			System.out.println("Création du fichier options par défaut.");
			
			set_default_opts();
			
			props.setProperty("debug", "false");
			
			props.setProperty("file_firstStep", "Test Files/graph_output.txt");
			props.setProperty("file_Index", "Test Files/graph_output_index.txt");
			props.setProperty("file_Trimmed", "Test Files/graph_output_integers.txt");
			props.setProperty("file_Content", "Test Files/frwiki-latest-pages-articles.xml");
			
			props.setProperty("input_Requests", "Test Files/requetes-10.txt");
			props.setProperty("output_Responses", "Test Files/reponses-10.new.txt");
			props.setProperty("file_Timer", "Test Files/requetes_timer.txt");
			
			props.setProperty("parse_nbThreads", "6");
			props.setProperty("parse_maxPages", "-1");
			props.setProperty("parse_PPT", "2");
			props.setProperty("parse_capacity", "2000");
			
			props.setProperty("request_memfirst", "false");
			
			props.setProperty("char_encoding", "UTF-8");
			
			FileOutputStream out = new FileOutputStream(file_props);
			props.store(out, "Wikilinks - options");
			out.close();
			
		} else {
			
			FileInputStream in = new FileInputStream(properties_path);
			props.load(in);
			in.close();
			
			debug = Boolean.parseBoolean(props.getProperty("debug","false"));
			
			file_firstStep = props.getProperty("file_firstStep", "Test Files/graph_output.txt");
			file_Index = props.getProperty("file_Index", "Test Files/graph_output_index.txt");
			file_Trimmed = props.getProperty("file_Trimmed", "Test Files/graph_output_integers.txt");
			file_Content = props.getProperty("file_Content", "Test Files/frwiki-latest-pages-articles.xml");
			
			input_Requests = props.getProperty("input_Requests", "Test Files/requetes-10.txt");
			output_Responses = props.getProperty("output_Responses", "Test Files/reponses-10.new.txt");
			file_Timer = props.getProperty("file_Timer", "Test Files/requetes_timer.txt");
			
			parse_nbThreads = Integer.parseInt(props.getProperty("parse_nbThreads", "6"));
			parse_maxPages = Integer.parseInt(props.getProperty("parse_maxPages", "-1"));
			parse_PPT = Integer.parseInt(props.getProperty("parse_PPT", "2"));
			parse_capacity = Integer.parseInt(props.getProperty("parse_capacity", "2000"));
			
			request_memfirst = Boolean.parseBoolean(props.getProperty("request_memfirst", "false"));
			
			char_encoding = props.getProperty("char_encoding", "UTF-8");
			
			System.out.println("Lecture du fichier options réussie.");
			
		}
		
		
	}

	
	
	
	
	
}
