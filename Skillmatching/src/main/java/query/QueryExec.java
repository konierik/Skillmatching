package query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

public class QueryExec {
	
	private String ontologyiri="";
	private OntModel modello;
	
	public QueryExec(String iri, String format) {
		this.setIRI(iri);
		this.openModel(format);
	}
	
	public QueryExec() {
		this.setIRI("https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_Instances.owl");
		this.openModel("OWL");
	}

	public LinkedList<ArrayList<String>> execQuery(String qstring) {
		Query query = QueryFactory.create(qstring);
		QueryExecution qe = QueryExecutionFactory.create(query, modello);
		try {
		    ResultSet results =  qe.execSelect();
			LinkedList<ArrayList<String>> caseIdArray = new LinkedList<ArrayList<String>>();
    		ArrayList<String> ResultNames =(ArrayList<String>)results.getResultVars();
    		caseIdArray.addFirst(ResultNames);
			for (; results.hasNext();){
	        	ArrayList<String> SelectVar =new ArrayList<String>();
	    		QuerySolution rb = results.nextSolution();

	    		for (int i=0;i<ResultNames.size();i++){   		
	    			try{
	    				RDFNode data=rb.get(ResultNames.get(i));
	    				SelectVar.add(data.toString());
	    				
	    			}catch(Exception e){
	    				System.out.println("Error: "+e);
	    			}
	    		}
			caseIdArray.add(SelectVar);
			}
			
			
			//transposing array to make it readable for converting method
	    	LinkedList<ArrayList<String>>caseIdArrayTrans=new LinkedList<ArrayList<String>>();
	    	for(int i=0;i<caseIdArray.get(0).size();i++){
	    		ArrayList<String> SpalteWirdZeile =new ArrayList<String>();
	    		for (int j=0;j<caseIdArray.size();j++){
	    			SpalteWirdZeile.add(caseIdArray.get(j).get(i));
	    		}
	    		caseIdArrayTrans.add(SpalteWirdZeile);
	    	}
	    	return caseIdArrayTrans;
//			return caseIdArray;
    	}
	    // Output query results    
	   //ResultSetFormatter.out(System.out, results, query);
	    finally{qe.close();}
	    
	}
	
	public void openInferenceModel(String language) {
		modello=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		modello.read(ontologyiri, language);
	}
	
	public void openModel(String language) {
		modello=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		modello.read(ontologyiri, language);
	}
	
	public void setIRI(String iri) {
		ontologyiri=iri;
	}
	

}
