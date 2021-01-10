# LOCUS : Maps Path Point Finder

### How to Run the application

After unzipping the file open the terminal and got to the project: 

```sh
$ cd locus-assignment
$ mvn clean install
$ ./mvnw spring-boot:run
```
This will boot up the web application on port 8080. After this just do a curl GET request with source and destination from different tab of terminal

```sh
$ curl -X GET "http://localhost:8080/api/maps/path/points?destLang=77.66085&destLat=12.95944&sourceLang=77.61896&sourceLat=12.94523" -H "accept: */*"
```
Make sure you replace correct lat lang values for source and destination
It returns the list of all the intermediate points spaced at 50m from each other on the path that connects them.

### Alternatively
visit ~http://localhost:8080/swagger-ui.html~ once you start the application and use the api via ***swagger***
