# Inventory

Inventory is an example Spring Boot application on how to properly set up a SaaS implementation by following the best practices as well having support for Multi Tenancy. 

## Highlights

- Discriminator based Multi Tenancy based on Hibernate Filter & Hibernate Interceptors with Auth0 integration
 -- Closely working together with Spring JPA thanks to Spring AOP
 -- Default CLOSED! No annotations, no "reminders to enable multi tenancy" -> You don't need to worry about multi tenancy anymore!! (except for one query - FindById -> See more why in section: (#FindById)
 -- Integrated with Auth0 identity provider with dynamically tenant resolving thanks to the JWT token
 -- Easily applied to an existing project! See section (#UpgradeMyProject) on how to!
 -- Full test coverage to show that it actually works!
 -- Explained what it exactly does, so you don't need to search on other resources how this code works.

## Initial Start

- JVM version should be Java 11 (or higher)
- Docker is necessary to be able to run the tests through TestContainer + local development thanks to the prepared docker commands below.

### Initial Database setup
Postgres is chosen for demo purposes, but other databases can be configured just as well (See #application.yml).

```bash
docker create -v /var/lib/postgresql/data --name inventory-postgres-data busybox
```

Running postgres 13

```bash
docker run --name inventory-postgreSQL-dev -p 5432:5432 -e POSTGRES_PASSWORD=inventory-docker-test-password -e POSTGRES_INITDB_ARGS="--data-checksums" -d --volumes-from inventory-postgres-data postgres:13-alpine
```

Create DATABASE in the postgres db.

```postgresql
CREATE DATABASE inventory
```


### Start / stop the container.

Afterwards to start or stop the container, you can use

```bash
docker start inventory-postgreSQL-dev
```

```bash
docker stop inventory-postgreSQL-dev
```

### FindById

explain why

### UpgradeMyProject

todo, upgrade db, migrate config, copy fwk subfolder
