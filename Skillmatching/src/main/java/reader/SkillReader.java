package reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

/*This class instantiates a json formatted file with skill information. It reads skill_targets and creates individuals. 
 * A "entity_type" information in the json formatted file specifies into what class the skill_target should be instantiated.
 * skill_target=Individual
 * entity_type=Class of the respective individual*/

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

public class SkillReader {
	
	private static String skills="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/skills.owl";
	private JsonReader reader;
	private OntoModeler mod = new OntoModeler();
	
	private String file;
	private String ontofile;
	private String pointer;
	private String skill_target;
	private String entity_type;
	private String skill_action;
	
	

	
	
	public void createReader() throws FileNotFoundException {
		try{
			System.out.println("Try to load as URL.");
			URL fileURL= new URL(file);
			BufferedReader read= new BufferedReader(new InputStreamReader(fileURL.openStream()));
			reader = Json.createReader(read);
			
		}catch (Exception e) {
			System.out.println("Could not load URL: "+e);
			System.out.println("Try to load as local file.");
			try {
				reader=Json.createReader(new FileReader(file));
			} catch(Exception ex) {
				System.out.println("Could not load file: "+ex);
			}
		}
		
		
	}
	
	
	public String getFileLocation() {
		return file;
	}

	public void setFileLocation(String file) {
		this.file = file;
	}

	public String getOntoLocation() {
		return ontofile;
	}

	public void setOntoLocation(String ontofile) {
		this.ontofile = ontofile;
	}

	public String getPointer() {
		return pointer;
	}

	public void setPointer(String pointer) {
		this.pointer = pointer;
	}

	public String getSkill_target() {
		return skill_target;
	}

	public void setSkill_target(String skill_target) {
		this.skill_target = skill_target;
	}

	public String getEntity_type() {
		return entity_type;
	}

	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}

	public String getSkill_action() {
		return skill_action;
	}

	public void setSkill_action(String skill_action) {
		this.skill_action = skill_action;
	}
	
	public void instantiateTargets() {
		//read file
		JsonStructure jsonStructure = reader.read();
		//looking for "/skill_targets" in the file
		JsonPointer jsonPointer = Json.createPointer(pointer);
		//Array to save results
		JsonArray jsonArray=null;
		//check if the pointer exists in the json file structure
		if(jsonPointer.containsValue(jsonStructure)) {
			//if pointer/data exists: 
			//loading ontology from local file
			try {
				mod.loadOnto(ontofile);
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
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
	public void saveOntology(String toLocation) {
		try {
			mod.saveOntology(toLocation);
		} catch (OWLOntologyStorageException | OWLOntologyCreationException | IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Ontology not saved:");
			e.printStackTrace();
		}
	}
	
	
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
		
	}
}
