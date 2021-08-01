package query;

public class Queries {

	private String prefixes="PREFIX : <https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_Instances.owl#>\r\n" + 
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
			"PREFIX xml: <http://www.w3.org/XML/1998/namespace>\r\n" + 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
			"PREFIX oshpd: <https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_OSHPDP_schema.owl#>\r\n" + 
			"PREFIX skills: <https://github.com/konierik/Skillmatching/raw/main/Skillmatching/data/on_skills.owl#>\r\n";
	
	public String UserInterest() {
		String query="SELECT ?User ?something\r\n" + 
				"WHERE {?User oshpd:interested_in ?something.}\r\n";
		String out= prefixes+query;
		return out;
	}
	
	public String UserSkillInterest() {
		String query="SELECT ?User ?Skill_Entity\r\n" + 
				"WHERE {?User oshpd:interested_in ?Skill_Entity.\r\n" + 
				"?Skill_Entity a skills:Skill_Entity.}\r\n";
		String out= prefixes+query;
		return out;
	}
	
	public String UserSkillInterest(String id) {
		String query="SELECT ?User ?Skill_Entity ?Project\r\n"+
				"WHERE{?User a oshpd:User; oshpd:User_id \""+id+"\"; skills:skill_action ?Skill_Entity.\r\n"+ 
				"?Skill_Entity oshpd:tags ?Project. ?Project a oshpd:Project.}"; 
		String out= prefixes+query;
		return out;
	}
}
