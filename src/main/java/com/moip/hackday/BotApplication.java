package com.moip.hackday;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "com.moip.hackday"})
public class BotApplication {
    public static void main(String[] args) {
        run(BotApplication.class, args);
    }
}