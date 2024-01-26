package com.example.jsonutil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hr.util"})
public class JsonUtilApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonUtilApplication.class, args);
    }

}
