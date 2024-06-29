package com.goott.potatoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableAsync
@EnableScheduling
@SpringBootApplication
@ServletComponentScan
public class PotatoesApplication {
    public static void main(String[] args) {
        SpringApplication.run(PotatoesApplication.class, args);
    }
}
