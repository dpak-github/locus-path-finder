package com.locus.path.points;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Slf4j
@EnableSwagger2
public class LocusAssignment implements CommandLineRunner
{
    public static void main(String[] args)
    {
        SpringApplication.run(LocusAssignment.class, args);

    }

    @Override
    public void run(String... args) throws Exception
    {
        log.info("Welcome to Path Points finder");

    }

}
