package com.example.demo.web;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Notification;
import com.example.demo.domain.NotificationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
public class NotificationController {
	
	private final NotificationRepository notificationRepository;
	private final Sinks.Many<Notification> sink;

	public NotificationController(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
		this.sink = Sinks.many().multicast().onBackpressureBuffer();
	}

	@RequestMapping(value = "/notification/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<Notification>> findAllSSE() {
		return sink.asFlux().map(notification -> ServerSentEvent.builder(notification).build()).doOnCancel(()->{
			sink.asFlux().blockLast(); // browser cancel button click해도 다시 실행되게 하기 위해
		});
	}
	
	@PostMapping("/notification")
	public Mono<Notification> save(@RequestBody Notification noti) {
		return notificationRepository.save(noti).doOnNext(notification -> {
			sink.tryEmitNext(notification); // sink에 담아줘야 sse에서 불러올 수 있음
		});
	}
	
	@PostMapping("/notification/delete")
	@Transactional
	public Mono<Void> delete(@RequestBody Notification noti) {
		sink.tryEmitNext(noti);
		return notificationRepository.deleteByNotiid(noti.getId());
	}
	
}
