
The project is built with Maven and can be run using the following command:

```bash
mvn spring-boot:run
```

The API documentation can be found at: http://localhost:8080/swagger-ui/index.html#/

The project is a Spring Boot application that provides a currency exchange service. It includes features such as:
- RESTful web service for currency exchange
- Data persistence using Spring Data JPA

The test layer can be executed with the following command:

```bash
mvn test
```
The project uses the following technologies:
- Java 17
- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL as the database
- Swagger for API documentation
- JUnit for testing
- Mockito for mocking in tests
- Lombok for reducing boilerplate code
- MapStruct for object mapping
- Spring Boot Actuator for monitoring and management

Docker compose file is provided to run the application with a PostgreSQL database. The database configuration can be found in the `application.properties` file.
