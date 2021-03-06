# Security with Token

In this section, our goal is to build a vue + spring security framework with token which support

* token

Firstly, let's

* copy the code from `2 Security`, including `\backend` and `\frontend`

## Refact the Code

1. Add `/initialize` directory

In this directory, a series of initialization methods will be invoked before the service. Here we need to create the database table if not exists. 

```java
@Component
@Slf4j
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Create user table if not exist.");
        userMapper.createUserTable();
    }
    
}
```

2. Move Dao to Mapper. 

DAO actually a miss leading in this project. Mapper is more appropriate

3. Move data transmission classes to `model/dto`

4. Move `/entity` to `/model/entity`

## Summary of the Structure

![Microservice](../static/Microservice%20inner.png)

* configuration
    * this directory should only contains configurations.
    * `ServerConfig` is used to config the SSL connection
    * `UserManagerSecurityConfiguration` is used to config Authn & Authz. 
* controller
    * this defined the REST interfaces
* filter
    * it defined the filters which used to customize the request & response
    * `JwtAuthenticationFilter` and `JwtAuthorizationFilter` defined the JWT related authn and authz
    * `MyCorsFilter` is the CORS filter, it deal with all CORS problems. 
* initialize
    * Implement `ApplicationRunner` to define the procedure which has to be finished before servlet starts. 
* mapper
    * Mybatis mapper interface
* model
    * dto 
        * stand for data transfer object
        * defined the object for data transfer between services or server-client
    * entity
        * database table object
* service
    * impl
        * defined all the interface implementations
        * `UserDetailsServiceImpl` is used for Spring Security build in login procedure, defined where to find the user data. 
        * `UserManagerServiceImpl` is implementation of custom user management interface, defined other user related operations.
    * `UserManagerService` defined the other user operations except for login and logout. 
* utils
    * all the utils class includes static variables. 

* ..../resources
    * `application.yml` defined all the environment parameters for the production version
    * mapper
        * `UserMapper.xml` is the MyBatis mapper file
    * static/secret
        * stores the certificate for SSL

## Add JWT to Backend

As usual, 3 steps

1. Add dependency

```groovy
	implementation 'com.auth0:java-jwt:3.10.3'
```

2. Add yml configuration

None

3. Add configuration bean



4. Add filters

## Containerize 

### Dockerfile
`backend/user-manager/Dockerfile`
```dockerfile
FROM openjdk:11-jre-slim

EXPOSE 8002

RUN mkdir /app

COPY user-manager/build/libs/*.jar /app/user-manager.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/user-manager.jar"]
```

### docker-compose.yml

```yaml
version: "3.5"

services:
  mysql-docker-container:
    image: mysql:latest
    networks:
      - spring-cloud-network
    environment:
      - MYSQL_ROOT_PASSWORD=123456
      - MYSQL_DATABASE=spring
      - MYSQL_USER=root
      - MYSQL_PASSWORD=123456
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3306"]
      interval: 1s
      timeout: 1s
      retries: 60
  user-manager:
    image: user-manager:latest
    build:
      context: ./
      dockerfile: user-manager/Dockerfile
    depends_on:
      - mysql-docker-container
    ports:
      - 8002:8002
      - 8443:8443
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-docker-container:3306/spring?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false
    networks:
      - spring-cloud-network
networks:
  spring-cloud-network:
    driver: bridge
```

## Test

we want to test each layer of the service and also sanity check the whole thing. 

### Test with Authn & Authz



### Unit Test

#### Controller

#### Service

### Smoke Test



### Integration Test