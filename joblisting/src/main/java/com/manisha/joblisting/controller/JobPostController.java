package com.manisha.joblisting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.manisha.joblisting.model.JobPost;
import com.manisha.joblisting.repository.JobPostRepo;
import com.manisha.joblisting.repository.SearchRepository;

@RestController
public class JobPostController {
	
	@Autowired
	JobPostRepo repo;
	
	@Autowired
	SearchRepository searchRepo;
	
	//Get list of all the jobs listed on the job portal
	@GetMapping("/jobposts")
	public List<JobPost> getAllJobPosts(){
		//now we want to fetch the job posts from mongodb
		return repo.findAll();		
	}
	
	//Search all the job posts which has the specified text
	@GetMapping("/jobposts/{searchText}")
	public List<JobPost> searchJobPosts(@PathVariable String searchText){
		return searchRepo.findByText(searchText);
	}
	
	//Add a new job post on the portal. i.e add it in the DB
	@PostMapping("/jobpost")
	public JobPost addJobPost(@RequestBody JobPost jobPost) {
		//save returns the object itself
		return repo.save(jobPost);
	}
}
