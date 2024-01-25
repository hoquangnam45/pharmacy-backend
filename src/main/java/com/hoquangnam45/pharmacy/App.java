package com.hoquangnam45.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// TODO: Add third party authentication login support -> implemented
// TODO: Add comment -> pending copy needed from other project
// TODO: Add rating ->
// TODO: Add job for confirm email send out upon registered
// TODO: Add chat + support if needed
@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}