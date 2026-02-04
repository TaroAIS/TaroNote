package com.taronote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Modulith
public class TaroNoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaroNoteApplication.class, args);
    }
}
