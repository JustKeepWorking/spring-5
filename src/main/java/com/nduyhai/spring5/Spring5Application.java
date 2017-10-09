package com.nduyhai.spring5;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.CrudRepository;
import reactor.core.publisher.Flux;

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
        .subscribe(null, null, args -> {
            Stream.of("Game of throne", "Enter Mono<Void>", "Winter is coming", "Chao isn't a bitch", "Back to the future")
                    .map(name -> new Movie(UUID.randomUUID().toString(), name, randomGenre()))
                    .forEach(movie -> repository.save(movie).subscribe(System.out::println));

        });

    }

    private String randomGenre() {
        String[] genres = "honor,action,drama".split(",");
        return genres[new Random().nextInt(genres.length)];
    }

}

interface MovieRepository extends ReactiveMongoRepository<Movie, String> {

}

@Document
@AllArgsConstructor
@ToString
@Data
class Movie {
    @Id
    private String id;

    private String title, genre;
}
