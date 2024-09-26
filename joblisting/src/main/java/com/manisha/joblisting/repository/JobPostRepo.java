package com.manisha.joblisting.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manisha.joblisting.model.JobPost;

public interface JobPostRepo extends MongoRepository<JobPost, String>{

}
