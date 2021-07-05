package reader;

import java.io.BufferedReader;
import java.io.FileReader;

//another source: https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/
//https://stackabuse.com/reading-and-writing-json-in-java/

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//https://www.baeldung.com/json-pointer
import javax.json.Json;
import javax.json.JsonArray;
//import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;



public class JSONReader {
	
	
	private JsonReader reader;
	private JsonStructure jsonStructure;
	private String file;


	
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
	 * @return The return type is a twofold ArrayList including strings. 
	 * <br>Format: |pointer to the key|value of the pointer|<br>
	 * Although the method is recursive, the return type is designed for a pointer with two array markers max.
	 * In the case of more markers the return type is not trivial and difficult to read. For this it would be best to add Arrays (outPartThree,..., etc.)*/
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
			try {
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
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		//write into the out array if there was a value for the searched key: (if outPartTwo>0 then there also was a value for outPartOne)
		if(outPartTwo.size()>0) {
			
			out.add(outPartOne);
			out.add(outPartTwo);
		}
		return out;
	}
	
	/**This method is used to replace the pointers of parsePointer() method, which gives the result:<br>
	 * [[property pointer],[pointer value]]<br>
	 * The property pointer has the location of the respective value in an array. 
	 * This we need to find the domain of the property. So all location information (basically the array indexes) from the property pointer are taken.
	 * Then the pointer for the domain will be enriched with the infomration, to get the respective value for the domain.
	 * @param inarray Array result from the parsePointer() method.
	 * @param ident Pointer of the domain concept, mostly a classmapping*/
	public ArrayList<ArrayList<String>> replaceToIdent(ArrayList<ArrayList<String>> inarray, String ident){
		ArrayList<ArrayList<String>> outarray=new ArrayList<ArrayList<String>>();
		ArrayList<String>cache=new ArrayList<String>();
		String replace="";
		//running through the input array
		for (int i =0; i<inarray.get(0).size(); i++){
			//get the string for the pointer of the property at i
			String arrayval=inarray.get(0).get(i);
			// replacing the array marker in the ident string with the actual array indices from the property pointer in arrayval. 
			//So the relating ident pointer to the property pointer is created.
			replace=replaceMarker(ident,arrayval);//ident.replaceAll("~", arrayval.replaceAll("\\D+",""));
			
			//replacing the array pointer with the domain value. For this the value from the pointer is read
			inarray.get(0).get(i).replaceAll(arrayval, getPointerValue(replace));
			cache.add(getPointerValue(replace)); 
		}
		//adding new information to the output array
		outarray.add(0,cache);
		outarray.add(1,inarray.get(1));
		return outarray;
	}
	
	/*public ArrayList<ArrayList<String>> replacePointer(ArrayList<ArrayList<String>> inarray, String ident, int replaceindex, int identindex){
		ArrayList<ArrayList<String>> outarray=new ArrayList<ArrayList<String>>();
		ArrayList<String>cache=new ArrayList<String>();
		String replace="";
		System.out.println("array.size(): "+inarray.size());
		System.out.println("array.get(0).size(): "+inarray.get(0).size());
		for (int i =0; i<inarray.get(0).size(); i++){
			//get the string for the pointer of the array index
			String arrayval=inarray.get(0).get(i);
			// replacing the array marker in the ident pointer string with the actual array indices from the pointer in arrayval. 
			//So the relating ident pointer to the property pointer is created.
			replace=replaceMarker(ident,arrayval);//ident.replaceAll("~", arrayval.replaceAll("\\D+",""));
			
			//replacing the array pointer with the actual value. For this the value from the pointer is read
			inarray.get(0).get(i).replaceAll(arrayval, getPointerValue(replace));
			cache.add(getPointerValue(replace)); 
		}
		
		//adding new information to the output array.
		outarray.add(0,cache);
		outarray.add(1,inarray.get(1));
		return outarray;
	}*/
	
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
	public JsonArray getArrayFromPointer(String pointer) throws IOException {
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
			jsonPointer=Json.createPointer(pointer.substring(0,pointer.indexOf("~")));
			if (jsonPointer.containsValue(jsonStructure)) {
				jsonArray=jsonPointer.getValue(jsonStructure).asJsonArray();
			}
			System.out.println("Pointer not in Json file. \n");
		}
		
		return jsonArray;
	}
	public JsonValue getValueFromPointer(String pointer) {
		JsonPointer jsonPointer= Json.createPointer(pointer);
		JsonValue jsonVal=null;
		if(jsonPointer.containsValue(jsonStructure)) {
			jsonVal=jsonPointer.getValue(jsonStructure);
		}
		return jsonVal;
	}
	
	
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
	
	public JsonStructure getStructure() {
		return jsonStructure;
	}
	
}