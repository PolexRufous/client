package com.thinkjava.client.handlers;

import com.github.javafaker.Faker;
import com.thinkjava.client.entities.Person;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Random;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

@Component
public class PersonRequestHandler {

    @Nonnull
    public Mono<ServerResponse> getPersonStream(ServerRequest serverRequest) {
        WebClient webClient = WebClient.create("http://localhost:8091");
        Flux<Person> personFlux = webClient.get()
                .uri("/persons")
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Person.class))
                .map(this::updatePerson)
                .delayElements(Duration.ofSeconds(2));
        personFlux = withNullTerminator(personFlux);
        return ServerResponse.ok()
                .header("Access-Control-Allow-Origin", "*")
                .contentType(TEXT_EVENT_STREAM)
                .body(fromPublisher(personFlux, Person.class));
    }

    private Person updatePerson(Person person) {
        String name = new Faker().name().fullName();
        String gender = getRandomGender();
        person.setGender(gender);
        person.setName(name);
        return person;
    }

    private String getRandomGender() {
        return new Random().nextBoolean() ? "Male" : "Female";
    }

    private Flux<Person> withNullTerminator(Flux<Person> personFlux) {
        return personFlux.concatWith(Flux.just(new Person()));
    }
}
