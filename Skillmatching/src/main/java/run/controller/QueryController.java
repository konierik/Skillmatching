package run.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import query.Queries;
import query.QueryExec;


@RestController
public class QueryController {
	private Queries querystrings = new Queries();
	private QueryExec qEx = new QueryExec();
	
	@GetMapping(value="/UserSkillInterestQuery/{id}")
	public LinkedList<ArrayList<String>> UserSkillInterestQuery(@PathVariable String id) {
		return qEx.execQuery(querystrings.UserSkillInterest(id));
	}
	
	@GetMapping(value="/test/{id}")
	public String test(@PathVariable String id) {
		String res="{\"id\":"+id+",\"content\":\"Hello, World!\"}";
		return res;
	}

}
