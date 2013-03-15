package graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import utils.Toolz;

/**
 * Modélisation d'un graphe orienté avec arêtes de poids 1.<br/>
 * L'objet EfficientGraph a été conçu de manière à minimiser au 
 * maximum la consommation mémoire.
 * 
 * @author Elbaz Julien
 */
public class EfficientGraph {

	private int[][][] graph;
	File f;
	File f2;
	
	/**
	 * Instancie un graphe à partir d'un fichier de graphe et d'un fichier index.<br/>
	 * Le graphe n'est pas crée par le constructeur ! Il faut appeler buildGraph().
	 * 
	 * @param index_path - Chemin vers le fichier index
	 * @param dump_path  - Chemin vers le fichier graphe
	 */
	public EfficientGraph(String index_path, String dump_path){
		f = new File(index_path);
		f2 = new File(dump_path);
	}
	
	/**
	 * Construit le graphe.
	 */
	public void buildGraph(){
		
		int length = 0;

		try {
			BufferedReader r = new BufferedReader(new FileReader(f),4096);
			while(r.readLine() != null)
				length++;
			r.close();


			graph = new int[length][][];
			for(int z = 0; z < graph.length; z++){
				graph[z] = new int[2][];
				graph[z][1] = new int[0];
			}
			
			int number;
			String s;

			Toolz.debug_println("======================");

			r = new BufferedReader(new FileReader(f2),4096);
			number = 0;

			while((s = r.readLine()) != null){
				number++;
				if(number%50000 == 0)
					Toolz.debug_println(number);
				//System.out.println(s);

				String[] splitted = s.split("[|]");
				int src = Integer.parseInt(splitted[0]);
				graph[src][0] = new int[splitted.length-1];

				for(int i = 1; i < splitted.length; i++){
					int dest = Integer.parseInt(splitted[i]);
					if(src != dest){
						graph[src][0][i-1] = dest;
						graph[dest][1] = Arrays.copyOf(graph[dest][1], graph[dest][1].length+1);
						graph[dest][1][graph[dest][1].length-1] = src;
					}
				}

			}
			
			for(int i = 0; i < graph.length; i++){
				graph[i][1] = Toolz.removeDoublons(graph[i][1]);
			}
			
			r.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Renvoie les arêtes sortantes pour le noeud choisi.
	 * 
	 * @param node - Noeud considéré
	 * @return Un tableau d'entiers contenant les arêtes sortantes.
	 */
	public int[] get_outgoing(int node){
		return graph[node][0];
	}
	
	/**
	 * Renvoie les arêtes entrantes pour le noeud choisi.
	 * 
	 * @param node - Noeud considéré
	 * @return Un tableau d'entiers contenant les arêtes entrantes.
	 */
	public int[] get_incoming(int node){
		return graph[node][1];
	}
	
	/**
	 * Renvoie toutes les arêtes pour le noeud choisi.</br>
	 * Utile dans le cas de graphes non orientés.
	 * 
	 * @param node - Noeud considéré
	 * @return Un tableau d'entiers contenant les arêtes.
	 */
	public int[] get_allvertice(int node){
		int[] result = Toolz.merge_unique(get_incoming(node), get_outgoing(node));
		return result;
	}
	
	/**
	 * Calcule les noeuds ayant une distance en largeur inférieure à la profondeur fixée.</br>
	 * 
	 * @param starting_node - noeud de départ
	 * @param distance - Profondeur
	 * @return Les noeuds à cette distance.
	 */
	public int[] LDistance(int starting_node, int distance){
		int[] result = new int[1];
		result[0] = starting_node;
		
		for(int i : get_incoming(starting_node)){
			int pos = -1;
			int[] out = get_outgoing(i);
			for(int j = 0; j < out.length; j++){
				if(out[j] == starting_node){
					if(pos == -1){
						for(pos = Math.max(0, j-distance); pos < j; pos++){
							result = Arrays.copyOf(result, result.length+1);
							result[result.length-1] = out[pos];
						}
						pos = j+1;
					} else {
						for(int old_pos = pos; pos < j && pos < old_pos + distance; pos++){
							result = Arrays.copyOf(result, result.length+1);
							result[result.length-1] = out[pos];
						}
						pos = Math.max(pos,j-distance);
						for(; pos < j; pos++){
							result = Arrays.copyOf(result, result.length+1);
							result[result.length-1] = out[pos];
						}
						pos++;
					}
				}
			}
			if(pos != -1){
				for(int old_pos = pos; pos < out.length && pos < old_pos + distance; pos++){
					result = Arrays.copyOf(result, result.length+1);
					result[result.length-1] = out[pos];
				}
			}
		}
		
		return Toolz.removeDoublons(result);
	}
	
	/**
	 * Calcule les noeuds ayant une distance en largeur inférieure à la profondeur fixée.</br>
	 * Plus rapide en calcul que LDistance() mais consomme plus de mémoire.
	 * 
	 * @param starting_node - noeud de départ
	 * @param distance - Profondeur
	 * @return Les noeuds à cette distance.
	 */
	public int[] LDistance_fast(int starting_node, int distance){
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.add(starting_node);
		
		for(int i : get_incoming(starting_node)){
			int pos = -1;
			int[] out = get_outgoing(i);
			for(int j = 0; j < out.length; j++){
				if(out[j] == starting_node){
					if(pos == -1){
						for(pos = Math.max(0, j-distance); pos < j; pos++){
							result.add(out[pos]);
						}
						pos = j+1;
					} else {
						for(int old_pos = pos; pos < j && pos < old_pos + distance; pos++){
							result.add(out[pos]);
						}
						pos = Math.max(pos,j-distance);
						for(; pos < j; pos++){
							result.add(out[pos]);
						}
						pos++;
					}
				}
			}
			if(pos != -1){
				for(int old_pos = pos; pos < out.length && pos < old_pos + distance; pos++){
					result.add(out[pos]);
				}
			}
		}
		
		Object[] o = result.toArray();
		result.clear(); result = null;
		
		int[] int_result = new int[o.length];
		for(int i = 0; i < int_result.length; i++){
			int_result[i] = (Integer) o[i];
			o[i] = null;
		}
		o = null;
		
		return Toolz.removeDoublons(int_result);
	}
	
	private HashSet<Integer> memory_edges = new HashSet<Integer>();
	
	private int[] sumOfEdges(int node, int distance){

		if(memory_edges.contains(node))
			return null;
		
		memory_edges.add(node);
		
		int[] edges = get_allvertice(node);
		if(distance == 1)
			return edges;
		
		for(int e : edges){
				edges = Toolz.merge(edges, sumOfEdges(e,distance-1));
		}
		return Toolz.removeDoublons(edges);
	}
	
	/**
	 * Calcule les noeuds ayant une distance en profondeur inférieure à la profondeur fixée.</br>
	 * Algorithme utilisé : BFS (parcours en largeur). <br/>
	 * 
	 * @param starting_node - noeud de départ
	 * @param distance - Profondeur
	 * @return Les noeuds à cette distance.
	 */
	public int[] PDistance(int starting_node, int distance){
		int[] result =  sumOfEdges(starting_node,distance);
		memory_edges.clear();
		memory_edges = new HashSet<Integer>();
		return result;
	}
	
	
}
