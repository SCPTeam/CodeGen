package it.unige.dibris.csec.codegen;

import java.io.File;
import java.util.HashMap;

public class FixedWrappers {
	
	public static HashMap<Class,String> map;
	
	static {
		map = new HashMap<Class,String>();
		
		map.put(String.class, "new String(\\\"\" + %x.toString() + \"\\\")");
		map.put(CharSequence.class, "new String(\\\"\" + %x.toString() + \"\\\")");
		map.put(StringBuilder.class, "new StringBuilder(\\\"\" + %x.toString() + \"\\\")");
		map.put(StringBuffer.class, "new StringBuffer(\\\"\" + %x.toString() + \"\\\")");
		map.put(Class.class, "Class.forName(%x.getCanonicalName())");
		//map.put(ClassLoader.class, "ClassLoader.getSystemClassLoader()");
		map.put(Enum.class, "\" + %x + \"");
		map.put(Object.class, "new Object(/* unable to build %x */)");
		map.put(File.class, "new File(\\\"\" + %x.getAbsolutePath() + \"\\\")");
		map.put(Thread.class, "Thread.currentThread()");
	}
	
	public static boolean known(Class T) {
		return map.keySet().contains(T);
	}
	
	public static String getVal(Class T, String var) {
		if(map.containsKey(T))
			return map.get(T).replace("%x", var);
		else
			return var + ".toString()";
	}

}
