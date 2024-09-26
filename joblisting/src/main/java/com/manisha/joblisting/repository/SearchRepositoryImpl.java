package com.manisha.joblisting.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;

import com.manisha.joblisting.model.JobPost;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Component
public class SearchRepositoryImpl implements SearchRepository{
	@Autowired
	MongoClient mongoClient;
	
	@Autowired
	MongoConverter converter;
	
	@Override
	public List<JobPost> findByText(String searchText) {
		//decalre it final as it will not change
		final List<JobPost> jobPosts = new ArrayList<>();
		
		MongoDatabase database = mongoClient.getDatabase("Job-Portal");
		MongoCollection<Document> collection = database.getCollection("JobPost");
		AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search", 
		    new Document("index", "default")
		            .append("text", 
		    new Document("query", searchText)
		                .append("path", Arrays.asList("profile", "desc", "techs")))), 
		    new Document("$sort", 
		    new Document("exp", 1L)), 
		    new Document("$limit", 5L)));
		
		//the data we are getting are in the result and we want to send JobPosts. This result is iterable, so we will iterate for each document
		//whatever document we are receiving , we will add them in the JobPost
		//the doc here is document format and the posts here is Java format. So we need Mongo converter here
		result.forEach(doc -> jobPosts.add(converter.read(JobPost.class, doc)));
		
		return jobPosts;
	}

}
