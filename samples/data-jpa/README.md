Spring Boot project with Spring Data JPA.

- `mvn clean install` => ok
- `cd api && mvn spring-boot:run` => ok
- `mvn spring-boot:build-image -Pnative-image` => KO
- `mvn spring-boot:build-image -Pnative-image -DskipTests` => ok
- `docker run --rm -p 8080:8080  -t data-jpa:0.0.1-SNAPSHOT` => KO
