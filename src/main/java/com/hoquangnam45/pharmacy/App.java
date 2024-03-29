package com.hoquangnam45.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// TODO: Add third party authentication login support -> implemented
// TODO: Add comment -> pending copy needed from other project -> copied not tested
// TODO: Add rating
// TODO: Add confirm email send out upon registered -> implemented
// TODO: Add chat + support if needed (looking into firebase) (not critical now since)
// TODO: Refactor so that listing is coupled with product, packaging is replaced with options
// TODO: Integrate with payment transaction processing service like momo / napas / bank / credit card -> planned (seem like implement for momo would be easier)
// TODO: Start working on FE
// TODO: Implement search product api -> Implemented, not tested
// TODO: Setup deployment / pipeline / config for production
// TODO: Refactor to use queue for heavy IO tasks instead of blocking api (mail)
@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}