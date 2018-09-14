# Sample application

Technologies:
- JDK 8
- Java EE 8
- Payara5
- docker
- docker-compose

The `./Dockerfile` creates an image with Payara5 installed and configured with postgresql driver, connection pool and datasource.
Username and password is hardcoded for simplicity.

`docker-compose` should be used to run the example. The app needs talk to the PostgreSQL over network
and docker-compose starts both the demoapp and PostgreSQL in the same network. 
 
Build: `mvn package`
Run: `docker-compose up`
Cleanup: `docker-compose down`