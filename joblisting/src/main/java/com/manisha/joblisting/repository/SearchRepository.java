package com.manisha.joblisting.repository;

import java.util.List;

import com.manisha.joblisting.model.JobPost;

public interface SearchRepository {
	
	//method to find a specific text in all the JobPosts present in the DB and return the ones which has this specific text
	List<JobPost> findByText(String text);
		
}
