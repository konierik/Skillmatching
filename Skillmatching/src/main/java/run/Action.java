package run;

import static org.mockito.Mockito.inOrder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import process.*;
import reader.*;


/**This class is used to create all files, mappings and queries of the project*/
public class Action {
	@SuppressWarnings("unused")
	public static void main(String args[]) throws OWLOntologyCreationException, IOException {
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
		String skillIRI="https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl";

				
		////////////////////////////////////////////////////////////////////////////////
		//
		//					Skill ontology instantiation
		//
		////////////////////////////////////////////////////////////////////////////////
		
		//The skill ontology is already build with classes and relations.
		//The application case data for the skills to use as instances is provided in a separate json file.
		
		/*
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
		
		*/
		
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
		mapping.setIRI("https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_OSHPDP_schema.owl");
		//Loading the ontology, here: from web
		mapping.loadOnto();
		mapping.mergeOntology(skillIRI);
		/*Define what mapping annotations should be looked for (in case there are more mappings in the ontology).
		 * Mappings can be named differently if the ontology is used to map several sources of different structure.*/
		mapping.setClassmapping("wif_issue_3_cmap");
		//mapping.setClassIdent("identifier");
		mapping.setObjectpropertymapping("wif_issue_3_opmap");
		mapping.setDatapropertymapping("wif_issue_3_dpmap");
		
		// Extract the pointers from the mapping annotations and iris of the respecting concepts as lists
		ArrayList<ArrayList<String>> classannotations = mapping.getClassesAnnotations(); //Format: [[classmapping pointer][rdfsType][classIRI]]
		ArrayList<ArrayList<String>> dataannotations = mapping.getDatapropertiesAnnotations(); //Format: dataproertyIRI|datapropertymapping pointer
		ArrayList<ArrayList<String>> objectannotations = mapping.getObjectpropertiesAnnotations(); //Format: objectproperty IRI|objectpropertymapping pointer
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			variables for instance ontology
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////

		String instanceIRI="https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_Instances.ttl";
		
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
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//			setup json2ntmapper
		//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////

		JSON2NTmapper ntmapper=new JSON2NTmapper();
		//setting iris for instantiation
		ntmapper.setInstanceIRI(instanceIRI);
		ntmapper.setMappingIRI(mapping.getIRI().toString());
		//mapping the annotations to NT
		ntmapper.instantiateToNTClasses(classannotations, readIssues );
		ntmapper.instantiateToNTDataproperties(dataannotations, readIssues);
		ntmapper.instantiateToNTObjectproperties(objectannotations, readIssues);
		//Set output string for file
		String ntoutput="C://Springboot-Repository//Skillmatch//Skillmatching//data//on_Instances.nt";
		ntmapper.setNToutputLocation(ntoutput);
		ntmapper.toNTFile();
		NTParser ntparse=new NTParser(ntoutput);
		ntparse.setPrefix("", instanceIRI);
		ntparse.setPrefix("oshpd", mapping.getIRIString());
		ntparse.setPrefix("skills", skillIRI);
		ntparse.readNTModel();
		ntparse.setOntologyIRI(instanceIRI);
		ntparse.addImport(mapping.getIRIString());
		//ntparse.addImport("https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl");
		ntparse.setOutput("C://Springboot-Repository//Skillmatch//Skillmatching//data//on_Instances.ttl");
		ntparse.parseNT(instanceIRI);
		/*
		for (int i=0; i<classannotations.get(0).size();i++) { 
			ArrayList<ArrayList<String>> jsondata = readIssues.parsePointer(classannotations.get(0).get(i));
			for (int j=0; j<jsondata.get(0).size();j++) {
				
			}
			System.out.println(i);
			System.out.println(jsondata);
		}*/
		

	
	}
}
