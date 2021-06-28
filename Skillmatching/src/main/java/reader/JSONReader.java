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
import java.util.Arrays;
import java.util.List;

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
	private JsonStructure jsonStructure;
	private String file;
	/*public static void main (String[] args) throws URISyntaxException, IOException {
		//read();
		JSONReader red= new JSONReader();
		red.setFile("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");
		red.open();
		String pointPerson= "/data/Person";
		String pointID="/data/Person/ID";
		JsonArray person=red.instantiate(red.getReader(),"/data/Person");
		for (int i=0;i<person.size();i++) {
		System.out.println("getID: "+person.getValue("/"+i+schort(pointPerson,pointID)));
		}
		
		
		close(reader);
	}*/
	
	public String schort(String pointer, String identifier) {
		return identifier.replaceAll(pointer,"");
		
	}
	//open a json reader from URL json file location
	public void open() throws IOException {
		try {
			URL fileURL= new URL(file);
			BufferedReader read= new BufferedReader(new InputStreamReader(fileURL.openStream()));
			reader = Json.createReader(read);//new FileReader(file))
			jsonStructure=reader.read();
		} catch(Exception e) {
			System.out.println("Open reader from URL not successful. Try reading as local file....");
			e.printStackTrace();
		}
		try {
			reader = Json.createReader(new FileReader(file));//new FileReader(file))
			jsonStructure=reader.read();
		}catch(Exception ex) {
			System.out.println("Open reader from local file was not successful either.");
			ex.printStackTrace();
		}
	}
	/*old method: open json reader from local json file location
	public void openDoc() throws IOException {
		reader = Json.createReader(new FileReader(file));//new FileReader(file))
		jsonStructure=reader.read();
		//return reader;
	}*/
	
	/**This method closes the reader after getting all the information*/
	public void close() {
		reader.close();
	}
	
	
	/**A method to parse a pointer with array markers ("~").
	 * The mapping annoations from the ontology file are json pointers to where the information stands a relating json formatted file.
	 * <p>Arrays from the json file are represented by a .../~/... in the annotation. This function recursively runs through 
	 * all existing arrays that have the requested information, where every "~" is replaced by an index counter until the information is received.
	 * @param input A string of the pointer where to get the information from. 
	 * The pointer does not necessary have to include array markers (~), but due to the complex return type
	 * it is recommended to use another function for getting a simple json value. 
	 * @return The return type is a twofold ArrayList including strings.  Format: |pointer to array of first array marker|value of|
	 * Although the method is recursive, the return type is designed for a pointer with two array markers max.
	 * In the case of more markers the return type is not trivial and difficult to read.*/
	public ArrayList<ArrayList<String>> parsePointer(String input){
		//JsonStructure jsonStructure=null;// = reader.read();
		JsonPointer jsonPointer = null;// = Json.createPointer(pointer);
		//create Array for json reading and data cache
		JsonArray data=null;
		//creating lists for output
		//list out will be returned in the end
		ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
		/* This lists (outPartOne and outPartTwo) hold the domain and range data for the second array information. 
		 * The list outPartOne has the domain, which is a subject representative in a semantic triple of the form <subject predicate object>
		 * and the outPartTwo has the range, representing the object of such triples.
		 * The pointer represents the predicate. Usually the domain in a pointer is the field of the first array marker and the range is the second array marker.
		 * A third array marker would mean, that the range also has its own predicate. The structure of the method allows that but that is difficult if the result
		 * is used for one of the instaniation methods of the project, which requires and instantiates in general just one predicate (salso called property).
		 */
		ArrayList<String> outPartOne = new ArrayList<String>();
		ArrayList<String> outPartTwo = new ArrayList<String>();//
		//count the '~' char, that marks an array in the pointer
		int count= (int) input.chars().filter(cha->cha=='~').count();	
		//if there was no array marker found, then the pointer gets to a value
		if (count<1) {
			jsonPointer=Json.createPointer(input);
			//check if the pointer exists: the domain and range (here without "") is added to the relating arrays
			if(jsonPointer.containsValue(jsonStructure)) {
					outPartOne.add(input);
					outPartTwo.add(jsonPointer.getValue(jsonStructure).toString().replace("\"", ""));
			}else {
				System.out.println("JsonPointer: "+input+"\nFound status: "+jsonPointer.containsValue(jsonStructure));
			}
			//closing the reader
			//reader.close();
		}else {
			//getting the array of the first array marker '~' (count from the left) in the pointer
			String newArray= input.substring(0, input.indexOf("~")-1);
			//jsonStructure=reader.read();
			jsonPointer=Json.createPointer(newArray);
			data=jsonPointer.getValue(jsonStructure).asJsonArray();
			//close the reader since the recursive function will open it again
			//close(reader);
			if(data.size()>0) {
				//running through the array:
				for (int i=0; i<data.size();i++) {
					//recursive funtion: add the arraylist<arraylist<string>> that is created from the next array marker '~'
					ArrayList<ArrayList<String>> in=parsePointer(input.replaceFirst("~", ""+i+""));
					if(in.size()>0) {
						if(out.size()>0) {
							out.get(0).addAll(in.get(0));
							out.get(1).addAll(in.get(1));
						}else {
							out.addAll(in);
						}
					}
				}
			}	
		}
		//write into the out array if there was a value for the searched key: (if outPartTwo>0 then there also was a value for outPartOne)
		if(outPartTwo.size()>0) {
			
			out.add(outPartOne);
			out.add(outPartTwo);
		}
		return out;
	}
	
	public ArrayList<ArrayList<String>> replaceToDomain(ArrayList<ArrayList<String>> inarray, String ident){
		ArrayList<ArrayList<String>> outarray=new ArrayList<ArrayList<String>>();
		ArrayList<String>cache=new ArrayList<String>();
		String replace="";
		System.out.println("array.size(): "+inarray.size());
		System.out.println("array.get(0).size(): "+inarray.get(0).size());
		for (int i =0; i<inarray.get(0).size(); i++){
			//get the string for the pointer of the array index
			String arrayval=inarray.get(0).get(i);
			// replacing the array marker in the ident string with the actual array number from the pointer in arrayval. 
			//For this the pointer is cropped to just the integer in it. (arrayval.replaceAll("\\D+","") removes all chars that are not one of 0123456789)
			replace=replaceMarker(ident,arrayval);//ident.replaceAll("~", arrayval.replaceAll("\\D+",""));
			//replacing the array pointer with the domain value. For this the value from the pointer is read
			inarray.get(0).get(i).replaceAll(arrayval, getPointerValue(replace));
			cache.add(getPointerValue(replace)); 
		}
		
		outarray.add(0,cache);
		outarray.add(1,inarray.get(1));
		return outarray;
	}
	
	public String replaceMarker(String toChange, String compare) {
		String out="";
		List<String> changearray=Arrays.asList(toChange.split("/"));
		List<String> comparearray=Arrays.asList(compare.split("/"));
		for(int i =1; i<changearray.size(); i++) {
			if (changearray.get(i).equals("~")) {
				out+="/"+comparearray.get(i);
				//changearray.set(i,comparearray.get(i));
			}else {
				out+="/"+changearray.get(i);
			}
		}
		return out;
	}
	
	public String getPointerValue(String point) {
		JsonPointer jsonPointer=Json.createPointer(point);
		String out=jsonPointer.getValue(jsonStructure).toString().replace("\"", "");
		return out;
	}
	
	//gets an JsonArray for a respective input pointer string
	public JsonArray getArrayFromPointer(JsonReader reader, String pointer) throws IOException {
		//reader=open("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");
		//JsonStructure jsonStructure = reader.read();
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
		//JsonStructure jsonStructure = reader.read();
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
			arr = getArrayFromPointer(reader,classpointer);
		} catch (IOException e1) {
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
	/*
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
	*/
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//						Setter
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setFile(String filo) {
		file=filo;
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//						Getter
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getFileString() {
		return file;
	}
	public JsonReader getReader() {
		return reader;
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*public void read() throws URISyntaxException, IOException {
		
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
		/*
		System.out.println(jsonArray.get(0));
		ArrayList <JsonObject> objects = new ArrayList <JsonObject>();
		
		//get all information
		JsonPointer jsonPointer3 = Json.createPointer("");
		jsonObject = (JsonObject) jsonPointer3.getValue(jsonStructure);*/
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
	//}
}