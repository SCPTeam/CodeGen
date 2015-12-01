package it.unige.dibris.csec.test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import it.unige.dibris.csec.codegen.WrapperBuilder;
import it.unige.dibris.csec.dependency.DependencyCrawler;

public class Test {
	
	static String outdir = "/home/gabriele/Scrivania/instr/";
	static String pack = "it.unige.dibris.csec.instr";

	public static void main(String[] args) {
		
		long begin = System.currentTimeMillis();
		
		Set<Class> todo = new HashSet<Class>();
		
		todo.add(org.apache.http.client.HttpClient.class);
		todo.add(org.apache.http.client.methods.HttpPost.class);
		todo.add(org.apache.http.client.methods.HttpGet.class);
		todo.add(org.apache.http.impl.client.DefaultHttpClient.class);
		todo.add(org.apache.http.client.methods.HttpUriRequest.class);
		todo.add(java.util.Properties.class);
		
		Set<Class> S = DependencyCrawler.crawl(todo);
		
		for(Class c : S) {
			WrapperBuilder wb = new WrapperBuilder(c);
			try {
				wb.save(outdir, pack);
			} catch(IOException e) {
				System.out.println("Could not write " + c.getCanonicalName() + "'s wrapper due to: " + e);
			}
		}
		
		System.out.println("Done in " + (System.currentTimeMillis() - begin) + " ms");

	}

}
