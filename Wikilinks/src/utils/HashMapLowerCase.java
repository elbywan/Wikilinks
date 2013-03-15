package utils;

import java.util.HashMap;

@SuppressWarnings("serial")
public class HashMapLowerCase extends HashMap<String, Integer> {

	public Integer put(String arg0, Integer arg1){
		return super.put(arg0.toLowerCase().trim(), arg1);
	}
	
	public boolean containsKey(Object s){
		return super.containsKey(((String) s).toLowerCase().trim());
	}
	
	public Integer get(Object key){
		return super.get(((String) key).toLowerCase().trim());
	}
	
	
}
