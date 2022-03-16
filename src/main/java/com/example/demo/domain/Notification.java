package com.example.demo.domain;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Notification {

    @Id
    private long id;
    private String title;
    private String content;

}
