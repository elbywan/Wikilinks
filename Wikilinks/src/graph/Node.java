package graph;

import java.util.ArrayList;

public class Node {
	
	//Useless : private int nodeid;
	private ArrayList<Integer> outgoing = new ArrayList<Integer>();
	private ArrayList<Integer> incoming = new ArrayList<Integer>();
	
	/* Useless :
	public Node(int id){
		nodeid = id;
	} */
	
	public void add_link_to(int n){
		this.outgoing.add(n);
	}
	
	public void add_link_from(int n){
		this.incoming.add(n);
	}
	
	/* Useless :
	public int getid(){
		return nodeid;
	} */
	
	public ArrayList<Integer> get_incoming(){
		return incoming;
	}
	
	public ArrayList<Integer> get_outgoing(){
		return outgoing;
	}
	
	public ArrayList<Integer> get_all_links(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i : incoming){
			result.add(i);
		}
		for(int i : outgoing){
			result.add(i);
		}
		return result;
	}
	
	public void delete(){
		incoming.clear();
		outgoing.clear();
	}
	
	
}
