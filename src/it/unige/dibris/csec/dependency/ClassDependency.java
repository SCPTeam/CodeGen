package it.unige.dibris.csec.dependency;

import it.unige.dibris.csec.codegen.FixedWrappers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class ClassDependency {
	
	private Set<Class> dependency;
	private Class clazz;
	
	public ClassDependency(Class c) {
		dependency = new HashSet<Class>();
		clazz = c;
	}
	
	public Set<Class> getDependencies() {
		
		for(Constructor constructor : clazz.getConstructors()) {
			if(Modifier.isPublic(constructor.getModifiers()) && Modifier.isTransient(constructor.getModifiers())) {
				for(Class c : constructor.getParameterTypes()) {
					if(!toSkip(c)) {
						dependency.add(outbox(c));
					}
				}
			}
		}
		
		for(Method method : clazz.getMethods()) {
			if(Modifier.isPublic(method.getModifiers())) {
				for(Class c : method.getParameterTypes()) {
					if(!toSkip(c)) {
						dependency.add(outbox(c));
					}
				}
			}
		}
		
		// we don't want to wrap low level stuff
		dependency.removeAll(FixedWrappers.map.keySet());
		
		return dependency;		
	}
	
	private boolean toSkip(Class c) {
		if(c.isPrimitive())
			return true;
		else if(c.isArray())
			return toSkip(c.getComponentType());
		else
			return c.isMemberClass() || c.isLocalClass() || c.isSynthetic();
	}
	
	private boolean toSkip(Constructor c) {
		return Modifier.isPublic(c.getModifiers()) && !Modifier.isTransient(c.getModifiers());
	}
	
	private Class outbox(Class c) {
		if(c.isArray())
			return outbox(c.getComponentType());
		else
			return c;
	}
}
