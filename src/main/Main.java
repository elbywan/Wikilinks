package main;

import graph.EfficientGraph;
import gui.GUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import options.Parameters;

import parsing.FileThreadedWikiParser;
import parsing.Fragment;
import requests.requestParser;
import utils.Toolz;

public class Main {

	public static boolean step_one_flag = false;
	public static boolean step_two_flag = false;
	public static boolean gui_flag      = true;
	
	public static void parse_flags(String[] args){
		for(String s : args){
			if(s.compareTo("--step1") == 0)
				step_one_flag = true;
			else if(s.compareTo("--step2") == 0)
				step_two_flag = true;
			else if(s.compareTo("--nogui") == 0)
				gui_flag = false;
			else if(!s.startsWith("-"))
				Parameters.file_Content = s;
		}
	}
	
	public static void old_run() {
		
		Toolz.getTime("[Step one]   Parsing original ~7GB File --[TO]->   Trimmed down file  \t\t\t@ ");
		
		File f = new File(Parameters.file_Content);
		if(!f.exists()){
			System.out.println("\t> Dump file not found. Please configure Parameters.java or specify as an argument.");
			System.exit(0);
		}
		
		
		File check = new File(Parameters.file_firstStep);
		if(check.exists() && step_one_flag == false){
			System.out.println("\t> Skipping (Use --step1 to build again)");
		} else {
			FileThreadedWikiParser p = new FileThreadedWikiParser(f,Parameters.parse_nbThreads);
			p.setPPT(Parameters.parse_PPT);
			p.setCapacity(Parameters.parse_capacity);
			p.parse();
		}

		Toolz.getTime("[Step two]   Parsing trimmed file       --[TO]->   Index file & Graph file \t\t@ ");
		
		check = new File(Parameters.file_Trimmed);
		File check2 = new File(Parameters.file_Index);
		if(check.exists() && check2.exists() && step_two_flag == false){
			System.out.println("\t> Skipping (Use --step2 to build again)");
		} else {
			try {
				Fragment.fragmentFile(Parameters.file_firstStep, Parameters.file_Index, Parameters.file_Trimmed);
			} catch (FileNotFoundException e) { e.printStackTrace();
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		Toolz.getTime("[Step three] Parsing graph file         --[TO]->   Making a Graph     \t\t\t@ ");
		
		EfficientGraph g = new EfficientGraph(Parameters.file_Index,Parameters.file_Trimmed);
		g.buildGraph();
		
		Toolz.getTime("[Step four] REQUESTS                    --[TO]->   Results	  \t@ ");
		
		BufferedReader r_req = null;
		BufferedWriter w_res = null; 
		
		try{
			Toolz.getTime("Opening request file ["+Parameters.input_Requests+"]  \t@ ");
			File requests = new File(Parameters.input_Requests);
			r_req = new BufferedReader(new InputStreamReader(new FileInputStream(requests), "UTF-8"));
			Toolz.getTime("Opening responses file ["+Parameters.output_Responses+"]  \t@ ");
			File responses = new File(Parameters.output_Responses);
			w_res = new BufferedWriter(new FileWriter(responses));
			
			String line; int count = 0;
			
			while((line = r_req.readLine()) != null){
				System.gc();
				Toolz.getTime("[Beginning request n°"+count+++"]   \t@ ");
				int[] to_write = requestParser.parse_request_group(line, g);
				if(to_write == null){
					w_res.newLine();
					continue;
				}
				Toolz.getTime("[Writing request]   \t@ ");
				String[] retrieved = Fragment.getTitle(to_write, Parameters.file_Index);
				for(int i = 0; i < retrieved.length; i++){
					if(i > 0)
						w_res.write(',');
					Toolz.debug_print("<"+retrieved[i]+">");
					w_res.write('<');
					w_res.write(retrieved[i]);
					w_res.write('>');
				}
				Toolz.debug_println("");
				w_res.newLine();
				w_res.flush();
			}
		} catch(IOException i) {
			i.printStackTrace();
		} finally {
			try {
				r_req.close();
				w_res.close();
			} catch (IOException e) {}
		}
		
		Toolz.getTime("[End]   \t@ ");
		
	}
	
	public static void main(String[] args){
		parse_flags(args);
		if(!gui_flag)
			old_run();
		else
			new GUI();
		
	}
	
}
