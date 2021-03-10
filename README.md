# MovieListReader
Read a movie list from the "movielist.csv" file in classpath for processing
The objective is to return the producers with closest and farest consecutive winning of the Golden Raspberry Award

## Requirements
- JDK 8;
- Maven 3

## Running the application
To run the apllication, run the following command at the root folder:
```
mvnw spring-boot:run
```
The application will start listening to port `8080`. 
The endpoint to retirieve the data is:
```
http://localhost:8080/producers
```
### Data structure
The data will be returned in a JSON format, including the following values:
- min: a JSON array containing objects with the producers with the closest consecutive winning;
- max: a JSON array containing objects with the producers with the farest consecutive winning

The objects within the array will have the following structure:
- producer: the producer's name;
- interval: the interval in year between the awards;
- previousWin: the year when the producer won the previous award;
- followingWin: the year when the producer won the next award

## Running tests
To run the apllication tests, run the following command at the root folder:
```
mvnw test
```
