package reader;

import java.io.IOException;
import java.util.ArrayList;

public class Testmain {
	public static void main (String[] args) throws IOException {
		JSONReader readie = new JSONReader();
		String input="/data/projects/result/edges/~/node/contributors/edges/~/node/id";
		readie.openDoc("C://Springboot-Repository//Skillmatch//Skillmatching//src//main//resources//inputdata//sampledata_projects.json");
		ArrayList<ArrayList<String>> jsondata=readie.parsePointer(input);
		System.out.println(jsondata.toString());
		for (int i=0;i<jsondata.get(0).size();i++) {
			System.out.println("Key: "+jsondata.get(0).get(i));
			System.out.println("Value: "+jsondata.get(1).get(i)+"\n\n");
			
		}
				
		
	}
}
