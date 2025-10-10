package com.cuijian.aimeeting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cuijian.aimeeting.mapper")
public class AiMeetingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMeetingApplication.class, args);
    }

}
