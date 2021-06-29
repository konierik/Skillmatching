package process;

import static org.mockito.Matchers.intThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Randomize {
	
	
	//ArrayList<ArrayList<String>> in format [[id][random id]]
	private ArrayList<ArrayList<String>> ids = new ArrayList<ArrayList<String>>();
	private Map<String,String> userid=new TreeMap<>();
	private TreeMap<String,String> username= new TreeMap<>();
	
	public static void main(String[] args) {
		int i=0;
		while(i<10) {
		
			System.out.println(UUID.randomUUID().toString());
			i++;
		}
	}
	public String randomizeID(String id) {
		String random="";
        //we do not want double random ids. So as long as the random id is already in the map we create new ons.
		do{
        	random=UUID.randomUUID().toString();
        }
        	while(userid.containsValue(random)); 
		userid.put(id, random);
		return random;
		
	}
	
	public String anonymizeID(String id) {
		//if there is already the id in the map with anonymized ids, the same randomized id will be returned
		if (userid.containsKey(id)) {
			return userid.get(id).toString();
		}else {
			return randomizeID(id);
		}
	}
	
	public String anonymizeName(String name) {
		if (username.containsKey(name)) {
			return userid.get(name).toString();
		}else {
			return randomizeName(name);
		}
	}
	public String randomizeName(String name) {
		String random="";
		if(username.isEmpty()) {
			username.put(name, "User1");
			random= "User1";
		}else {
			//Get the index of the last user and add 1: get lastEntriy(), remove all letters other from 0123456789
			int index =Integer.parseInt(username.lastEntry().toString().replaceAll("\\D+",""))+1;
			random="User"+index;
			username.put(name, random);
			
		}
		return random;
		
	}
	
}
