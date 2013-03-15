package graph;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Graph {

	private SimpleGraph<Integer, DefaultEdge> g;
	private File index;
	private DataOutputStream index_writer;
	private DataInputStream index_reader;
	private HashMap<String,Integer> reference;

	public Graph(){
		g  = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		try { index = File.createTempFile("WikiLinks-index", ".wkl"); } 
		catch (IOException e) { System.out.println("Error while creating a temporary file."); e.printStackTrace(); }
	}

	private int populate_index(String s) throws IOException{
		if(reference.containsKey(s))
			return -1;
		int id = reference.size();
		int length = s.length();
		index_writer.writeInt(length);
		index_writer.writeBytes(s);
		index_writer.writeInt(id);
		reference.put(s,id);
		return id;
	}

	public int get_id(String s) throws IOException{

		index_reader = new DataInputStream(new FileInputStream(index));

		boolean reading = true;
		int id = -1;
		int length;
		byte[] name_bytes;

		while(reading){
			try{
				length = index_reader.readInt();
				name_bytes = new byte[length];
				index_reader.read(name_bytes);
				id = index_reader.readInt();
				if(s.compareTo(new String(name_bytes)) == 0)
					reading = false;
			} catch(EOFException e){
				index_reader.close();
				return -1;
			}
		}

		index_reader.close();
		return id;
	}

	public String get_name(int ident) throws IOException{

		index_reader = new DataInputStream(new FileInputStream(index));

		boolean reading = true;
		int id = -1;
		int length;
		byte[] name_bytes = null;

		while(reading){
			try{
				length = index_reader.readInt();
				name_bytes = new byte[length];
				index_reader.read(name_bytes);
				id = index_reader.readInt();
				if(id == ident)
					reading = false;
			} catch(EOFException e){
				index_reader.close();
				return null;
			}
		}

		index_reader.close();
		return new String(name_bytes);
	}
	
	public void begin_indexing() throws FileNotFoundException{
		index_writer = new DataOutputStream(new FileOutputStream(index));
		reference = new HashMap<String,Integer>();
	}
	
	public void stop_indexing() throws IOException{
		reference.clear();
		index_writer.close();
	}
	
	public void add_node(String s, LinkedList<String> n) throws IOException{
		
		int id = populate_index(s);
		if(id == -1)
			id = reference.get(s);
		
		int[] neighbours = new int[n.size()];
		int i = 0; 
		
		for(String str : n){
			if((neighbours[i] = populate_index(str)) == -1)
				neighbours[i] = reference.get(str);
			i++;
		}
		
		g.addVertex(id);
		for(i = 0; i < neighbours.length; i++){
			if(id == neighbours[i])
				continue;
			g.addVertex(neighbours[i]);
			g.addEdge(id, neighbours[i]);
		}
		
	}
	
	public SimpleGraph<Integer,DefaultEdge> getGraphObject(){
		return g;
	}





}
