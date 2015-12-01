package it.unige.dibris.csec.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class WrapperBuilder {
	
	private Class clazz;
	
	public WrapperBuilder(Class c) {
		clazz = c;
	}
	
	public void save(String dir, String pack) throws IOException {
		File src = new File(dir + clazz.getSimpleName() + ".java");
		
		if(!src.exists()) {
			src.createNewFile();
		}
		
		FileWriter writer = new FileWriter(src);
		
		if(pack != null)
			writer.write("package " + pack + ";\n\n");
		
		writer.write("import it.saonzo.annotations.ClassWithInstrMethods;\n");
		writer.write("import it.saonzo.annotations.InstrumentedMethod;\n");
		writer.write("import it.saonzo.instrapp.HelperClass;\n\n");
		
		writer.write("@ClassWithInstrMethods\n");
		writer.write("public class " + clazz.getSimpleName() + " {\n");
		
		for(Constructor c : clazz.getConstructors()) {
			if(Modifier.isPublic(c.getModifiers())) {
				writeConstructor(c, writer);
			}
		}
		
		for(Method m : clazz.getMethods()) {
			if(Modifier.isPublic(m.getModifiers())) {
				writeMethod(m, writer);
			}
		}
		
		writer.write("}\n");
		writer.flush();
		writer.close();
		
	}

	private void writeMethod(Method m, FileWriter writer) throws IOException {
		writer.write("\n");
		writer.write("\t@InstrumentedMethod(defClass=\"" + clazz.getCanonicalName() + "\", isInit=0, isStatic=" + ((Modifier.isStatic(m.getModifiers()))? "1" : "0") + ")\n");
		writer.write("\tpublic static " + m.getReturnType().getCanonicalName() + " " + m.getName() + "(");
		if(!Modifier.isStatic(m.getModifiers()))
			writer.write(clazz.getCanonicalName() + " self");
		Class[] T = m.getParameterTypes();
		for(int i = 0; i < T.length; i++) {
			if(i > 0 || !Modifier.isStatic(m.getModifiers()))
				writer.write(", ");
			if(T[i].isPrimitive()) {
				writer.write(T[i].getSimpleName() + " x" + i);
			}
			else {
				writer.write(T[i].getCanonicalName() + " x" + i);
			}
		}
		writer.write(")");
		
		if(m.getExceptionTypes().length > 0) {
			writer.write(" throws");
			for(Class e : m.getExceptionTypes()) {
				writer.write(" " + e.getCanonicalName());
			}
		}
		
		writer.write(" {\n");
		
		
		for(int i = 0; i < T.length; i++) {
			if(T[i].isArray()) {
				writer.write("\t\tHelperClass.Log(\""+ T[i].getComponentType().getCanonicalName() +"[] "
						+ "x" + i + "copy = new " + T[i].getComponentType().getCanonicalName() + "[\" + x"+i+".length + \"]\");\n");
				writer.write("\t\tfor(int j = 0; i < x"+i+".length; j++)\n");
				writer.write("\t\t\tHelperClass.Log(\"\\tx"+i+"copy[\"+j+\"] = \" + x"+i+"[j]);\n");
			}
		}
		
		if(!Modifier.isStatic(m.getModifiers()))
			writer.write("\t\tHelperClass.Log(HelperClass.get(self.hashCode()) + \"");
		else
			writer.write("\t\tHelperClass.Log(\"" + clazz.getCanonicalName());
			
		writer.write("." + m.getName() + "(");
		
		for(int i = 0; i < T.length; i++) {
			if(i > 0)
				writer.write(", ");
			writeReference(T[i], "x"+i, writer);
		}
		
		writer.write(")\");\n");
		
		writer.write("\t\t");
		if(!m.getReturnType().equals(Void.TYPE))
			writer.write(m.getReturnType().getCanonicalName() + " result = ");
		
		String invoke = "";
		if(Modifier.isStatic(m.getModifiers()))
			invoke += clazz.getCanonicalName() + "." + m.getName() + "(";
		else
			invoke += "self." + m.getName() + "(";
		
		for(int i = 0; i < T.length; i++) {
			if(i > 0)
				invoke += ", ";
			invoke += "x" + i;
		}
		
		invoke += ")";
		writer.write(invoke + ";\n");
		
		if(!m.getReturnType().equals(Void.TYPE) && !m.getReturnType().isPrimitive()) {
			
			String metaivk = new String(invoke.toCharArray());
			
			for(int i = 0; i < T.length; i++) {
				if(T[i].isPrimitive())
					metaivk.replaceAll("x"+i, "\" + x" +i+ " + \"");
				else
					metaivk.replaceAll("x"+i, "\" + HelperClass.get(x"+i+".hashCode()) + \"");
			}
			
			writer.write("\t\tHelperClass.CreateVariable("
					+ "result.hashCode(), "
					+ "\"" + m.getReturnType().getCanonicalName() +"\", "
					+ "\"" + metaivk + "\");\n");
		}
		
		if(!m.getReturnType().equals(Void.TYPE))
			writer.write("\t\treturn result;\n");
		
		writer.write("\t}\n");
	}

	private void writeConstructor(Constructor c, FileWriter writer) throws IOException {
		writer.write("\n");
		writer.write("\t@InstrumentedMethod(defClass=\"" + clazz.getCanonicalName() + "\", isInit=1, isStatic=0)\n");
		writer.write("\tpublic static " + clazz.getCanonicalName() + " constructor(" + clazz.getCanonicalName() + " self");
		Class[] T = c.getParameterTypes();
		for(int i = 0; i < T.length; i++) {
			if(T[i].isPrimitive()) {
				writer.write(", " + T[i].getSimpleName() + " x" + i);
			}
			else {
				writer.write(", " + T[i].getCanonicalName() + " x" + i);
			}
		}
		writer.write(")");
		
		if(c.getExceptionTypes().length > 0) {
			writer.write(" throws");
			for(Class e : c.getExceptionTypes()) {
				writer.write(" " + e.getCanonicalName());
			}
		}
		
		writer.write(" {\n");
		
		for(int i = 0; i < T.length; i++) {
			if(T[i].isArray()) {
				writer.write("\t\tHelperClass.Log(\""+ T[i].getComponentType().getCanonicalName() +"[] "
						+ "x" + i + "copy = new " + T[i].getComponentType().getCanonicalName() + "[\" + x"+i+".length + \"]\");\n");
				writer.write("\t\tfor(int j = 0; i < x"+i+".length; j++)\n");
				writer.write("\t\t\tHelperClass.Log(\"\\tx"+i+"copy[\"+j+\"] = \" + x"+i+"[j]);\n");
			}
		}
		
		writer.write("\t\tHelperClass.CreateVariable("
				+ "self.hashCode(), "
				+ "\"" + clazz.getCanonicalName() +"\", "
				+ "\"new " + clazz.getCanonicalName() + "(");
		
		for(int i = 0; i < T.length; i++) {
			if(i > 0)
				writer.write(", ");
			writeReference(T[i], "x"+i, writer);
		}
		
		writer.write(")\");\n");
		
		writer.write("\t\treturn self;\n");
		writer.write("\t}\n");
	}

	private void writeReference(Class type, String var, FileWriter writer) throws IOException {
		if(type.isArray()) {
			writer.write(var+"copy");
		}
		else if(type.isPrimitive()) {
			writer.write("\" + " + var + " + \"");
		}
		else if(FixedWrappers.known(type)) {
			writer.write(FixedWrappers.getVal(type, var));
		}
		else {
			writer.write("\" + HelperClass.get(" + var + ".hashCode()) + \"");
		}
	}

}
