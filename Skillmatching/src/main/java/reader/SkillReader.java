package reader;



import java.io.BufferedReader;
import java.io.FileNotFoundException;



import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

//using the javax.json package to handle json data with json pointer
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonStructure;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import process.OntoModeler;

/**SkillReader class is used to read skill instance data from a json formatted file, reading a skill OWL-ontology
 * and instantiate the json data as OWL instances.
 * <p>
 * This class instantiates a json formatted file with skill information. It reads skill_targets and creates individuals. 
 * A "entity_type" information in the json formatted file specifies into what class the skill_target should be instantiated.
 * skill_target=Individual
 * entity_type=Class of the respective individual*/
public class SkillReader {

	
	
	/**skills is a namespace variable for the instances. Skills will be instantiated as: skills+"#instance_attribut"*/
	private static String skills="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/skills.owl";
	/**JsonReader reader is used to read and parse information of json files.
	 * @type JsonReader JsonReader is a custom class in the project.*/
	private JsonReader reader;
	/**OntoModeler mod holds functions to handle the OWL ontology (get attributes, intsantiate classes/properties, load/save ontologies...).
	 * It is used to load the void skill-architecture and to instantiate the skill infomation into it.*/
	private OntoModeler mod = new OntoModeler();
	
	//variables for:
	
	/**Variable for the location of the json skills file holding the information for the skills.*/
	private String filelocation;
	/**Variable for the location of the skill OWL-ontology file.*/
	private String ontofile;
	/**Pointer string, that references to the field in the json file that should be parsed.*/
	private String pointer;
	/**Variable for a key in the pointer field, that is looked for.*/
	private String skill_target;
	/**Variable for a key in the pointer field, that is looked for.*/
	private String entity_type;
	/**Variable for a key in the pointer field, that is looked for.*/
	private String skill_action;
	
	
	
	/**This method creates a reader for the given skills json file location.
	 * The file location can be set with a separate method setFileLocation().*/
	public void createReader() throws FileNotFoundException {
		//at first it tries to load the skills as the location is given as URL
		try{
			System.out.println("Try to load as URL.");
			URL fileURL= new URL(filelocation);
			BufferedReader read= new BufferedReader(new InputStreamReader(fileURL.openStream()));
			reader = Json.createReader(read);
			
		}catch (Exception e) {
			System.out.println("Could not load URL: "+e);
			System.out.println("Try to load as local file.");
			//second try is to load the json file as local file
			try {
				reader=Json.createReader(new FileReader(filelocation));
			} catch(Exception ex) {
				System.out.println("Could not load file: "+ex);
			}
		}
	}
	
	/**This method instantiates the skill_targets as instances of entity_type classes. 
	 * The entity_types and skill_actions are already in the ontology as classes and objectproperties.*/
	public void instantiateTargets() {
		//read the json file
		JsonStructure jsonStructure = reader.read();
		//creating the json pointer to search for in the file
		JsonPointer jsonPointer = Json.createPointer(pointer);
		//Array to save results
		JsonArray jsonArray=null;
		//check if the pointer exists in the json file structure
		if(jsonPointer.containsValue(jsonStructure)) {
			//if pointer/data exists: 
			//loading ontology from local file
			try {
				mod.loadOnto(ontofile);
				//setSkillPrefix(mod.getIRIString());
			} catch (OWLOntologyCreationException e) {
				System.out.println("Ontology was not loaded: ");
				e.printStackTrace();
			}
			//loading the pointed information as JsonArray
			jsonArray=jsonPointer.getValue(jsonStructure).asJsonArray();
			//running through the Array and looking for the values of the keys "skill_target" and "entity_type", than instantiating them as individuals of a class
			for (int i=0; i<jsonArray.size();i++) {
				String instance=jsonArray.get(i).asJsonObject().getString(skill_target);
				String Class=jsonArray.get(i).asJsonObject().getString(entity_type);
				mod.instantiateClass(skills+"#"+instance, skills+"#"+Class);		
			}
		}
		reader.close();
		
	}
	
	
	/**This method calls the saving function of the OntoModeler handling the ontology.
	 * @OntoModeler OntoModeler is a custom class in the project.
	 * @param toLocation FilePath (including filename and extension) to where the new instantiated ontology should be saved into.*/
	public void saveOntology(String toLocation) {
		try {
			mod.saveOntology(toLocation);
		} catch (OWLOntologyStorageException | OWLOntologyCreationException | IOException e) {
			System.out.println("Ontology not saved:");
			e.printStackTrace();
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//							GETTER
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getFileLocation() {
		return filelocation;
	}
	
	public String getOntoLocation() {
		return ontofile;
	}

	public String getPointer() {
		return pointer;
	}

	public String getSkill_target() {
		return skill_target;
	}

	public String getEntity_type() {
		return entity_type;
	}

	public String getSkill_action() {
		return skill_action;
	}
	
	public String getSkillPrefix() {
		return skills;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//							SETTER
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setFileLocation(String file) {
		this.filelocation = file;
	}

	public void setOntoLocation(String ontofile) {
		this.ontofile = ontofile;
	}

	public void setPointer(String pointer) {
		this.pointer = pointer;
	}

	public void setSkill_target(String skill_target) {
		this.skill_target = skill_target;
	}

	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}

	public void setSkill_action(String skill_action) {
		this.skill_action = skill_action;
	}

	public void setSkillPrefix(String prefix) {
		skills=prefix;
	}
	
	/*
	static void main(String[] args) throws IOException, OWLOntologyStorageException, OWLOntologyCreationException {
		//creating a reader and a pointer for the json-file
		JsonReader reader = Json.createReader(new FileReader("C:/Users/konierik/Nextcloud2/MA-Arbeit/03_Ontology/Skills/WIF_data/exported_schema.json"));
		//read file
		JsonStructure jsonStructure = reader.read();
		//looking for "/skill_targets" in the file
		JsonPointer jsonPointer = Json.createPointer("/skill_targets");
		//Array to save results
		JsonArray jsonArray=null;
		//creating a OntologyModeler, that provides needed functions to handle owl-ontologies
		OntoModeler ont=new OntoModeler();
		//check if the pointer exists in the json file structure
		if(jsonPointer.containsValue(jsonStructure)) {
			//if pointer/data exists: 
			//loading ontology from local file
			ont.loadOnto("C:\\Users\\konierik\\Nextcloud2\\MA-Arbeit\\03_Ontology\\github\\skills_wif_merge.owl");
			//loading the pointed information as JsonArray
			jsonArray=jsonPointer.getValue(jsonStructure).asJsonArray();
			//running through the Array and looking for the values of the keys "skill_target" and "entity_type", than instantiating them as individuals of a class
			for (int i=0; i<jsonArray.size();i++) {
				String instance=jsonArray.get(i).asJsonObject().getString("skill_target");
				String Class=jsonArray.get(i).asJsonObject().getString("entity_type");
				ont.instantiateClass(skills+"#"+instance, skills+"#"+Class);		
			}
			//locally saving the new instantiated ontology
			ont.saveOntology("C:\\Users\\konierik\\Nextcloud2\\MA-Arbeit\\03_Ontology\\github\\skills_wif_merge_instances.owl");
		}
		reader.close();
		
	}*/
}
