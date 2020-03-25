package ac.simons.neo4j.examples.record_movies.web;

import ac.simons.neo4j.examples.record_movies.domain.MovieEntity;
import ac.simons.neo4j.examples.record_movies.domain.MovieRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieController {

	private final MovieRepository movieRepository;

	public MovieController(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	@PutMapping
	Mono<MovieEntity> createOrUpdateMovie(@RequestBody MovieEntity newMovie) {
		return movieRepository.save(newMovie);
	}

	@GetMapping(value = { "", "/" }, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<MovieEntity> getMovies() {
		return movieRepository
			.findAll();
	}

	@GetMapping("/by-title")
	Mono<MovieEntity> byTitle(@RequestParam String title) {
		return movieRepository.findOneByTitle(title);
	}

	@DeleteMapping("/{id}")
	Mono<Void> delete(@PathVariable String id) {
		return movieRepository.deleteById(id);
	}
}
