package run;

import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import process.JSON2NTmapper;
import process.NTParser;
import process.OntoModeler;
import reader.JSONReader;

public class CreateInstances {

	//variable for skill ontology iri
	private static String skillIRI="https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl";
	//variable for mapping ontology iri
	private static String mappingIRI="https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_OSHPDP_schema.owl";
	//variable for instance ontology iri
	private static String instanceIRI="https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_Instances.owl";
	
	
	public static void main (String[] args) throws IOException {
		/*A mapping OntoModeler is created to instantiate Project 
		 * and User data for the OSHPDP ontology, that represents a project landscape for OSH projects. 
		 * In this case the OSHPDP ontology itself contains the mapping annotations. A mapping annotation is 
		 * a json pointer representing where to find the information in a json formatted file. The annotations in
		 * the OSHPDP ontology is based on the graphQL api schema structure of the projectpartner Wikifactory (https://wikifactory.com/api/graphql)
		 * but can be changed to any json format. A classmapping represents a pointer which information 
		 * will be than instantiated as a OWLClass of the OSHPD. A Objectpropertymapping pointer will be instantiated
		 * as the relating object property (same for datapropertymapping) and a identifiermapping represents the domain
		 * of the relating property. 
		 * As example: An object property is annotated with an objectpropertymapping-annotation. 
		 * The value behind the pointer in the annotation is instantiated as range instance of the object property.
		 * Additionally to data-/objectpropertymappings there is a identifier mapping, which contains the pointer to the
		 * respective domain class information for the property.*/
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Mapping Ontology
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Creating an OntoModeler for the ontology holding the mappings
		OntoModeler mapping=new OntoModeler();
		//Setting the IRI of the ontology
		mapping.setIRI(mappingIRI);
		//Loading the ontology, here: from web
		mapping.loadOnto();
		//merge the skill ontology into the Modeler, so every class, property and instance is accessible
		mapping.mergeOntology(skillIRI);
		//annotation arrays for later instaniation
		ArrayList<ArrayList<String>> classannotations=null;
		ArrayList<ArrayList<String>> dataannotations = null;
		ArrayList<ArrayList<String>> objectannotations = null;
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			setup json reader
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////

		//create one JSONReader per data file: issues, projects, user
		JSONReader readIssues=new JSONReader();
		//set the file to read and open it
		readIssues.setFile("C://Springboot-Repository//Skillmatch//Skillmatching//data//sampledata_issues_anonym.json");
		readIssues.open();
		
		JSONReader readUser=new JSONReader();
		//set the file to read and open it
		readUser.setFile("C://Springboot-Repository//Skillmatch//Skillmatching//data//sampledata_user_anonym.json");
		readUser.open();
		
		JSONReader readProjects=new JSONReader();
		//set the file to read and open it
		readProjects.setFile("C://Springboot-Repository//Skillmatch//Skillmatching//data//sampledata_projects_anonym.json");
		readProjects.open();
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			setup json2ntmapper
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////

		JSON2NTmapper ntmapper=new JSON2NTmapper();
		//setting iris for instantiation
		ntmapper.setInstanceIRI(instanceIRI);
		ntmapper.setMappingIRI(mapping.getIRI().toString());
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Instantiate issue-mappings
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//going through all 5 issue_mapping_annotations
		System.out.println("Instantiate Issue-mappings");
		for (int i=1; i<=5;i++) { 
			/*Define what mapping annotations should be looked for (in case there are more mappings in the ontology).
			 * Mappings can be named differently if the ontology is used to map several sources of different structure.*/
			mapping.setClassmapping("wif_issue_"+i+"_cmap");
			//mapping.setClassIdent("identifier");
			mapping.setObjectpropertymapping("wif_issue_"+i+"_opmap");
			mapping.setDatapropertymapping("wif_issue_"+i+"_dpmap");
			
			// Extract the pointers from the mapping annotations and iris of the respecting concepts as lists
			classannotations = mapping.getClassesAnnotations(); //Format: [[classmapping pointer][rdfsType][classIRI]]
			dataannotations = mapping.getDatapropertiesAnnotations(); //Format: dataproertyIRI|datapropertymapping pointer
			objectannotations = mapping.getObjectpropertiesAnnotations(); //Format: objectproperty IRI|objectpropertymapping pointer
				
			ntmapper.instantiateToNTClasses(classannotations, readIssues );
			ntmapper.instantiateToNTDataproperties(dataannotations, readIssues);
			ntmapper.instantiateToNTObjectproperties(objectannotations, readIssues);
		}
		System.out.println("\n\n");
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Instantiate user-mappings
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//going through all 6 user_mapping_annotations
		System.out.println("Instantiate User-mappings");
		for (int i=1; i<=5;i++) { 
			/*Define what mapping annotations should be looked for (in case there are more mappings in the ontology).
			 * Mappings can be named differently if the ontology is used to map several sources of different structure.*/
			mapping.setClassmapping("wif_user_"+i+"_cmap");
			//mapping.setClassIdent("identifier");
			mapping.setObjectpropertymapping("wif_user_"+i+"_opmap");
			mapping.setDatapropertymapping("wif_user_"+i+"_dpmap");
			
			// Extract the pointers from the mapping annotations and iris of the respecting concepts as lists
			classannotations = mapping.getClassesAnnotations(); //Format: [[classmapping pointer][rdfsType][classIRI]]
			dataannotations = mapping.getDatapropertiesAnnotations(); //Format: dataproertyIRI|datapropertymapping pointer
			objectannotations = mapping.getObjectpropertiesAnnotations(); //Format: objectproperty IRI|objectpropertymapping pointer
				
			ntmapper.instantiateToNTClasses(classannotations, readUser );
			ntmapper.instantiateToNTDataproperties(dataannotations, readUser);
			ntmapper.instantiateToNTObjectproperties(objectannotations, readUser);
		}
		System.out.println("\n\n");
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Instantiate prject-mappings
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//going through all 3 project_mapping_annotations
		System.out.println("Instantiate User-mappings");
		for (int i=1; i<=3;i++) { 
			/*Define what mapping annotations should be looked for (in case there are more mappings in the ontology).
			 * Mappings can be named differently if the ontology is used to map several sources of different structure.*/
			mapping.setClassmapping("wif_project_"+i+"_cmap");
			//mapping.setClassIdent("identifier");
			mapping.setObjectpropertymapping("wif_project_"+i+"_opmap");
			mapping.setDatapropertymapping("wif_project_"+i+"_dpmap");
			
			// Extract the pointers from the mapping annotations and iris of the respecting concepts as lists
			classannotations = mapping.getClassesAnnotations(); //Format: [[classmapping pointer][rdfsType][classIRI]]
			dataannotations = mapping.getDatapropertiesAnnotations(); //Format: dataproertyIRI|datapropertymapping pointer
			objectannotations = mapping.getObjectpropertiesAnnotations(); //Format: objectproperty IRI|objectpropertymapping pointer
				
			ntmapper.instantiateToNTClasses(classannotations, readProjects );
			ntmapper.instantiateToNTDataproperties(dataannotations, readProjects);
			ntmapper.instantiateToNTObjectproperties(objectannotations, readProjects);
		}
		System.out.println("\n\n");
		
		readIssues.close();
		readUser.close();
		readProjects.close();
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			parse nt file, adding prefixes and import statements and converting to ttl format
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
		//Set output string for file
		String ntoutput="C://Springboot-Repository//Skillmatch//Skillmatching//data//on_Instances.nt";
		ntmapper.setNToutputLocation(ntoutput);
		ntmapper.toNTFile();
		
		NTParser ntparse=new NTParser(ntoutput);
		ntparse.setPrefix(":", instanceIRI);
		ntparse.setPrefix("oshpd", mapping.getIRIString());
		ntparse.setPrefix("skills", skillIRI);
		ntparse.setPrefix("owl", "http://www.w3.org/2002/07/owl");
		ntparse.readNTModel();
		ntparse.setOntologyIRI(instanceIRI);
		ntparse.addImport(mapping.getIRIString());
		ntparse.addImport(skillIRI);
		ntparse.setOutput("C://Springboot-Repository//Skillmatch//Skillmatching//data//on_Instances.owl");
		ntparse.parseNT(instanceIRI,"RDF/XML");
		
		
		//infer asserted axioms of the new ontology:
		OntoModeler instance= new OntoModeler();
		instance.setIRI(instanceIRI);
		instance.loadOnto();
		//instance.mergeOntology(skillIRI);
		//instance.mergeOntology(mapping.getIRIString());
		instance.assertInferences();
		try {
			instance.saveOntology("C://Springboot-Repository//Skillmatch//Skillmatching//data//on_Instances.owl");
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	
	}
}
