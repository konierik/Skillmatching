package query;
/**This class provides the query strings for the skillmatching demonstrator*/
public class Queries {
	
	/*some of the queries are oriented to validate the ontology on following competency questions:
	 CQ1.1: Does the ontology connect skills to users?
	 CQ1.2: Does the ontology connect interests to users?
	 CQ2.1: Can I find a project with a need for skills that a user can provide?
	 CQ2.2: Can I find a project with a need for skills that a user is interested in?
	 CQ3:	Can I find a person with a certain skill using the ontology?
	 */
	
	private String prefixes="PREFIX : <https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_Instances.owl#>\r\n" + 
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
			"PREFIX xml: <http://www.w3.org/XML/1998/namespace>\r\n" + 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
			"PREFIX oshpd: <https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_OSHPDP_schema.owl#>\r\n" + 
			"PREFIX skills: <https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl#>\r\n";

	///////////////////////////////////////////////////////////////
	//
	//			CQ1.1 Query
	//
	////////////////////////////////////////////////////////////////


	/**This query looks at the skills of all users*/
	public String UserSkill() {
		String query="SELECT ?User ?Skill_Entity\r\n" + 
				"WHERE { ?User skills:skill_action ?Skill_Entity.}\r\n"; 
		String out= prefixes+query;
		return out;
	}
	
	/**This query looks at the skills of a users*/
	public String UserSkill(String id) {
		String query="SELECT ?User ?Skill_Entity\r\n" + 
				"WHERE { ?Usera oshpd:User; oshpd:User_id \""+id+"\"; skills:skill_action ?Skill_Entity.}\r\n"; 
		String out= prefixes+query;
		return out;
	}
	
	///////////////////////////////////////////////////////////////
	//
	//			CQ1.2(a) Query
	//
	////////////////////////////////////////////////////////////////
	
	/**This query looks for general interests of all users; if skills or not.*/
	public String UserInterest() {
		String query="SELECT ?User ?something\r\n" + 
				"WHERE {?User oshpd:interested_in ?something.}\r\n";
		String out= prefixes+query;
		return out;
	}
	
	/**This query looks for general interests of a specific user; if skills or not.*/
	public String UserInterest(String id) {
		String query="SELECT ?User ?something\r\n" + 
				"WHERE {?User oshpd:User_id \""+id+"\" ;oshpd:interested_in ?something.}\r\n";
		String out= prefixes+query;
		return out;
	}
	
	
	///////////////////////////////////////////////////////////////
	//
	//			CQ1.2(b) Query
	//
	////////////////////////////////////////////////////////////////
	
	/**This query finds the interests of users.*/
	public String UserSkillInterest() {
		String query="SELECT ?User ?Skill_Entity\r\n" + 
				"WHERE {?User oshpd:interested_in ?Skill_Entity.\r\n" + 
				"?Skill_Entity a skills:Skill_Entity.}\r\n";
		String out= prefixes+query;
		return out;
	}
	/**This query finds the interests of a specific user.*/
	public String UserSkillInterest(String id) {
		String query="SELECT ?User ?Skill_Entity\r\n" + 
				"WHERE {?User a oshpd:User; oshpd:User_id \""+id+"\"; oshpd:interested_in ?Skill_Entity.\r\n" + 
				"?Skill_Entity a skills:Skill_Entity.}\r\n";
		String out= prefixes+query;
		return out;
	}
	

	
	///////////////////////////////////////////////////////////////
	//
	//			CQ2.1 Query
	//
	////////////////////////////////////////////////////////////////
	
	/**This query finds all users and projects with matching skills*/
	public String ProjectUserSkill() {
		String query="SELECT ?User ?Skill_Entity ?Project\r\n" + 
				"WHERE {?Skill_Entity a skills:Skill_Entity; skills:possible_action ?User; oshpd:tags ?Project.\r\n" + 
				"?Project a oshpd:Project.}\r\n";
		String out= prefixes+query;
		return out;
	}
	
	/**This query finds projects matching a users skills*/
	public String ProjectUserSkill(String id) {
		String query="SELECT ?User ?Skill_Entity ?Project\r\n" + 
				"WHERE {?User a oshpd:User; oshpd:User_id \""+id+"\";skills:skill_action ?Skill_Entity.\r\n"+
				"?Skill_Entity oshpd:tags ?Project.\r\n" + 
				"?Project a oshpd:Project.}";
		String out= prefixes+query;
		return out;
	}
	
	///////////////////////////////////////////////////////////////
	//
	//			CQ2.2 Query
	//
	////////////////////////////////////////////////////////////////
	
	/**This query finds all users and projects with skills matching the users interests*/
	public String ProjectUserInterest() {
		String query="SELECT ?User ?Skill_Entity ?Project\r\n" + 
				"WHERE {?Skill_Entity a skills:Skill_Entity; oshpd:interest_of ?User; oshpd:tags ?Project.\r\n" + 
				"?Project a oshpd:Project.}";
		String out= prefixes+query;
		return out;
	}
	
	/**This query looks which projects might fit which users based on the users interests for a specific user
	 * @param id id of user for whom projects to match are searched*/
	public String ProjectUserInterest(String id) {
		String query="SELECT ?User ?Skill_Entity ?Project\r\n" + 
				"WHERE {?User a oshpd:User; oshpd:User_id \""+id+"\";oshpd:interested_in ?Skill_Entity.\r\n"+
				"?Skill_Entity a skills:Skill_Entity; oshpd:tags ?Project.\r\n" + 
				"?Project a oshpd:Project.}";
		String out= prefixes+query;
		return out;
	}
	
	
	///////////////////////////////////////////////////////////////
	//
	//			CQ3 Query
	//
	////////////////////////////////////////////////////////////////
	
	/**This query finds users with a specific skill*/
	public String SkillUser(String skill) {
		String query="SELECT ?User ?Skill_Entity \r\n" + 
				"WHERE {?Skill_Entity skills:SkillEntity_name \""+skill+"\"; skills:possible_action ?User.}\r\n" + 
				"";
		String out= prefixes+query;
		return out;
	}

	
}
