package it.unige.dibris.csec.codegen;

import java.util.HashMap;

public class FixedWrappers {
	
	public static HashMap<Class,String> map;
	
	static {
		map = new HashMap<Class,String>();
		
		//map.put(String.class, "%x.toString()");
		//map.put(CharSequence.class, "%x.toString()");
		//map.put(StringBuilder.class, "%x.toString()");
		//map.put(StringBuffer.class, "%x.toString()");
		//map.put(Class.class, "Class.forName(%x.getCanonicalName())");
		//map.put(ClassLoader.class, "ClassLoader.getSystemClassLoader()");
		//map.put(Enum.class, "%x");
		// map.put(Object.class, "new Object(/* unable to build %x */)");
		// map.put(File.class, "new File(%x.getAbsolutePath())");	
	}
	
	public static boolean known(Class T) {
		return map.keySet().contains(T);
	}
	
	public static String getVal(Class T, String var) {
		if(map.containsKey(T))
			return map.get(T).replace("%x", var);
		else
			return "null";
	}

}
