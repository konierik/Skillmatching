package run;

import process.Randomize;
import reader.JSONReader;

public class AnonymizeID {
	
	public static void main(String[] args) {
		//Create a Randomize instance to randomize the user id property
		Randomize user= new Randomize();

		//create JSONReader for the files that should be anonymized.
		JSONReader whoami_projects =new JSONReader();
		JSONReader whoami_issues=new JSONReader();
		JSONReader whoami_users =new JSONReader();
		
		
	}
}
