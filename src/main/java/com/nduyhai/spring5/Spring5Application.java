package com.nduyhai.spring5;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class Spring5Application {

	public static void main(String[] args) {
		SpringApplication.run(Spring5Application.class, args);
	}

	@Bean
    CommandLineRunner demo() {
	    return args -> {
            Flux<String> flux = Flux.fromArray("1,2,3,4".split(","));
            flux.map(Integer::parseInt)
                    .filter(i -> i % 2 == 0)
                    .subscribe( System.out::println, null, null);

        };
    }
	
}
