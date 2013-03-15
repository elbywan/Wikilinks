package parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import options.Parameters;

import utils.Toolz;

/**
 * Classe utilisée durant les 2,3 et 4èmes étapes du programme Wikilinks.
 * Possède des opérations d'entrée et de sortie sur les fichiers index & graphe.
 * 
 * @author Elbaz Julien
 */
public class Fragment {

	/**
	 * Sépare un gros fichier au format graphe en deux fichiers.<br/>
	 * - Un index contenant des correspondances entre chaînes et entiers.<br/>
	 * - Le fichier graphe d'entrée avec des entiers à la place des chaînes.<br/>
	 * 
	 * @param input_path - Chemin du fichier graphe
	 * @param index_path - Chemin de sortie pour la création de l'index
	 * @param trimmed_path - Chemin de sortie pour la création du nouveau fichier graphe
	 * @throws FileNotFoundException Si le fichier d'entrée est manquant
	 * @throws IOException Si une erreur survient durant l'écriture ou la lecture
	 */
	public static void fragmentFile(String input_path, String index_path, String trimmed_path) throws FileNotFoundException,IOException{
		
		File f = new File(input_path);
		File f2 = new File(trimmed_path);
		File f3 = new File(index_path);

		if(f3.exists())
			f3.delete();
		f3.createNewFile();

		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f),Parameters.char_encoding),4096);
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f3),Parameters.char_encoding),4096);	

		HashMap<String,Integer> map = new HashMap<String,Integer>();
		String s;
		int number = 0;
		int max_index = 0;
		StringBuilder builder;

		while((s = r.readLine()) != null){
			number++;
			if(number%50000 == 0)
				Toolz.debug_println(number);

			builder = new StringBuilder();
			String[] splitted = s.split("[|]");
			if(!map.containsKey(splitted[0])){
				map.put(splitted[0], max_index);
				builder.append(Integer.toString(max_index)); builder.append("|");
				builder.append(splitted[0]);  builder.append("\n");
				w.write(builder.toString());
				max_index++;
			}
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
		r = new BufferedReader(new InputStreamReader(new FileInputStream(f),Parameters.char_encoding),4096);
		w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f2),Parameters.char_encoding),4096);
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

	}
	
	/**
	 * Récupère l'id d'une chaîne de caractère modélisant un noeud.
	 * Pour cela parcourt le fichier index à la recherche du noeud et de sa correspondance.
	 * 
	 * @param title - Titre à rechercher
	 * @param index_path - Chemin du fichier index
	 * @return Un entier correspondant à l'identifiant du titre recherché ou -1 si le titre n'a pas été trouvé
	 * @throws IOException Si une erreur survient durant la lecture de l'index
	 */
	public static int getId(String title, String index_path) throws IOException{
		
		File f = new File(index_path);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f),Parameters.char_encoding),4096);
		String s;
		
		while((s = r.readLine()) != null){
			String[] splitted = s.split("[|]");
			if(splitted[1].compareTo(title.trim()) == 0)
				return Integer.parseInt(splitted[0]);
		}
		
		r = new BufferedReader(new InputStreamReader(new FileInputStream(f),Parameters.char_encoding),4096);
		
		while((s = r.readLine()) != null){
			String[] splitted = s.split("[|]");
			if(splitted[1].compareToIgnoreCase(title.trim()) == 0)
				return Integer.parseInt(splitted[0]);
		}
		
		return -1; 
	}
	
	/**
	 * Donne la chaîne de caractère correspondant à l'id passé en paramètre.
	 * Pour cela parcourt le fichier index à la recherche du noeud et de sa correspondance.
	 * 
	 * @param id - Indentifiant à chercher
	 * @param index_path - Chemin vers le fichier index
	 * @return La chaîne de caractère correspondant à l'identifiant
	 * @throws IOException Si une erreur survient durant la lecture de l'index
	 */
	public static String getTitle(int id, String index_path) throws IOException{
		
		File f = new File(index_path);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f),Parameters.char_encoding),4096);
		String s;
		
		while((s = r.readLine()) != null){
			String[] splitted = s.split("[|]");
			if(Integer.parseInt(splitted[0]) == id)
				return splitted[1];
		}
		
		return ""; 
	}
	
	/**
	 * Donne la chaîne de caractère correspondant aux ids passé en paramètre.
	 * Pour cela parcourt le fichier index à la recherche des noeuds et de leur correspondance.
	 * 
	 * 
	 * @param id - Indentifiants à chercher
	 * @param index_path - Chemin vers le fichier index
	 * @return Les chaînes de caractère correspondant aux identifiants
	 * @throws IOException Si une erreur survient durant la lecture de l'index
	 */
	public static String[] getTitle(int[] list, String index_path) throws IOException{
		
		if(list == null)
			return null;
		
		File f = new File(index_path);
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f),Parameters.char_encoding),4096);
		String s;
		String[] result = new String[list.length];
		HashMap<Integer,Integer> set = new HashMap<Integer,Integer>();
		int pos = 0;
		for(int i : list)
			set.put(new Integer(i),pos++);
		
		while((s = r.readLine()) != null){
			String[] splitted = s.split("[|]");
			if(set.containsKey(Integer.parseInt(splitted[0])))
				result[set.get(new Integer(Integer.parseInt(splitted[0])))] = splitted[1];
		}
		
		set.clear();
		set = null;
		
		return result; 
	}
	
}
