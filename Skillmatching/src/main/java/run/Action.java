package run;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import process.*;
import reader.*;


/**This class is used to create all files, mappings and queries of the project*/
public class Action {
	public static void main(String args[]) throws OWLOntologyCreationException {
		//initialization of variables used:
		String projectdir=System.getProperty("user.dir").replace("\\", "/");
		
		
		/*At first the skill ontology is instantiated. For this a JSONReader is created that extracts
		the input data from a json formated file. Than an OntoModeler is used to instantiate the data into
		the semantic net.*/
		
		////////////////////////////////////////////////////////////////////////////////
		//
		//					Skill ontology variables
		//
		////////////////////////////////////////////////////////////////////////////////
		String skill_inputfilelocation=projectdir+"/src/main/resources/inputdata/skills_schema.json";
		String skill_ontolocation=projectdir+"/src/main/resources/inputdata/on_skills_void.owl";
		String skill_ontofilelocation=projectdir+"/src/main/resources/ontology/on_skills.owl";
		String skill_prefix="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/on_skills.owl";
				
		////////////////////////////////////////////////////////////////////////////////
		//
		//					Skill ontology instantiation
		//
		////////////////////////////////////////////////////////////////////////////////
		
		//The skill ontology is already build with classes and relations.
		//The application case data for the skills to use as instances is provided in a separate json file.
		
		//first create instances for the skill ontology relating on the skill inputs
		SkillReader skully= new SkillReader();
		//setting the location of the json input file
		skully.setFileLocation(skill_inputfilelocation);
		//create a reader to read the set file
		try {
			skully.createReader();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//create a pointer to the section in the json input file we are looking at. Here we want to go into the skill_target section
		skully.setPointer("/skill_targets");
		//setting the keys for targets, entity_types, and skill_actions. 
		//Here they have the same naming, but in other cases choose the equivalent keys
		skully.setSkill_target("skill_target");
		skully.setEntity_type("entity_type");
		//We do not have to set the skill_action, because they are used as objectproperties (relation between classes) in the ontology. The architecture is already build.
		//Set ontologylocation of the void ontology to instantiate. 
		skully.setOntoLocation(skill_ontolocation);
		//set prefix for the instances
		skully.setSkillPrefix(skill_prefix);
		//instantiate the set targets as instances of a class relating to the respective entity_types
		skully.instantiateTargets();
		//save instantiated ontology. 
		skully.saveOntology(skill_ontofilelocation);
		
		
		
		/*In a second step a mapping OntoModeler is created to instantiate Project 
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
		mapping.setIRI("https://github.com/konierik/O-N/raw/master/ontology/Family2.owl");
		//Loading the ontology, here: from web
		mapping.loadOnto();
		/*Define what mapping annotations should be looked for (in case there are more mappings in the ontology).
		 * Mappings can be named differently if the ontology is used to map several sources of different structure.*/
		mapping.setClassmapping("classmapping");
		mapping.setClassIdent("identifier");
		mapping.setObjectpropertymapping("objectpropertymapping");
		mapping.setDatapropertymapping("datapropertymapping");
		
		// Extract the pointers from the mapping annotations and iris of the respecting concepts as lists
		ArrayList<ArrayList<String>> classannotations = mapping.getClassesAnnotations(); //Format: classIRI|classmapping pointer|identifier pointer
		ArrayList<ArrayList<String>> dataannotations=mapping.getDatapropertiesAnnotations(); //Format: dataproertyIRI|datapropertymapping pointer
		ArrayList<ArrayList<String>> objectannotations=mapping.getObjectpropertiesAnnotations(); //Format: objectproperty IRI|objectpropertymapping pointer
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			Instance Ontology
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//Create an OntoModeler for the ontology to populate instances
		OntoModeler instance=new OntoModeler();
		instance.setIRI("https://github.com/konierik/O-N/raw/master/ontology/Family_instance_mapping.owl");
		instance.createOnto("C:\\Users\\konierik\\Desktop\\Family_test\\Family_instance_mapping.owl");
		//instance.loadOnto("C:\\Users\\konierik\\Desktop\\Family_Test\\Family_instance_mapping.owl");
		//import mapping ontology
		instance.importFromURL(mapping.getIRIString());
		
		//import instances into the mapping ontology:
		mapping.importFromURL(instance.getIRIString());
		
	}
}
