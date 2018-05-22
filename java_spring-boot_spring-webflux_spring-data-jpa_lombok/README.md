# WIP!

# Sample Java and Spring Webflux (functional style) application

Uses the postgres driver which is blocking, so not a true reactive application, but it demonstrates the functional webflux paradigm.   

Technologies:
- JDK 8
- Tomcat (blocking postgres driver so cannot use netty)
- Spring Boot
- Spring Boot Data JPA
- Spring Webflux
- Lombok
- Junit5

Build: `mvn package`
Run: `java -jar ./target/demoapp-0.0.1-SNAPSHOT.jar` or `mvn spring-boot:run`