package graph;

public class Couple<T,T2> {
	
	private T key;
	private T2 value;

	public Couple(T k, T2 v){
		key = k;
		value = v;
	}
	
	public T get_key(){
		return key;
	}
	
	public T2 get_value(){
		return value;
	}
	
	public void dispatch(){
		key = null;
		value = null;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public boolean equals(Object obj){
		if(obj instanceof Couple<T,T2>){
			if(((Couple<T,T2>) obj).get_value().equals(value) && ((Couple<T,T2>) obj).get_key().equals(key))
				return true;
		}
		return false;
	}
	
}
