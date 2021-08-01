package run;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import reader.SkillReader;

public class CreateSkill {
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
		String skill_inputfilelocation=projectdir+"/data/skills_schema.json";
		String skill_ontolocation=projectdir+"/data/on_skills_void.owl";
		String skill_ontofilelocation=projectdir+"/on_skills_classes.owl";
		
		String skill_prefix="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/on_skills.owl";
		//String skillIRI="https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl";

				
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
		skully.instantiateTargetsAsClass();
		//save instantiated ontology. 
		skully.saveOntology(skill_ontofilelocation);
		

	}
}
