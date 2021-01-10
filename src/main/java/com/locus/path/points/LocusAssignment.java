package com.locus.path.points;

import java.util.List;

import com.google.maps.model.LatLng;
import com.locus.path.points.service.PathPointService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class LocusAssignment implements CommandLineRunner
{
    @Autowired
    private PathPointService pathPointService;

    public static void main(String[] args)
    {
        SpringApplication.run(LocusAssignment.class, args);

    }

    // @formatter:off
    
    @Override
    public void run(String... args) throws Exception
    {
        List<LatLng> paths = pathPointService.getAllFuzzyPathPoints(new LatLng(18.5526009, 73.7689953), new LatLng(18.5793833, 73.6837402));
        log.info("Path started");

        log.info(paths.size() + " size " + paths.toString());

        log.info("path ended");

    }
    
    // @formatter:on
}
