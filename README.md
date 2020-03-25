# Neo4j SDN-RX Reactive Movies based on JDK 14 Records

This is an example project how to use [Spring Data Neo4j⚡️RX (SDN-RX)][1] with JDK 14.
The domain entities `MovieEntity` and `PersonEntity` are JDK 14 Records.
Records a currently a preview feature. Thus the project needs to be build with `--enable-preview`.

The project also uses the [Spring Record Support][2] by [Oliver][3].
The later is needed to make the records work with Spring Data.

I included a JSON Module that makes Jackson access the fields directly to correctly deserialize records.

Also, have a look at the `RepositoryIT`. It uses a brand new Spring 5.2.5 feature named `@DynamicPropertySource`, that makes it incredible easy to create database tests based on [Testcontainers][4].

## Packaging

Install a JDK 14 and run `./mvnw clean verify`.

## Running

You need a Neo4j 4.0 database. The easiest way is creating a Docker instance:

```
docker run --publish=7474:7474 --publish=7687:7687 -e 'NEO4J_AUTH=neo4j/secret' neo4j:4.0.2    
```

Then, start the application with preview features enabled and your Neo4j settings:

```
java --enable-preview -jar target/record-movies-0.0.1-SNAPSHOT.jar --org.neo4j.driver.uri=bolt://localhost:7687 --org.neo4j.driver.authentication.password=secret
```

To create a new movie, you can run this cUrl command:

```
curl -X "PUT" "http://localhost:8080/movies" \
     -H 'Content-Type: application/json; charset=utf-8' \
     -d $'{
  "title": "Aeon Flux",
  "description": "Reactive is the new cool",
  "actors": [{
      "name": "Charlize Theron",
      "born": 1975
  }],
  "directors": [{
      "name": "Karyn Kusama",
      "born": 1968
  }]
}'
```

To retrieve all movies, run

```
curl http://localhost:8080/movies
```

To use the build-in examples, goto [localhost:7474][5] and execute the first step of the `:play movies` command.

[1]: https://github.com/neo4j/sdn-rx
[2]: https://github.com/odrotbohm/spring-record-support
[3]: https://twitter.com/odrotbohm
[4]: https://www.testcontainers.org
[5]: http://localhost:7474/browser/?cmd=play&arg=movies
