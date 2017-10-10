package com.nduyhai.spring5;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;


@SpringBootApplication
public class Spring5Application {

	public static void main(String[] args) {
		SpringApplication.run(Spring5Application.class, args);
	}

	@Bean
    CommandLineRunner demo(MovieRepository repository) {
	    return args -> repository.deleteAll()
        .subscribe(null, null, () -> {
            Stream.of("Game of throne", "Enter Mono<Void>", "Winter is coming", "Chaos isn't a bitch", "Back to the future")
                    .map(name -> new Movie(UUID.randomUUID().toString(), name, randomGenre()))
                    .forEach(movie -> repository.save(movie).subscribe(System.out::println));

        });

    }

    private String randomGenre() {
        String[] genres = "honor,action,drama".split(",");
        return genres[new Random().nextInt(genres.length)];
    }

}


@Service
class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Flux<MovieEvent> streamStreams(Movie movie) {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
        Flux<MovieEvent> events = Flux.fromStream(Stream.generate(() -> new MovieEvent(movie, new Date(), randomUser())));

        return Flux.zip(interval, events)
                .map(Tuple2::getT2);
    }

    private String randomUser() {
        String[] user = "John Snow,Arya Stark,Daenerys Targaryen,Theon Greyjoy".split(",");
        return user[new Random().nextInt(user.length)];
    }

    public Flux<Movie> all() {
        return this.movieRepository.findAll();
    }

    public Mono<Movie> byId(String id) {
        return this.movieRepository.findById(id);
    }
}

@RestController
@RequestMapping("/movies")
class MovieRestController {
    private final MovieService movieService;

    public MovieRestController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieEvent> events(@PathVariable  String id) {
        return this.movieService.byId(id).flatMapMany(this.movieService::streamStreams);
    }

    @GetMapping
    public Flux<Movie> all() {
        return this.movieService.all();
    }

    @GetMapping("/{id}")
    public Mono<Movie> byId(@PathVariable  String id) {
        return this.movieService.byId(id);
    }
}

interface MovieRepository extends ReactiveMongoRepository<Movie, String> {

}

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
class MovieEvent {
    private Movie movie;
    private Date when;
    private String user;
}


@Document
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
class Movie {
    @Id
    private String id;

    private String title, genre;
}
