package requests;

import java.io.IOException;
import java.util.Arrays;

import options.Parameters;
import parsing.Fragment;
import graph.EfficientGraph;
import utils.Toolz;

/**
 * Fournit des méthodes pour analyser les requêtes transmises à Wikilinks et lancer les opérations nécessaires.
 * 
 * @author Elbaz Julien
 */
public class requestParser {
	
	
	public static String[] splitting(String s, char c){
		
		String[] criteres = new String[0];
		boolean is_in_link = false;
		StringBuilder request = new StringBuilder();
		
		for(int k = 0; k < s.length(); k++){
			if(!is_in_link && s.charAt(k) == c){
				criteres = Arrays.copyOf(criteres, criteres.length+1);
				criteres[criteres.length-1] = request.toString();
				request = null;
				request = new StringBuilder();
			} else {
				if(s.charAt(k) == '<' && k < (s.length()-1) && s.charAt(k+1) != '=')
					is_in_link = true;
				if(s.charAt(k) == '>')
					is_in_link = false;
				request.append(s.charAt(k));
			}
		}
		
		criteres = Arrays.copyOf(criteres, criteres.length+1);
		criteres[criteres.length-1] = request.toString();
		request = null;
		
		return criteres;
	}

	/**
	 * Analyse un groupe de requêtes (C.A.D une conjonction de plusieurs requêtes)
	 * 
	 * @param group - Les requêtes à analyser sur une ligne, séparées par des virgules
	 * @param g - L'objet EfficientGraph nécessaire aux opérations.
	 * @return Un tableau d'entiers correspondant au résultat du groupe de requêtes
	 * @throws IOException Si une erreur se produit durant la lecture du fichier index.
	 */
	public static int[] parse_request_group(String group, EfficientGraph g) throws IOException{
		String[] criteres = splitting(group,',');
		int[] result = null;
		for(int i = 0; i < criteres.length; i++){
			int[] recup = parse_request(criteres[i],g);
			if(recup != null)
				Toolz.debug_println("\t> Size : ["+recup.length+"]");
			else {
				Toolz.debug_println("\t> Size : [0]");
				return null;
			}
			if(result == null)
				result = recup;
			else
				result = Toolz.intersectArrays(recup, result);
		}
		return result;
	}
	
	private static int[] parse_request(String req, EfficientGraph g) throws IOException{
		Toolz.getTime(" Sous requête {"+req+"}  \t@ ");
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
		int[] result = null;
		int id = Fragment.getId(word,Parameters.file_Index);
		if(id == -1)
			return result;
		
		if(mode == 'L'){
			if(Parameters.request_memfirst)
				result = g.LDistance(id, distance);
			else 
				result = g.LDistance_fast(id, distance);
		} else if(mode == 'P'){
			result = g.PDistance(id, distance);
		}
		
		return result;
	}
	
	
}
