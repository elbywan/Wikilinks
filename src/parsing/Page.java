package parsing;

import java.util.LinkedList;

public class Page {

	public String raw;
	
	public String title;
	public String text;
	public LinkedList<String> formatted_links = new LinkedList<String>();
	
	public boolean interrupt = false;
	
	public Page(String ttl, String txt){
		title = ttl; text = txt;
	}
	
	public Page(String all){
		raw = all;
	}
	
	public Page(boolean b){
		interrupt = b;
	}
	
}
