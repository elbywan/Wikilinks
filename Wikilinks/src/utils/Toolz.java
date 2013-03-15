package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;


public class Toolz {
	
	/* Conversion unsigned */
	
	public static int unsignByte(byte sigb){
		if(sigb < 0)
			return ((int) sigb)+256;
		return (int) sigb;
	}
	
	public static int unsignShort(short sigs){
		if(sigs < 0)
			return ((int) sigs)+65536;
		return (int) sigs;
	}
	
	public static long unsignInt(int sigi){
		if(sigi < 0)
			return ((long) sigi)+ 4294967296l;
		return (long) sigi;
	}
	
	/* *********************/
	
	/* Conversion en tableau de byte */
	
	public static byte[] getBytes(short s){
		byte[] b_tab = new byte[2];
		b_tab[0] = (byte) ((s & 0xFF00) >> 8);
		b_tab[1] = (byte)  (s & 0x00FF);
		return b_tab;
	}
	
	public static byte[] getBytes(int i){
		byte[] b_tab = new byte[4];
		b_tab[0] = (byte) (i >> 24);
		b_tab[1] = (byte) (i >> 16);
		b_tab[2] = (byte) (i >> 8 );
		b_tab[3] = (byte) (i & 0x000000FF);
		return b_tab;
	}
	
	public static byte[] getBytes(long l){
		byte[] b_tab = new byte[8];
		b_tab[0] = (byte) (l >> 56);
		b_tab[1] = (byte) (l >> 48);
		b_tab[2] = (byte) (l >> 40);
		b_tab[3] = (byte) (l >> 32);
		b_tab[4] = (byte) (l >> 24);
		b_tab[5] = (byte) (l >> 16);
		b_tab[6] = (byte) (l >> 8);
		b_tab[7] = (byte)  l;
		return b_tab;
	}
	
	public static byte[] getBytes(float f){
		return getBytes(Float.floatToIntBits(f));
	}
	
	public static byte[] getBytes(double d){
		return getBytes(Double.doubleToLongBits(d));
	}
	
	/* Inverse (Byte[] -> short/int/long ... */
	
	public static short BytesToShort(byte[] b){
		return (short) ((unsignByte(b[0]) << 8) + unsignByte(b[1]));
	}
	
	public static int BytesToInt(byte[] b){
		return ((unsignByte(b[0]) << 24) + (unsignByte(b[1]) << 16) + (unsignByte(b[2]) << 8) + unsignByte(b[3]));
	}
	
	public static long BytesToLong(byte[] b){
		return (((long)unsignByte(b[0]) << 56) + ((long)unsignByte(b[1]) << 48) + ((long)unsignByte(b[2]) << 40) + ((long)unsignByte(b[3]) << 32) + ((long)unsignByte(b[4]) << 24) + ((long)unsignByte(b[5]) << 16) + ((long)unsignByte(b[6]) << 8) + unsignByte(b[7]));
	}
	
	/* *********************/
	/* Prints de debuggage */
	
	public static void debug_println(Object s){
		if(options.Parameters.debug)
			System.out.println(s);
	}
	
	public static void debug_print(Object s){
		if(options.Parameters.debug)
			System.out.print(s);
	}
	
	/* Utile pour benchmarker */
	public static void getTime(String s){
		Calendar c = Calendar.getInstance();
		System.out.println(s+"["+c.get(Calendar.HOUR_OF_DAY)+"H:"+c.get(Calendar.MINUTE)+"M:"+c.get(Calendar.SECOND)+"S]");
	}
	
	public static String strTime(String s){
		Calendar c = Calendar.getInstance();
		return (s+"["+c.get(Calendar.HOUR_OF_DAY)+"H:"+c.get(Calendar.MINUTE)+"M:"+c.get(Calendar.SECOND)+"S]");
	}
	
	private static long last_time = -1;
	
	public static long bench(){
		if(last_time < 0){
			last_time = System.currentTimeMillis();
			return 0;
		}
		long now = System.currentTimeMillis();
		long result = now - last_time;
		last_time = now;
		return result;
	}
	
	public static String format_time(long l){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		c.setTime(new Date(l));
		return (c.get(Calendar.HOUR_OF_DAY)+"H:"+c.get(Calendar.MINUTE)+"M:"+c.get(Calendar.SECOND)+"S");
	}
	
	/* Fusion de tableaux */
	public static int[] merge(int[] arg1, int[] arg2){
		
		if(arg1 == null)
			return arg2;
		if(arg2 == null)
			return arg1;
		
		int[] merged = Arrays.copyOf(arg1, arg1.length+arg2.length);
	    for(int i = arg1.length; i < merged.length; i++)
	    	merged[i] = arg2[i-arg1.length];
		    
	    return(merged);

	 }
	
	public static int[] merge_unique(int[] arg1, int[] arg2){
		
		if(arg1 == null)
			return arg2;
		if(arg2 == null)
			return arg1;
		
		int[] merged = Arrays.copyOf(arg1, arg1.length+arg2.length);
	    for(int i = arg1.length; i < merged.length; i++)
	    	merged[i] = arg2[i-arg1.length];

	    removeDoublons(merged);
		    
	    return(merged);

	 }
	
	/* Jointure */
	
	public static int[] intersectArrays(int[] first, int[] second) {  
          
		if(first == null)
			return null;
		if(second == null)
			return null;
		
        Set<Integer> intsIntersect = new HashSet<Integer>();  
  
        HashSet<Integer> array1ToHash = new HashSet<Integer>();  
        for (int i = 0; i < first.length; i++) {  
            array1ToHash.add(first[i]);  
        }  
  
        for (int i = 0; i < second.length; i++) {  
            if (array1ToHash.contains(second[i])) {  
                intsIntersect.add(second[i]);  
            }  
        }  
  
        Integer[] res = intsIntersect.toArray(new Integer[0]);
        intsIntersect.clear();
        intsIntersect = null;
        int[] result = new int[res.length];
        for(int i = 0; i < res.length; i++){
        	result[i] = res[i];
        }
        return result;  
          
    }  
	
	public static int[] removeDoublons(int[] arr){
		if(arr == null)
			return null;
		if(arr.length < 2)
			return arr;
		
		Set<Integer> set = new TreeSet<Integer>();
		
		for(int i : arr)
			set.add(new Integer(i));
		
		Object[] o_res = set.toArray();
		Integer[] res = new Integer[o_res.length];
		for(int i = 0; i < res.length; i++)
			res[i] = (Integer) o_res[i];
		set.clear();
		set = null;
		
		int[] result = new int[res.length];
        for(int i = 0; i < res.length; i++){
        	result[i] = res[i];
        }
        
		return result;
	}
	
	public static void printFile(File f) throws IOException{
		BufferedReader r = new BufferedReader(new FileReader(f));
		String s;
		while((s = r.readLine()) != null)
			System.out.println(s);
		r.close();
	}
	
	
}
