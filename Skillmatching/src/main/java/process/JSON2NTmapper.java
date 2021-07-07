package process;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsoup.Jsoup;

import reader.JSONReader;

public class JSON2NTmapper {
	
	private OntoModeler mapping= new OntoModeler();
	private String objectpropertymapping;
	private String classmapping;
	private String datapropertymapping;
	
	private String NToutputFile; //Format should be: "c:\\projects\\app.log"
	private String NTcontent="";;
	private String instanceIRI;
	private String mappingIRI;
	private Writer out;
	
	public void instantiateToNTClasses(ArrayList<ArrayList<String>> annotations, JSONReader reader) throws IOException {
		for (int i=0; i<annotations.get(0).size();i++) {
			//reader.setFile("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");//setting input filelocation
			//reader.open(); //open reader 
			//needed variables: class-iri to instantiate, class pointer to get the class objects from json file, ident pointer to get the ident-property out of the json objects
			String individualpointer=annotations.get(0).get(i);
			String rdfsType = annotations.get(1).get(i);
			String classIRI= annotations.get(2).get(i);
			try{
				//getting the identifier values from all instances of the class i
				ArrayList<ArrayList<String>> jsonResult = reader.parsePointer(individualpointer);
				//running through all found instances of class i
				if(!jsonResult.isEmpty()&&jsonResult.size()!=0&&jsonResult!=null) {
					for (int j=0; j<jsonResult.get(0).size(); j++) {
						//getting jth-instance value
						String individual=jsonResult.get(1).get(j);
						//instantiate the jth- value as an individual of class i
						addNTStatement(instanceIRI+"#"+individual,rdfsType,classIRI);
						//if the instance is from the skill ontology, it will be set as the same individual as the one in the skill ontology. so it will be accordingly hierarchized.
						if(classIRI.contentEquals("https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl#Skill_Entity")||classIRI.contentEquals("https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_OSHPDP_schema.owl#Tag")) {
							addNTStatement(instanceIRI+"#"+individual,"http://www.w3.org/2002/07/owl#sameAs","https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl#"+individual);
						}
					}
				}else {
					System.out.println("Array for "+annotations.get(0).get(i)+" is empty.");
				}
			
			}catch(Exception e) {
				e.printStackTrace();
				}
			//reader.close(); //closing reader: opening-closing is necessary since there requests per reader are limited
		}
	}
	
	public void instantiateToNTObjectproperties(ArrayList<ArrayList<String>> annotations, JSONReader reader) {
		for (int i=0; i<annotations.get(0).size();i++) {
			//reader.setFile("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");//setting input filelocation
			//reader.open(); //open reader 
			//needed variables: class-iri to instantiate, class pointer to get the class objects from json file, ident pointer to get the ident-property out of the json objects
			String range=annotations.get(2).get(i);
			String objectproperty = annotations.get(1).get(i);
			String domain= annotations.get(0).get(i);
			try{
				//getting the identifier values from all instances of the class i
				ArrayList<ArrayList<String>> jsonResult = reader.parsePointer(range);
				if(!jsonResult.isEmpty()&&jsonResult.size()!=0&&jsonResult!=null) {
					//replacing the value pointers in jsonResult with pointers of the domain concept for the dataproperty
					//System.out.println("replacing"+i+"-th pointer: "+range+" for property "+objectproperty);
					ArrayList<ArrayList<String>> replacedResults=reader.replaceToIdent(jsonResult,domain);
					//running through all found values j of the dataproperty i 
					for (int j=0; j<replacedResults.get(0).size(); j++) {
						//getting jth-instance value
						String subject=instanceIRI+"#"+replacedResults.get(0).get(j);
						//the object of an object property is instanitated as string value
						String object=instanceIRI+"#"+replacedResults.get(1).get(j);
						//instantiate the jth- value as an individual of class i
						addNTStatement(subject,objectproperty,object);

					}
				}else {
					System.out.println("Array for "+annotations.get(0).get(i)+" is empty.");
				}
			}catch(Exception e) {
				e.printStackTrace();
				}
			//reader.close(); //closing reader: opening-closing is necessary since there requests per reader are limited
		}
	}
	
	public void instantiateToNTDataproperties(ArrayList<ArrayList<String>> annotations, JSONReader reader) {
		for (int i=0; i<annotations.get(0).size();i++) {
			//reader.setFile("https://github.com/konierik/O-N/raw/master/ontology/Family_input.json");//setting input filelocation
			//reader.open(); //open reader 
			//needed variables: class-iri to instantiate, class pointer to get the class objects from json file, ident pointer to get the ident-property out of the json objects
			String range=annotations.get(2).get(i);
			String dataproperty = annotations.get(1).get(i);
			String domain= annotations.get(0).get(i);
			try{
				//getting the identifier values from all instances of the class i
				ArrayList<ArrayList<String>> jsonResult = reader.parsePointer(range);
				if(!jsonResult.isEmpty()&&jsonResult.size()!=0&&jsonResult!=null&&!range.isEmpty()&&range.length()!=0&&range!=null){
					//replacing the value pointers in jsonResult with pointers of the domain concept for the dataproperty
					ArrayList<ArrayList<String>> replacedResults=reader.replaceToIdent(jsonResult, domain);
					//running through all found values j of the dataproperty i 
					for (int j=0; j<replacedResults.get(0).size(); j++) {
						//getting jth-instance value
						String subject=instanceIRI+"#"+replacedResults.get(0).get(j);
						//the object of a data property is instanitated as string value
						String object="\""+replaceIllegalChar(replacedResults.get(1).get(j))+"\"";
						
						//instantiate the jth- value as an individual of class i
						addDatapropertyNTStatement(subject,dataproperty,object);
					}
				}else {
					System.out.println("Array for "+annotations.get(0).get(i)+" is empty.");
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				}
			//reader.close(); //closing reader: opening-closing is necessary since there requests per reader are limited
		}
	}
	/**This method removes characters from a string that are not allowed in nt format, e.g. the escape "\" */	
	public String replaceIllegalChar(String in) {
		String neu="";
		
		try {
			//remove html statements
			neu=Jsoup.parse(in).text();
		} catch(Exception e) {
			e.printStackTrace();
		}
		neu=neu.replace("\\", "");
		neu=neu.replace("\"","");
		return neu;

	}
	
	public void addNTStatement(String subject, String predicate, String object) {
		NTcontent+="<"+subject+"> <"+predicate+"> <"+object+">.\n";
		//System.out.println("Added NT statement: <"+subject+"> <"+predicate+"> <"+object+">.\n");
	}
	
	public void addDatapropertyNTStatement(String subject, String predicate, String object) {
		NTcontent+="<"+subject+"> <"+predicate+"> "+object+".\n";
		//System.out.println("Added NT statement: <"+subject+"> <"+predicate+"> "+object+".\n");
	}
	
	public void toNTFile() throws UnsupportedEncodingException, FileNotFoundException {
		out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NToutputFile),"UTF-8"));
		try {
			//Files.write(Paths.get(NToutputFile), NTcontent.getBytes(),StandardCharsets.UTF_8);
			out.write(NTcontent);
		} catch(IOException e) {
			System.out.println("Not successful writing.");
			e.printStackTrace();
		}finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			setter
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setNToutputLocation(String out) {
		NToutputFile=out;
	}
	
	public void setInstanceIRI(String iri) {
		instanceIRI=iri;
	}
	public void setMappingIRI(String iri) {
		mappingIRI=iri;
	}
	
	public void setObjectPropertyMapping(String opm) {
		objectpropertymapping= opm;
		mapping.setObjectpropertymapping(opm);
	}
	
	public void setDataPropertyMapping(String dpm) {
		datapropertymapping= dpm;
		mapping.setDatapropertymapping(dpm);
	}
	
	public void setClassMapping(String cm) {
		classmapping= cm;
		mapping.setClassmapping(cm);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//			getter
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getObjectPropertyMapping() {
		return objectpropertymapping;
	}
	
	public String getDataPropertyMapping() {
		return datapropertymapping;
	}
	
	public String getClassMapping() {
		return classmapping;
	}

	public String getNToutputLocation() {
		return NToutputFile;
	}
	
}

