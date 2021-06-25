package run;

import java.io.FileNotFoundException;

import process.*;
import reader.*;


/**This class is used to create all files, mappings and queries of the project*/
public class Action {
	public static void main(String args[]) {
		//initialization of variables used:
		String projectdir=System.getProperty("user.dir").replace("\\", "/");
		
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
		//The application case data for the skills to use as instances is provided in a json file.
		
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
		
		
	}
}
