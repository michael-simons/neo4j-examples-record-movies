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
package ac.simons.neo4j.examples.record_movies.domain;

import static org.neo4j.springframework.data.core.schema.Relationship.Direction.*;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Property;
import org.neo4j.springframework.data.core.schema.Relationship;

@Node("Movie")
public record MovieEntity(
	@Id String title,
	@Property("tagline") String description,
	@Relationship(type = "ACTED_IN", direction = INCOMING)
	Set<PersonEntity> actors,
	@Relationship(type = "DIRECTED", direction = INCOMING)
	Set<PersonEntity> directors
) {

	public static MovieEntity of(String title, String description) {
		return new MovieEntity(title, description, new HashSet<>(), new HashSet<>());
	}
}
