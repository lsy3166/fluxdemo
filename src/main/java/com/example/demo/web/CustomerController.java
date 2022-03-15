package com.example.demo.web;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Customer;
import com.example.demo.domain.CustomerRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
public class CustomerController {
	
	private final CustomerRepository customerRepository;
	private final Sinks.Many<Customer> sink;

	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
		this.sink = Sinks.many().multicast().onBackpressureBuffer();
	}

	@RequestMapping(value = "/customers", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Customer> findAll() {
		return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
	}
	
	@RequestMapping(value = "/customers/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<Customer>> findAllSSE() {
		return sink.asFlux().map(customer -> ServerSentEvent.builder(customer).build()).doOnCancel(()->{
			sink.asFlux().blockLast(); // browser cancel button click해도 다시 실행되게 하기 위해
		});
	}
	
	@PostMapping("/customer")
	public Mono<Customer> save() {
		return customerRepository.save(new Customer("GilDong", "Hong")).doOnNext(customer -> {
			sink.tryEmitNext(customer); // sink에 담아줘야 sse에서 불러올 수 있음
		});
	}
	
	@RequestMapping(value = "/customer/{id}")
	public Mono<Customer> findById(@PathVariable Long id) {
		return customerRepository.findById(id).log();
	}
	
	@RequestMapping(value = "/flux")
	public Flux<Integer> flux() {
		return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
	}
	
	@RequestMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Integer> fluxstream() {
		return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
	}

}
