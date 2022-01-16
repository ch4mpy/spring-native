/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.experimental.api;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.experimental.domain.Flurb;
import org.springframework.experimental.domain.Foo;
import org.springframework.experimental.domain.FooRepository;
import org.springframework.web.servlet.function.RouterFunction;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = { FooRepository.class })
@EntityScan(basePackageClasses = { Foo.class })
public class DataJpaMultimoduleApplication {

	private final FooRepository entities;

	public DataJpaMultimoduleApplication(FooRepository entities) {
		this.entities = entities;
	}

	@Bean
	public CommandLineRunner runner() {
		return args -> {

			Optional<Foo> maybeFoo = entities.findById(1L);
			Foo foo;
			foo = maybeFoo.orElseGet(() -> entities.save(new Foo("Hello")));
			Flurb flurb = new Flurb();
			flurb.setValue("Balla balla");
			foo.setFlurb(flurb);
			entities.save(foo);

			entities.findWithBetween("a", "X");
		};
	}

	@Bean
	AuditorAware<String> fixedAuditor() {
		return () -> Optional.of("Douglas Adams");
	}

	@Bean
	public RouterFunction<?> userEndpoints() {
		return route(GET("/"), request -> ok().body(findOne()));
	}

	private Foo findOne() {
		return entities.findById(1L).get();
	}

	public static void main(String[] args) {
		SpringApplication.run(DataJpaMultimoduleApplication.class, args);
	}
}
