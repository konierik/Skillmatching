package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

//another source: https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
//https://stackabuse.com/reading-and-writing-json-in-java/

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;


//https://www.baeldung.com/json-pointer
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;

public class JSONReader {
	
	//public static JsonArray jsonArray;
	//public static JsonObject jsonObject;
	//public static JsonString jsonString;
	
	private JsonReader reader;
	
	/*public static void main (String[] args) throws URISyntaxException, IOException {
		//read();
		open("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");
		String pointPerson= "/data/Person";
		String pointID="/data/Person/ID";
		JsonArray person=instantiate(reader,"/data/Person");
		for (int i=0;i<person.size();i++) {
		System.out.println("getID: "+person.getValue("/"+i+schort(pointPerson,pointID)));
		}
		
		
		close(reader);
	}*/
	
	public String schort(String pointer, String identifier) {
		return identifier.replaceAll(pointer,"");
		
	}
	//open a json reader from URL json file location
	public void open(String file) throws IOException {
		URL fileURL= new URL(file);
		BufferedReader read= new BufferedReader(new InputStreamReader(fileURL.openStream()));
		reader = Json.createReader(read);//new FileReader(file))
		//return reader;
	}
	//open json reader from local json file location
	public void openDoc(String file) throws IOException {
		reader = Json.createReader(new FileReader(file));//new FileReader(file))
		//return reader;
	}
	
	//close the reader
	public void close(JsonReader reader) {
		reader.close();
	}
	
	
	//parsing a pointer with array markers "~"
	public ArrayList<ArrayList<String>> parsePointer(String input){
		//create Array for json reading and data cache
		JsonStructure jsonStructure = reader.read();
		JsonPointer jsonPointer = null;// = Json.createPointer(pointer);
		JsonArray data=null;
		//creating lists for output
		ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
		ArrayList<String> outPartOne = new ArrayList<String>();//
		ArrayList<String> outPartTwo = new ArrayList<String>();//
		//count the '~' char, that marks an array in the pointer
		int count= (int) input.chars().filter(cha->cha=='~').count();
				
		if (count==0) {
			jsonPointer=Json.createPointer(input);
			//check if the pointer exists:
			if(jsonPointer.containsValue(jsonStructure)) {
				data=jsonPointer.getValue(jsonStructure).asJsonArray();
				for (int i=0;i<data.size();i++) {
					outPartOne.add(input);
					outPartTwo.add(data.get(i).toString());
				}
			}
		}else {
			//getting the array of the first array marker '~'
			String newInput= input.substring(0, input.indexOf('~'));
			jsonPointer=Json.createPointer(newInput);
			data=jsonPointer.getValue(jsonStructure).asJsonArray();
			//running through the array:
			for (int i=0; i<data.size();i++) {
				//recursive funtion: add the arraylist<arraylist<string>> that is created from the next array marker '~'
				out.addAll(parsePointer(newInput+i+input.substring(input.lastIndexOf('~'))));
			}
			
		}
		//write into the arrays:
		out.add(0, outPartOne);
		out.add(1, outPartTwo);
		return out;
	}
	
	//gets an JsonArray for a respective input pointer string
	public JsonArray instantiate(JsonReader reader, String pointer) throws IOException {
		//reader=open("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");
		JsonStructure jsonStructure = reader.read();
		JsonPointer jsonPointer = Json.createPointer(pointer);
		JsonArray jsonArray=null;
		//check if pointer is existent in json-inputfile
		boolean found = jsonPointer.containsValue(jsonStructure);
		if (found) {
			jsonArray=jsonPointer.getValue(jsonStructure).asJsonArray(); //if pointer is found, it gets the array
			
			System.out.println("Pointer instantiated: "+jsonArray+"\n");
		} else {
			System.out.println("Pointer not in Json file. \n");
		}
		
		return jsonArray;
	}
	
	// gets ident for classes
	public ArrayList<String> getClassInstances(JsonReader reader, String pointer, String identifier){
		//to read the json
		JsonStructure jsonStructure = reader.read();
		JsonPointer jsonPointer = Json.createPointer(pointer);
		JsonArray jsonArray=null;
		//cache list
		ArrayList<String> values=new ArrayList<String>();
		//check if pointer is existent in json file
		boolean found = jsonPointer.containsValue(jsonStructure);
		if (found) {
			//if pointer exist, get the json-array from it
			jsonArray=jsonPointer.getValue(jsonStructure).asJsonArray();
			//runs through the array and get the values
			for (int i=0;i<jsonArray.size();i++) {
				values.add(jsonArray.getValue("/"+i+schort(pointer,identifier)).toString().replace("\"",""));
			}
		}
		return values;
	}
	
	
	//gets value data from key "propertypointer" and from key "identpointer"
	public ArrayList<ArrayList<String>> getValuesFromArray(JsonReader reader, String classpointer, String identpointer, String propertypointer){
		JsonArray arr = null;
		try {
			//get an array of all objects that contain information about the keys we want to have
			arr = instantiate(reader,classpointer);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//lists for saving the values
		ArrayList<String> domain= new ArrayList<String>();
		ArrayList<String> range= new ArrayList<String>();
		ArrayList<ArrayList<String>> ListOut =new ArrayList<ArrayList<String>>();
		//going throug the array
		for (int i=0;i<arr.size();i++) {
			if (!(arr.getValue("/"+i+schort(classpointer,identpointer)).toString().replace("\"", "").isEmpty()) && !arr.getValue("/"+i+schort(classpointer,propertypointer)).toString().replace("\"", "").isEmpty()) {
				try{
					//replace statement because all values come with quote marks
					domain.add(arr.getValue("/"+i+schort(classpointer,identpointer)).toString().replace("\"", ""));
					range.add(arr.getValue("/"+i+schort(classpointer,propertypointer)).toString().replace("\"", ""));
					System.out.println("Array class id: "+arr.getValue("/"+i+schort(classpointer,identpointer)));
					System.out.println(arr.getValue("/"+i+schort(classpointer,identpointer)).toString()!= "");
					System.out.println("Array property: "+arr.getValue("/"+i+schort(classpointer,propertypointer)));
					System.out.println(arr.getValue("/"+i+schort(classpointer,propertypointer)).toString().replace("\"", "").isEmpty());
				}catch (Exception e) {
				
				}
			}
		}
		//adding all values to another list
		ListOut.add(0,domain);
		ListOut.add(1,range);
		return ListOut;
	}
	
	public JsonString tryToJsonString (JsonPointer jsonPointer, JsonStructure jsonStructure){
		JsonString jsonString =null;
		try{
			jsonString = (JsonString) jsonPointer.getValue(jsonStructure);
			System.out.println(jsonString.toString());
			}
		catch(Exception e) {
			System.out.println("Exception: "+e);
		}
		return jsonString;
	}
	
	public JsonObject tryToJsonObject (JsonPointer jsonPointer, JsonStructure jsonStructure){
		JsonObject jsonObject =null;
		try{
			jsonObject = (JsonObject) jsonPointer.getValue(jsonStructure);
			System.out.println(jsonObject.toString());
			}
		catch(Exception e) {
			System.out.println("Exception: "+e);
		}
		return jsonObject;
	}
	
	public JsonArray tryToJsonArray (JsonPointer jsonPointer, JsonStructure jsonStructure){
		JsonArray jsonArray =null;
		try{
			jsonArray = (JsonArray) jsonPointer.getValue(jsonStructure);
			jsonArray.forEach(System.out::println);
			//System.out.println(jsonArray.toString());
			}
		catch(Exception e) {
			System.out.println("Exception: "+e);
		}
		return jsonArray;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//						Setter
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//						Getter
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public JsonReader getReader() {
		return reader;
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void read() throws URISyntaxException, IOException {
		
		;
		//InputStream input = fileURL.openStream();
		
		//BufferedReader read= new BufferedReader(new InputStreamReader(fileURL.openStream()));
		//read file
		//JsonReader reader = Json.createReader(read);//new FileReader(file));
		JsonStructure jsonStructure = reader.read();
		//reader.close();
		
		//check if a pointer is in the json structure
		JsonPointer jsonPointer2 = Json.createPointer("/data/Person");
		boolean found = jsonPointer2.containsValue(jsonStructure);
		System.out.println("Found: "+found);
		System.out.println("PointerType: "+ jsonPointer2.getValue(jsonStructure).getClass());

		
		//fetch value from file
		//JsonPointer jsonPointer = Json.createPointer("/data/Person");
		//System.out.println("Child type: "+jsonPointer.getClass());
		
		//get value if its a string
		JsonString jsonString= tryToJsonString(jsonPointer2,jsonStructure);
		JsonObject jsonObject= tryToJsonObject(jsonPointer2,jsonStructure);
		JsonArray jsonArray = tryToJsonArray(jsonPointer2,jsonStructure);
		/*try{
			jsonString = (JsonString) jsonPointer2.getValue(jsonStructure);
			System.out.println(jsonString.getString());
			}
		catch(Exception e) {System.out.println("Exception: "+e);}
		//get value if its an object
		try {
			jsonObject = (JsonObject) jsonPointer2.getValue(jsonStructure);
			System.out.println(jsonObject.toString()+"\n");
			System.out.println(jsonObject.values().toString());
		}
		catch (Exception e) {
			System.out.println("Exception: "+e);
		}
		//get value if its an array
		try {
			jsonArray = (JsonArray) jsonPointer2.getValue(jsonStructure);
			System.out.println(jsonArray.toString()+"\n");
			//System.out.println(jsonArray.get(0).toString());
		}
		catch (Exception e) {
			System.out.println("Exception: "+e);
		}*/
		
		System.out.println(jsonArray.get(0));
		ArrayList <JsonObject> objects = new ArrayList <JsonObject>();
		
		//get all information
		JsonPointer jsonPointer3 = Json.createPointer("");
		jsonObject = (JsonObject) jsonPointer3.getValue(jsonStructure);
		//System.out.println(jsonObject.toString());
		
		//JSONParser parser=new JSONParser();
		
		//try (FileReader reader= new FileReader("C:\\Springboot-Repository\\ONTAPI\\src\\main\\resources\\mined_data.json")){
		    // create a reader
		    //Reader reader = Files.newBufferedReader(Paths.get("C:\\Springboot-Repository\\ONTAPI\\src\\main\\resources\\mined_data.json"));
	
		    // create parser
		   // JsonObject jsonObj= parser.parse(reader);
		   // Object obj= reader;//jsonParser.parse(reader);//Jsoner.deserialize(reader);
		    //JsonObject parser = (JsonObject) parsed;
		   // JsonArray repolist = (JsonArray) obj;
		   // System.out.println(repolist);
		    
		    
	
		    // read customer details
		   /*
		    BigDecimal id = (BigDecimal) parser.get("id");
		    String name = (String) parser.get("name");
		    String email = (String) parser.get("email");
		    BigDecimal age = (BigDecimal) parser.get("age");
	
		    System.out.println(id);
		    System.out.println(name);
		    System.out.println(email);
		    System.out.println(age);
			*/
	
		    // read address
		    //Map<Object, Object> address = (Map<Object, Object>) parser.get("address");
		    //address.forEach((key, value) -> System.out.println(key + ": " + value));
	
		    // read payment method
		    //JsonArray paymentMethods = (JsonArray) parser.get("paymentMethods");
		    //paymentMethods.forEach(System.out::println);
	
		    // read projects
		   /*
		    JsonArray projects = (JsonArray) parser.get("Repositoriy");
		    projects.forEach(entry -> {
		        JsonObject project = (JsonObject) entry;
		        System.out.println(project.get("project"));
		        System.out.println(project.get("platform"));
		        System.out.println(project.get("repo_url"));
		        System.out.println(project.get("last_mined"));
		    });*/
	
		    //close reader
		  //  reader.close();
	
		//} catch (Exception ex) {
		 //   ex.printStackTrace();
	//}
	}
}