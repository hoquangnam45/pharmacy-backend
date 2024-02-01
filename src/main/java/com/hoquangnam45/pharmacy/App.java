package com.hoquangnam45.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// TODO: Add third party authentication login support -> implemented
// TODO: Add comment -> pending copy needed from other project -> copied not tested
// TODO: Add rating -> working on
// TODO: Add confirm email send out upon registered -> implemented
// TODO: Add chat + support if needed -> looking into firebase
// TODO: Integrate with payment transaction processing service like momo / napas / bank / credit card
// TODO: Start working on FE
// TODO: Implement search product api
// TODO: Setup deployment / pipeline / config for production
// TODO: Refactor to use queue for heavy IO tasks instead of blocking api (mail)
@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}