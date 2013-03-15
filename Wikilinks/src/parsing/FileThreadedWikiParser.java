package parsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import options.Parameters;

public class FileThreadedWikiParser extends ThreadedWikiParser {
	
	File output = new File(Parameters.file_firstStep);
	BufferedWriter wr;
	
	private int bufferPPTSIZE = 1;

	public FileThreadedWikiParser(File input) {
		super(input);
	}

	public FileThreadedWikiParser(File input, int thr_num) {
		super(input, thr_num);
	}
	
	public FileThreadedWikiParser(File input, int thr_num, int capacity) {
		super(input, thr_num, capacity);
	}
	
	public void setOutput(String output_path) throws IOException{
		output = new File(output_path);
	}
	
	private void set_fd() throws IOException{
		if(output.exists()){
			output.delete();
			output.createNewFile();
		}
		wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output),Parameters.char_encoding),512000);
	}

	protected void retrieval_method() {
		try { set_fd(); } 
			catch (IOException e) { e.printStackTrace(); }
			
		Page[] pages = null;
		try { pages = retrieve.take(); }
			catch (InterruptedException e1) { return; }
			
		StringBuilder buffer = new StringBuilder("");
		int count_buffer = 1;
		
		while(pages != null && !pages[0].interrupt){
			
			//System.out.println("Writer thread : "+(loop_test++));
			
			for(int i = 0; i < pages_by_thread; i++){
				Page p = pages[i];
				if(p == null)
					break;
				if(p.title == null || p.title.length() == 0 || p.title.compareTo("") == 0)
					continue;
				
				buffer.append(p.title);
				for(String s : p.formatted_links){
					if(s.compareTo("") == 0)
						continue;
					buffer.append("|");
					buffer.append(s);
				}
				p = null;
				buffer.append('\n');
			}
			try { if(count_buffer == bufferPPTSIZE){
					wr.write(buffer.toString()); 
					buffer = new StringBuilder("");
					count_buffer = 1;
				  } else {
					  count_buffer ++;
				  }
			} catch(IOException e){ e.printStackTrace(); }
			
			try { pages = retrieve.take(); }
				catch (InterruptedException e1) { return; }
		}	
		
		if(buffer.toString() != "")
			try { wr.write(buffer.toString()); }
			catch(IOException e){ e.printStackTrace(); }
		
		try { wr.close(); } 
			catch (IOException e) { e.printStackTrace(); }
	}


}
