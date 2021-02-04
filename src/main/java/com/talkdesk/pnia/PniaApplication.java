package com.talkdesk.pnia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PniaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PniaApplication.class, args);
    }

}
