package parsing;

import graph.Graph;

import java.io.File;
import java.io.IOException;

public class RamThreadedWikiParser extends ThreadedWikiParser {

	Graph stock = new Graph();

	public RamThreadedWikiParser(File input) {
		super(input);
	}

	public RamThreadedWikiParser(File input, int thr_num) {
		super(input, thr_num);
	}

	public RamThreadedWikiParser(File input, int thr_num, int capacity) {
		super(input, thr_num, capacity);
	}

	protected void retrieval_method() {

		Page[] pages = null;
		
		try { 
			
			pages = retrieve.take();
			stock.begin_indexing(); 

			while(pages != null && !pages[0].interrupt){

				for(int i = 0; i < pages_by_thread; i++){
					Page p = pages[i];
					if(p == null)
						break;
					if(p.title == null || p.title.length() == 0)
						continue;

					stock.add_node(p.title, p.formatted_links);
					
					p = null;
				}
				pages = retrieve.take();
			}
			stock.stop_indexing(); 
			
		} catch (IOException e) {
			System.out.println("Error while dealing with the indexing file.");
			e.printStackTrace();
		}
		catch (InterruptedException e) { return; }

	}
	
	public Graph getGraph(){
		return stock;
	}

}
