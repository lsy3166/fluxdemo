package com.example.demo.domain;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
	@Query(value = "SELECT b.* "
			+ "FROM noti n "
			+ "join board b on n.tbid = b.id "
			+ "WHERE (n.userid not like CONCAT('%',:username,',%') or n.userid is NULL) "
			+ "and b.writer != :username "
			+ "order by id desc")
	Flux<Notification> findByUserids(@Param("username") String username);
}
