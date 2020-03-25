/*
 * Copyright (c) 2019-2020 "Neo4j,"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.neo4j.examples.record_movies;

import static org.assertj.core.api.Assertions.*;

import ac.simons.neo4j.examples.record_movies.domain.MovieRepository;
import ac.simons.neo4j.examples.record_movies.domain.PersonRepository;
import reactor.test.StepVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.springframework.boot.test.autoconfigure.data.DataNeo4jTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author Michael J. Simons
 * @author Gerrit Meier
 */
@Testcontainers
@DataNeo4jTest
class RepositoryIT {

	@Container
	private static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.0.0");

	@DynamicPropertySource
	static void neo4jProperties(DynamicPropertyRegistry registry) {
		registry.add("org.neo4j.driver.uri", neo4jContainer::getBoltUrl);
		registry.add("org.neo4j.driver.authentication.username", () -> "neo4j");
		registry.add("org.neo4j.driver.authentication.password", neo4jContainer::getAdminPassword);
	}

	@BeforeEach
	void setup(@Autowired Driver driver) throws IOException {
		try (var moviesReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/movies.cypher")));
			var session = driver.session()) {
			session.run("MATCH (n) DETACH DELETE n");
			var moviesCypher = moviesReader.lines().collect(Collectors.joining(" "));
			session.run(moviesCypher);
		}
	}

	@Test
	void loadAllPersonsFromGraph(@Autowired PersonRepository personRepository) {
		var expectedPersonCount = 133;
		StepVerifier.create(personRepository.findAll())
			.expectNextCount(expectedPersonCount)
			.verifyComplete();
	}

	@Test
	void findPersonByName(@Autowired PersonRepository personRepository) {
		StepVerifier.create(personRepository.findByName("Tom Hanks"))
			.assertNext(personEntity -> {
				assertThat(personEntity.born()).isEqualTo(1956);
			})
			.verifyComplete();
	}

	@Test
	void findsPersonsWhoActAndDirect(@Autowired PersonRepository personRepository) {
		var expectedActorAndDirectorCount = 5;
		StepVerifier.create(personRepository.getPersonsWhoActAndDirect())
			.expectNextCount(expectedActorAndDirectorCount)
			.verifyComplete();
	}

	@Test
	void findOneMovie(@Autowired MovieRepository movieRepository) {
		StepVerifier.create(movieRepository.findOneByTitle("The Matrix"))
			.assertNext(movie -> {
				assertThat(movie.title()).isEqualTo("The Matrix");
				assertThat(movie.description()).isEqualTo("Welcome to the Real World");
				assertThat(movie.directors()).hasSize(2);
				assertThat(movie.actors()).hasSize(5);
			})
			.verifyComplete();
	}
}
