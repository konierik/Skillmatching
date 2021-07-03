package reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Testmain {
	public static void main (String[] args) throws IOException {
		JSONReader readie = new JSONReader();
		String input="/data/projects/result/edges/~/node/contributors/edges/~/node/id";
		String ident="/data/projects/result/edges/~/node/id";
		String output="/data/projects/result/edges/25/node/contributors/edges/~/node/id";
		readie.setFile("C://Springboot-Repository//Skillmatch//Skillmatching//src//main//resources//inputdata//sampledata_projects.json");
		readie.open();
		ArrayList<ArrayList<String>> jsondata=readie.parsePointer(input);
		System.out.println(jsondata.toString());
		System.out.println(jsondata.size());
		System.out.println(jsondata.get(0).size());
		for (int i=0;i<jsondata.get(0).size();i++) {
			
				System.out.println("Key: "+jsondata.get(0).get(i));
				System.out.println("Value: "+jsondata.get(1).get(i)+"\n\n");
			
		}
		
		ArrayList<ArrayList<String>> jsondatadomain=readie.replaceToIdent(jsondata, ident);
		System.out.println(jsondatadomain.toString());
		for (int i=0;i<jsondatadomain.get(0).size();i++) {
			
			System.out.println("Key: "+jsondatadomain.get(0).get(i));
			System.out.println("Value: "+jsondatadomain.get(1).get(i)+"\n\n");
		
	}
		
		
		/*String clean = output.replaceAll("\\D+",""); //remove non-digits
		int indexofclean=output.indexOf(clean);
		
		String replace=ident.replaceAll("~", output.replaceAll("\\D+",""));
		String begin=output.substring(0,output.indexOf(clean)+clean.length());
		String end=ident.substring(ident.indexOf("~")+1);*/
		//System.out.println(compare(ident,output));
	}
	public static String compare(String toChange, String compare) {
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
}
