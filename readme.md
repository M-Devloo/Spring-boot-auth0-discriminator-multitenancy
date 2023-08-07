# Inventory [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/M-Devloo/Spring-boot-auth0-discriminator-multitenancy/issues)

Inventory is a production ready Spring Boot application on how to correctly setup discriminator based multi-tenancy using OIDC while leveraging on top of the Spring libraries like JPA, Spring Security & many more.  

## Highlights

* Discriminator based Multi Tenancy based on Hibernate Filter & Hibernate Interceptor with Auth0 as identity provider and using the Auth0 ID as discriminator.  
* Full integration with Spring JPA (Repository, JpaRepository, CrudRepository, ...)  
* Developer friendly as it is default closed. No need to worry about data leaks or forgotten multi tenancy implementations due it is enabled by default on every single autowired repository.  
* Integrated with Auth0 identity provider which automatically resolves the tenant id by using the received JWT token.
* Fully tested E2E with focus on security & the multi tenancy implementation.

## Branches

> master

The `master` branch is the stable branch targeting Spring boot 3.

> release/spring-boot-v2

The `release/spring-boot-v2` branch is the stable branch targeting Spring boot 2.

> feature/X

Feature branches are development branches which should be not used in production environments.

## Detailed explanation

There are 3 types of Multi Tenancy.
* Multiple databases
* Same database - Multiple schema's
* Same Database, Same schema, using a discriminator <--
This project focuses on the latter option.

### Multi Tenancy

All the framework classes can be found under the [directory](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/HibernateInterceptorConfiguration.java) 

- [TenantAssistance](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/TenantAssistance.java)   
 -- Helper class to grab the SecurityContextHolder and extract the Auth0 tenant ID from the JWT token which is used in the TenantInterceptor  
- [TenantInterceptor](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/TenantInterceptor.java)   
 -- Implementation which automatically adds the tenantId in case the entity is a TenantEntity
- [HibernateMultiTenancyInterceptorBean](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/HibernateMultiTenancyInterceptorBean.java)   
 -- Spring @Bean configuration to use the TenantInterceptor which gets autowired in HibernateInterceptorConfiguration  
- [HibernateInterceptorConfiguration](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/HibernateInterceptorConfiguration.java)   
 -- Configuration to tell hibernate to use the EmptyInterceptor @Bean as a session interceptor to intercept operations like save, update, delete, etc.  
- [TenantEntity](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/TenantEntity.java)  
 -- JPA entity with @Filter which automatically (Due to TenantServiceAspect) adds a where clause around all repositories with tenantId as input.  
- [TenantServiceAspect](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/TenantServiceAspect.java)  
 -- Spring AOP implementation to enable the @Filter automatically on every method of a Spring Data repository. It unwraps the current Session (So transaction required) and enables the filter with tenantId as param.  
- [MultiTenancyRepository](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/MultiTenancyRepository.java)  
 -- Custom repository base implementation that overrides by default the findById() method with CriteriaBuilder to avoid direct fetching. [Direct fetching?](#Direct-fetching-vs-CriteriaBuilder)    
 -- The method delete() is also implemented here due it used internally em.find() in SimpleJpaRepository which caused to bypass the multi tenancy.    
 -- The method getOne() throws a EntityNotFoundException as otherwise it bypasses the filter due it returns a proxy of the entity. [Lazy Fetched proxy](#GetOne)  
 
#### Direct fetching vs CriteriaBuilder

Taken from the [hibernate](https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html) documentation! 
> Filters apply to entity queries, but not to direct fetching.   
> Therefore, all methods that use em.find() do not take the filter into consideration when fetching an entity from the Persistence Context.  

When using Spring Data be careful to know which methods of SimpleJpaRepository are using direct fetching (em.find()) and which ones are using (getQuery() -> CriteriaBuilder) as the @Filter is NOT applied for direct fetching!  
Luckily the class [MultiTenancyRepository](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/multitenancy/MultiTenancyRepository.java) already takes care of this problem completely!    

#### GetOne

The JpaRepository getOne() method uses the em.getReference() internally.  
This will return an entity proxy which only has the primary key field initialized. Other fields are unset unless we lazily request them through getters.    
The proxy does not have support for the filter thus we are avoiding this issue all together by throwing a EntityNotFoundException which is allowed by JPA.

#### Multi Tenancy conclusion

* The goal to provide a default closed & worry free discriminator based multi tenancy implementation for Spring JPA is achieved  
This concludes the documentation for the multi tenancy part.
 
### Auth0

- [AudienceValidator](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/auth0/security/AudienceValidator.java)   
 -- OAuth2Token validator which validates the audience  
- [SecurityConfig](/src/main/java/com/github/mdevloo/multi/tenancy/fwk/auth0/security/SecurityConfig.java)     
 -- Spring Security Web config that uses Auth0 as a OAuth2.0 resource server. Through autowiring, this is integrated in the SecurityContextHolder.   

## Startup of the application

- JVM version should be Java 17 (or higher)
- Docker is necessary to be able to run the tests through TestContainer + local development thanks to the prepared docker commands below.

### Initial Database setup
Postgres is chosen for demo purposes, but other databases can be configured just as well. Details can be found in [Application.yml](/src/main/resources/application.yml).

For the development environment locally, we choose the use the docker internal volume management to persist our local database. (See: [Docker hub postgres for more info](https://hub.docker.com/_/postgres/))  
* Docker own internal volume management [Volumes](https://docs.docker.com/storage/volumes/)
    * Easier to share database data between docker applications
    * OS independent

> To keep the data persistent, we are going to use a data volume container.  

```bash
docker create -v /var/lib/postgresql/data --name inventory-postgres-data-dev busybox
```
Explanation of the command:
* Create a new data container from the busybox image (Time of writing: Version 1.32) 
* The -v option specifies the location of the persisted data inside the docker container. 
* The --name option provides a friendly name to the data container.

> Now we will start the postgres image as in its own container together with the previous created data volume container.

```bash
docker run --name inventory-postgreSQL-dev -p 5432:5432 -e POSTGRES_PASSWORD=inventory-docker-test-password -e POSTGRES_INITDB_ARGS="--data-checksums" -d --volumes-from inventory-postgres-data-dev postgres:14-alpine
```

Explanation of the command:  
* Create and start a new container from the postgres:14-alpine image.   
* For this container, we are providing the name: `inventory-postgreSQL-dev`
* Start the database on port `5432` internally and expose it through the same port.
* We are providing parameters (-e) to the database like:  
    * POSTGRES_PASSWORD : Database password  
    * POSTGRES_USER : Database user, not defined for development, so (default `postgres`)  
    * POSTGRES_INITDB_ARGS : Additional arguments like --data-checksums.  
        * --data-checksums adds checksums to each page of data when it is written to the HDD. This can detect data corruption. 
* By using -d, we are running the container in detached mode
* We are mounting the busybox container as a volume storage by adding --volumes-from  

To be able to start the application, we need to add a new database called `inventory` through your favorite editor.

> Create the required database.
```postgresql
CREATE DATABASE inventory
```

That's it! You can now properly run the application without any issues.

To access the postgres through command line, the following command can help you with that:
     
```bash
docker run -it --link inventory-postgreSQL-dev:postgres --rm postgres:14-alpine sh -c 'exec psql -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U postgres'
```

This will ask for the password which can be found in [Application.yml](/src/main/resources/application.yml) -> datasource password  

## Start / stop the local development environment.

Afterwards to start or stop the container, you can use

> Start the database container

```bash
docker start inventory-postgreSQL-dev
```
> Stop the database container

```bash
docker stop inventory-postgreSQL-dev
```
