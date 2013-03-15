package graph;

import java.util.ArrayList;

public class Node {
	
	private String content;
	private ArrayList<Integer> neighbours;
	
	public Node(String c){
		content = c;
	}
	
	public void set_neighbours(ArrayList<Integer> n){
		this.neighbours = n;
	}
	
	
}
