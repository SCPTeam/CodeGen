package it.unige.dibris.csec.dependency;

import java.util.HashSet;
import java.util.Set;

public class DependencyCrawler {
	
	public static Set<Class> crawl(Set<Class> todo) {
		return crawl(todo, new HashSet<Class>());
	}
	
	public static Set<Class> crawl(Set<Class> todo, Set<Class> seen) {
		
		while(!todo.isEmpty()) {
			
			Class clazz = todo.iterator().next();
			
			todo.remove(clazz);
			
			if(!seen.contains(clazz)) {
				seen.add(clazz);
				
				ClassDependency cd = new ClassDependency(clazz);
				
				todo.addAll(cd.getDependencies());
			}
		}
		
		return seen;
		
	}

}
