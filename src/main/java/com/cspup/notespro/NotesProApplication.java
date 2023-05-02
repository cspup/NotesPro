package com.cspup.notespro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cspup.notespro.mapper")
public class NotesProApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesProApplication.class, args);
    }

}
