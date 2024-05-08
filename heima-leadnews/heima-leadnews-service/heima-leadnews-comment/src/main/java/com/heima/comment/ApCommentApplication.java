package com.heima.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.heima.apis")
public class ApCommentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApCommentApplication.class, args);
    }
}
