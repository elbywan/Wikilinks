package parsing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Implémente un parseur avec quelques opérations utiles sur les tags xml.
 * Doit être sousclassé pour définir une méthode de parsing.
 * 
 * @author Elbaz Julien
 */
public abstract class XMLParser {

	protected BufferedReader XMLReader;
	private String discarded_buffer = "";
	
	protected XMLParser(File input){
		try { XMLReader = getDescriptor(input); } 
			catch (IOException e) { e.printStackTrace(); }
	}
	
	private BufferedReader getDescriptor(File f) throws IOException{
		if(!f.exists())
			f.createNewFile();
		BufferedReader r = new BufferedReader(new FileReader(f));
		return r;
	}
	
	protected void goToTag(String tag) throws IOException{
		
		int i = -1;
		String op_tag = "<"+tag;
		String s;
		
		if((i = discarded_buffer.indexOf(op_tag)) != -1 && i > 0){
			if((i = discarded_buffer.substring(i+op_tag.length()).indexOf(op_tag)) != -1){
				discarded_buffer += discarded_buffer.substring(i);
				return;
			}
		}
		
		discarded_buffer = "";
		
		while((s = XMLReader.readLine()) != null && (i = s.indexOf(op_tag)) == -1);
		
		if(s != null)
			discarded_buffer += s.substring(i);
			
	}
	
	protected String getTagContent(String tag) throws IOException{
		
		String op_tag = "<"+tag;
		String close_tag = "</"+tag+">";
		String s = "";
		StringBuilder result = new StringBuilder("");
		int i  = -1;
		int i2 = -1;
		boolean skip = false;
		
		//System.out.println(discarded_buffer);
		
		if((i = discarded_buffer.indexOf(op_tag)) == -1)
			while((s = XMLReader.readLine()) != null && (i = s.indexOf(op_tag)) == -1);
		else{
			if((i2 = discarded_buffer.indexOf(close_tag)) == -1){
				result.append(discarded_buffer.substring(i + op_tag.length())); 
				skip = true;
			}
			else {
				result.append(discarded_buffer.substring(i + op_tag.length(), i2));
				discarded_buffer = discarded_buffer.substring(i2 + close_tag.length());;
				return result.toString();
			}
		}
		
		discarded_buffer = "";
			
		
		if(s == null)
			return "";
		
		if((i2 = s.indexOf(close_tag)) == -1){
			if(!skip)
				result.append(s.substring(i + op_tag.length()));
			i = -1;
			while((s = XMLReader.readLine()) != null && (i = s.indexOf(close_tag)) == -1){
				result.append(s);
			}
			if(i != -1){
				result.append(s.substring(0, i));
				discarded_buffer += s.substring(i+close_tag.length());
			}
		} else {
			result.append(s.substring(i + op_tag.length(), i2));
			discarded_buffer += s.substring(i2+close_tag.length());
		}
		
		return result.toString();
		
	}
	
	protected LinkedList<String> getContents(String left_border, String right_border) throws IOException{
		
		String s = "";  
		LinkedList<String> result = new LinkedList<String>();
		
		byte[] left  = left_border.getBytes();
		byte[] right = right_border.getBytes();
		
		boolean is_in = false;
		
		while((s = XMLReader.readLine()) != null){
			byte[] b = s.getBytes();
			
			int helper = 0;
			int pos = 0;
			
			for(int i = 0; i < b.length; i++){
				
				if(!is_in && b[i] == left[helper]){
					if(helper == left.length-1){
						helper = 0;
						is_in = true; 
						pos = i+1; 
					}
					else
						helper++;
				} 
				
				else if(!is_in && b[i] != left[helper])
					helper = 0;
				
				else if(is_in && b[i] == right[helper]){
					if(helper == right.length-1){
						is_in = false;
						int max = i - pos - helper;
						byte[] copy = new byte[max];
						for(int j = 0; j < max; j++, pos++){
							copy[j] = b[pos];
						}
						result.add(new String(copy));
						helper = 0;
					}
					else
						helper++;
				}
				
				else if (is_in && b[i] != right[helper])
					helper = 0;
				
			}
		}
		
		return result;
		
	}

	protected LinkedList<String> getContents(String left_border, String right_border, String target) throws IOException{

		String s = "";  
		BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(target.getBytes())));
		LinkedList<String> result = new LinkedList<String>();
		
		byte[] left  = left_border.getBytes();
		byte[] right = right_border.getBytes();
		
		boolean is_in = false;
		
		while((s = r.readLine()) != null){
			byte[] b = s.getBytes();
			
			int helper = 0;
			int pos = 0;
			
			for(int i = 0; i < b.length; i++){
				
				if(!is_in && b[i] == left[helper]){
					if(helper == left.length-1){
						helper = 0;
						is_in = true; 
						pos = i+1; 
					}
					else
						helper++;
				} 
				
				else if(!is_in && b[i] != left[helper])
					helper = 0;
				
				else if(is_in && b[i] == right[helper]){
					if(helper == right.length-1){
						is_in = false;
						int max = i - pos - helper;
						byte[] copy = new byte[max];
						for(int j = 0; j < max; j++, pos++){
							copy[j] = b[pos];
						}
						result.add(new String(copy));
						helper = 0;
					}
					else
						helper++;
				}
				
				else if (is_in && b[i] != right[helper])
					helper = 0;
				
			}
		}
		
		r.close();
		
		return result;

	}
	
	public String[] getTagContents(String tag, String target){
		
		String open_tag = "<"+tag;
		String close_tag = "</"+tag+">";
		
		StringBuilder result = new StringBuilder("");
		StringBuilder rest = new StringBuilder("");
		
		int i  = -1;
		int i2 = -1;
		
		if((i = target.indexOf(open_tag)) == -1)
			return (new String[] {result.toString(),target});
		else if((i2 = target.indexOf(close_tag)) != -1){
			result.append(target.substring(i+open_tag.length(), i2));
			rest.append(target.substring(i2+close_tag.length())); 
			return (new String[] {result.toString(),rest.toString()});
		} else {
			return (new String[] {result.toString(),target});
		}
			
	}
	
	protected String getStartingTags(String tag) throws IOException{
		
		String open_tag = "<"+tag;
		String close_tag = "</"+tag+">";
		
		String s = "";
		StringBuilder result = new StringBuilder();
		
		while((s = XMLReader.readLine()) != null && !s.trim().startsWith(open_tag));
		while((s = XMLReader.readLine()) != null && !s.trim().startsWith(close_tag)){
			result.append(s);
		}
		
		if(s == null)
			return "";
		
		return result.toString();
		
	}
	
	protected void close_fd() throws IOException{
		XMLReader.close();
	}
	
	public abstract void parse();
	
	
}
