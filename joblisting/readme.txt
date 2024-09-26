Java Spring Boot Mongodb Full Project:  https://www.youtube.com/watch?v=kYiLzIiHVY8
We will build a web project where Employer can hire and Job Seekers can get hired for free. 
For Backend we will use Spring boot framework and Mongodb as database. Frontend is done using react.

We will also implement the search feature using the Mongodb atlas.

Login to Mongodb atlas
We are using cloud MongoDB. So 
Step1: Create a cluster with AWS as service. Copy 73.170.32.165
It will provide default admin credentials. I rewrote it to: root/ admin123
Connection string: mongodb+srv://root:admin123@cluster-job-portal.z4tsu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster-Job-Portal

Step2: Create a database
Click browse collections -> create database -> enter details (Database Name: Job-Portal, Collection name: JobPost -> Create

Step3: Click Insert Document and copy pase the json data.

############################################################

Now go to start.spring.io and create a maven project with java language and following dependencies:
Spring Data MongoDB and Spring web
Generate and unzip this project

Open Eclipse and import this project.

################################################################

Open application.properties and add following to establish connection with MongoDB:
Step1. spring.data.mongodb.uri=

To get this URI open mongodb atlas and go to 
Database -> cluster ->click on cluster-job-portal -> click on tab Overview -> click connect -> connect to your application
-> Select your driver and version -> 
Add your connection string into your application code (copy the uri) :
mongodb+srv://root:<db_password>@cluster-job-portal.z4tsu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster-Job-Portal

click done.

Now add this uri after replacing <db_password> in the uri with the actual password.

Step2. Next add 
spring.data.mongodb.database=Job-Portal

####################################################################

As we are using spring MVC, we will create JobPostController class

And to create data/POJO we will create JobPost class.

To fetch data from the MongoDB, we will create JobPostRepo Interface which extends MongoRepository<Data Type, Primary Key type>
This MongoRepository will help take care of all the CRUD operations.

Also make post aware that it's a document for which collection in Mongodb by adding
@Document(collection="JobPost")

#######################################################################

Implement /jobposts and /jobpost REST endpoints
To test post method use following data:
{
        "profile": "Blockchain Developer",
        "desc": "Ethereum developer with dapps experience ",
        "exp": 5,
        "techs": [
            "Solidity",
            "xml",
            "microservices"
        ]
    }

#########################################################################

Now we will explore two important features of MongoDB:
1. MongoDB Compass 2. MongoDB Atlas Search.

It will search the data whether it's in keys or in the values of the documents. It will search every single text.

MongoDB Compass - is the GUI for MongoDB. We can use it connect the MongoDB server on our local system or on the cloud. 

Download and install MongoDB compass: https://www.mongodb.com/products/tools/compass
After installation, when we open the compass, we need to configure following things:

Click Add new connection ->  enter the cloud uri as we have our setup in the cloud and not on the local
mongodb+srv://root:admin123@cluster-job-portal.z4tsu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster-Job-Portal
click connect.

Note: By Default, MongoDB creates 2 replicas of a DB.

We can use either compass or the cloud to do data manipulation.

##########################################################################

Now let's implement search feature using search option of Atlas search. 

Step 1:
************************************
If we do regular search for million of records, it takes minutes to get the search result. So to speed this up we
have something called indexing.

We will be creating search indexes. 
Go to cloud MongoDB or MongoDB compass:

Open the collection JobPortal -> open tab "Atlas search" ->click create search index
->visual editor (as it's easy) ->next ->index name (default) -> Select the Collection from the database (JobPortal) -> Next
-> click create search index

Note: Index Fields:[dynamic]
By default it will index everything in our DB but in case DB is huge then this indexing will take lot of time and memory.
 
We can make this Index Fields as Static as well where we can tell that we only want to index 2-3 fields in document. E.g We can only index desc and profile of JobPost.

Step 2:
**************************************
Now let's search "Java", sort the results, and limit the number of results to 5.

To do this we will go to Aggregation tab of the collection "JobPortal"
By default it will show all the results as there are no filters applied.

To apply filters, go to stage area and add operators.
First operator, we will chose is $search

click add stage -> select operator $search -> 

{
  index: 'default',
  text: {
    query: 'Java',
    path: 'desc'
  }
}

if we want to provide multiple paths:
{
  index: 'default',
  text: {
    query: 'Java',
    path: ['profile','desc', 'techs']
  }
}

Now we want to sort w.r.t the number of years of experience (exp field of JobPost)
Add one more stage. Basically we are creating a pipeline (first search, now sorting)
click Add stage -> select $sort operator

/**
 * Provide any number of field/order pairs.
 */
{
  exp: 1
}

1 here means it will sort in ascending order. and -1 is for descending


Now we want to show only 5 records.
Click add stage -> select $limit operator
/**
 * Provide the number of documents to limit.
 */
5

This will show only 5 records

This completed the pipeline

#############################################################

MongoDB has one more important feature called Client side Field encryption

Let's say we have an application where client submits some data and some of the data are sensitive information.
like ssn, email, phone etc. The moment client submits this data, we can field encrypt them where we can specify which field we want to encrypt.
Because in the backend mongodb is also using some cloud service and we don't want to compromise our data.

The moment we send aour data from server to DB it will get encrypted. we save this encrypted data in the DB. But what do we get when we want to 
fetch this data. When we fetch this data from the DB, it will come in encrypted form only but the moment we receive it on the server where we have
the Driver, it will get decrypted.

##################################################################

Now lets implement search feature using java code.
add searchJobPosts() method in the controller class.

But we donot have any existing method in MongoRepository to search by text.

For that create an interface SearchRepository. Here We will create a custom method for that findByText(String text)

Now create a java class - SearchRepositoryImpl which implements this method . Keep this method empty.
@Component
public class SearchRepositoryImpl

This @Component will be used because we will declare an object of SearchRepository which in turn will get instantiated with the help of SearchRepositoryImpl class. 

Add this in controller class
@Autowired
SearchRepository srepo;


Now go to MongoDB Atlas and stay on the page where we created the pipeline for search, sort, and limit. There
click on "EXPORT TO LANGUAGE"

Select Java -> select "Include Import Statements" and "Include Driver Syntax" -> Copy the exported pipeline java code

*************************************************
import java.util.Arrays;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import com.mongodb.client.AggregateIterable;

/*
 * Requires the MongoDB Java Driver.
 * https://mongodb.github.io/mongo-java-driver
 */

MongoClient mongoClient = new MongoClient(
    new MongoClientURI(
        ""
    )
);
MongoDatabase database = mongoClient.getDatabase("Job-Portal");
MongoCollection<Document> collection = database.getCollection("JobPost");

AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search", 
    new Document("index", "default")
            .append("text", 
    new Document("query", "Java")
                .append("path", Arrays.asList("profile", "desc", "techs")))), 
    new Document("$sort", 
    new Document("exp", 1L)), 
    new Document("$limit", 5L)));
    
 *******************************************   
we will use some part of it in the empty method (findByText()).

We will not create MongoClient rather we will use Spring framework to create that.
@Autowired
MongoClient mongoClient;

Now paste following code in the method :
MongoDatabase database = mongoClient.getDatabase("Job-Portal");
MongoCollection<Document> collection = database.getCollection("JobPost");
AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search", 
    new Document("index", "default")
            .append("text", 
    new Document("query", "Java")
                .append("path", Arrays.asList("profile", "desc", "techs")))), 
    new Document("$sort", 
    new Document("exp", 1L)), 
    new Document("$limit", 5L)));

	




 