package run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import query.Queries;
import query.QueryExec;

public class RunQuery {
	public static void main (String[] args) throws IOException {
		
		QueryExec searchforme= new QueryExec();
		searchforme.setIRI("https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_Instances.owl");
		searchforme.openModel("OWL");
		
		Queries q= new Queries();
		LinkedList<ArrayList<String>> result=searchforme.execQuery(q.UserSkillInterest());
		
		for (Iterator<ArrayList<String>> rs=result.iterator(); rs.hasNext();) {
			ArrayList<String> resultat = rs.next();
			resultat.forEach(System.out::println);
		}
			
		}
		
}
