package reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import process.OntoModeler;

public class Testmain {
	public static void main (String[] args) throws IOException {
	/*
		JSONReader readie = new JSONReader();
		String input="/data/profiles/result/edges/~/node/followingProjects/edges/~/node/tags/~/name";//"/data/projects/result/edges/~/node/contributors/edges/~/node/id";
		String ident="/data/profiles/result/edges/~/node/followingProjects/edges/~/node/id";//"/data/projects/result/edges/~/node/id";
		String output="/data/projects/result/edges/25/node/contributors/edges/~/node/id";
		readie.setFile("C://Users//konierik//Nextcloud2//MA-Arbeit//03_Ontology//Skills//WIF_data//sampledata_user.json");
		readie.open();
		System.out.println(readie.getStructure().toString());
		ArrayList<ArrayList<String>> jsondata=readie.parsePointer(input);
		System.out.println(jsondata.toString());
		System.out.println(jsondata.size());
		System.out.println(jsondata.get(0).size());
		
		for (int i=0;i<jsondata.get(0).size();i++) {
			
				System.out.println("Key: "+jsondata.get(0).get(i));
				System.out.println("Value: "+jsondata.get(1).get(i)+"\n\n");
			
		}*/
		OntoModeler mod= new OntoModeler();
		mod.setDocIRI("https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl");
		mod.loadOnto();
		/*
		ArrayList<ArrayList<String>> jsondatadomain=readie.replaceToIdent(jsondata, ident);
		System.out.println(jsondatadomain.toString());
		System.out.println("Länge "+jsondatadomain.get(0).size());
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
