
1) cd to the TinyURL dir, build the code 
	$ ./gradlew clean build
2)  docker-compose build 

3)  docker-compose up

4) POST request  "localhost:8080/tiny" with body  
		{
		"url":"google3.com"
		}
response wil be like http:localhost:8080/bH

5) To fetch the original URL, GET "http:localhost:8080/bH", should see redirect to google3.com.

6) To get the number of times the tinyURL is used GET 'http://localhost:8080/stats/bH'
   response  will be " Count of requests for this URL is : 9"

7) To get total number of urls GET 'http://localhost:8080/stats/ 
"Count of tiny urls is :<total>"
