package process;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import reader.JSONReader;

public class JSON2NTmapper {
	
	private OntoModeler mapping= new OntoModeler();
	private String objectpropertymapping;
	private String classmapping;
	private String datapropertymapping;
	private String identifier;
	
	
	private String NToutputFile; //Format should be: "c:\\projects\\app.log"
	private String NTcontent;

	

	
	public static void main(String[]args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Mapping Ontology
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Creating an ontologymodeler for the ontology holding the mappings
		OntoModeler mapping=new OntoModeler();
		mapping.setIRI("https://github.com/konierik/O-N/raw/master/ontology/Family2.owl");
		mapping.loadOnto();
		// Define what mapping annotations should be looked for (in case there are more mappings in the ontology)
		mapping.setClassmapping("classmapping");
		mapping.setClassIdent("identifier");
		mapping.setObjectpropertymapping("objectpropertymapping");
		mapping.setDatapropertymapping("datapropertymapping");
		
		// Get the mapping annotations as lists
		ArrayList<ArrayList<String>> classannotations = mapping.getClassesAnnotations(); //Format: classIRI|classmapping pointer|identifier pointer
		ArrayList<ArrayList<String>> dataannotations=mapping.getDatapropertiesAnnotations(); //Format: dataproertyIRI|datapropertymapping pointer
		ArrayList<ArrayList<String>> objectannotations=mapping.getObjectpropertiesAnnotations(); //Format: objectproperty IRI|objectpropertymapping pointer
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Instance Ontology
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Create an ontologymodeler for the ontology to populate instances
		OntoModeler instance=new OntoModeler();
		instance.setIRI("https://github.com/konierik/O-N/raw/master/ontology/Family_instance_mapping.owl");
		instance.createOnto("C:\\Users\\konierik\\Desktop\\Family_test\\Family_instance_mapping.owl");
		//instance.loadOnto("C:\\Users\\konierik\\Desktop\\Family_Test\\Family_instance_mapping.owl");
		//import mapping ontology
		instance.importFromURL(mapping.getIRIString());
		
		//import instances into the mapping ontology:
		mapping.importFromURL(instance.getIRIString());
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			JSON input
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Create a JSONReader for the JSON input to be instantiated
		JSONReader jsonReader = new JSONReader();
		
		//JsonArray classes=jsonReader.instantiate(jsonReader.getReader(),"/data/Person");
		//ArrayList<String> values = jsonReader.getClassInstances(jsonReader.getReader(), classannotations.get(1).get(0), classannotations.get(2).get(0));
		//mapping.listOut(objectannotations);
		
		instantiateToOWLClasses(classannotations,jsonReader, instance);	
		instantiateToOWLObjectProperties(objectannotations, jsonReader, instance);
		instantiateToOWLDataProperties(dataannotations, jsonReader, instance);
		
		instance.saveOntology("C:\\Users\\konierik\\Desktop\\Family_test\\Family_instance_mapping.owl");
		
		//han: create  namespaces --> is there already?

	}
	
	public void loadMappingOntology(String mappingiri) {
		
		
	}
	
	public static void instantiateToOWLClasses(ArrayList<ArrayList<String>> annotations, JSONReader reader, OntoModeler onto) throws IOException{
		//loop for every class-iri in the array
		for (int i=0; i<annotations.get(0).size();i++) {
			reader.setFile("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");//setting input filelocation
			reader.open(); //open reader 
			//needed variables: class-iri to instantiate, class pointer to get the class objects from json file, ident pointer to get the ident-property out of the json objects
			String classy=annotations.get(0).get(i);
			String pointer = annotations.get(1).get(i);
			String identifier= annotations.get(2).get(i);
			try{
				//getting the identifier values from all instances of the class i
				ArrayList<String> jsonResult = reader.getClassInstances(reader.getReader(), pointer, identifier);
				//running through all found instances of class i
				for (int j=0; j<jsonResult.size(); j++) {
					//getting jth-instance value
					String value=jsonResult.get(j);
					//instantiate the jth- value into the ontology as an individual of class i
					onto.instantiateClass(onto.getIRIString()+"#"+value, classy);
					System.out.println(onto.getIRIString()+"#"+value+" instantiated as "+classy+".");
				}
				System.out.println("\n");
			}catch(Exception e) {
				e.printStackTrace();
				}
			reader.close(); //closing reader: opening-closing is necessary since there requests per reader are limited
		}
		
	}
	
	public static void instantiateToOWLDataProperties(ArrayList<ArrayList<String>> annotations, JSONReader reader, OntoModeler onto) throws IOException {
		for (int i=0; i<annotations.get(0).size();i++) {
			reader.setFile("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");//set input file location
			reader.open();//open reader
			String datas=annotations.get(0).get(i);
			String pointer = annotations.get(1).get(i);
			String domain= annotations.get(2).get(i);
			String identifier= annotations.get(3).get(i);
			try{
				ArrayList<ArrayList<String>> jsonResult = reader.getValuesFromArray(reader.getReader(), domain, identifier, pointer);
				for (int j=0; j<jsonResult.get(0).size(); j++) {
					
					onto.instantiateDataProperty(onto.getIRIString()+"#"+jsonResult.get(0).get(j), datas,jsonResult.get(1).get(j));
					System.out.println(onto.getIRIString()+"#"+jsonResult.get(0).get(j)+" "+datas+" "+jsonResult.get(1).get(j));
				}
				System.out.println("\n");
			}catch(Exception e) {
				e.printStackTrace();
				}
			reader.close();
		}
	}


	public static void instantiateToOWLObjectProperties(ArrayList<ArrayList<String>> annotations, JSONReader reader, OntoModeler onto) throws IOException {
		for (int i=0; i<annotations.get(0).size();i++) {
			reader.setFile("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");//set input file location
			reader.open(); //open reader
			String objects=annotations.get(0).get(i);
			String pointer = annotations.get(1).get(i);
			String domain= annotations.get(2).get(i);
			String identifier= annotations.get(3).get(i);
			try{
				ArrayList<ArrayList<String>> jsonResult = reader.getValuesFromArray(reader.getReader(), domain, identifier, pointer);
				for (int j=0; j<jsonResult.get(0).size(); j++) {
					
					onto.instantiateObjectProperty(onto.getIRIString()+"#"+jsonResult.get(0).get(j), objects,jsonResult.get(1).get(j));
					System.out.println(onto.getIRIString()+"#"+jsonResult.get(0).get(j)+" "+objects+" "+onto.getIRIString()+"#"+jsonResult.get(1).get(j));
				}
				System.out.println("\n");
			}catch(Exception e) {
				e.printStackTrace();
				}
			reader.close();
		}
	}
	
	public void toNTFile() {
		try {
			Files.write(Paths.get(NToutputFile), NTcontent.getBytes());
			
		} catch(IOException e) {
			System.out.println("Not successful writing.");
			e.printStackTrace();
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			setter
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setNToutputLocation(String out) {
		NToutputFile=out;
	}
	
	public void setObjectPropertyMapping(String opm) {
		objectpropertymapping= opm;
		mapping.setObjectpropertymapping(opm);
	}
	
	public void setDataPropertyMapping(String dpm) {
		datapropertymapping= dpm;
		mapping.setDatapropertymapping(dpm);
	}
	
	public void setClassMapping(String cm) {
		classmapping= cm;
		mapping.setClassmapping(cm);
	}
	
	public void setIdentifierMapping(String im) {
		identifier= im;
		mapping.setClassIdent(im);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			getter
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getObjectPropertyMapping() {
		return objectpropertymapping;
	}
	
	public String getDataPropertyMapping() {
		return datapropertymapping;
	}
	
	public String getClassMapping() {
		return classmapping;
	}
	
	public String getIdentifierMapping() {
		return identifier;
	}
	
	public String getNToutputLocation() {
		return NToutputFile;
	}
	
}

