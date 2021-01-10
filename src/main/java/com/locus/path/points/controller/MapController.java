package com.locus.path.points.controller;

import java.util.List;

import com.google.maps.model.LatLng;
import com.locus.path.points.service.PathPointService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController("/api/maps")
@AllArgsConstructor
public class MapController
{
    private final PathPointService pathPointService;

    @GetMapping("/paths")
    public List<LatLng> getIntermediatePathPoints(final List<String> source, final List<String> destination)
    {
        if (source.size() != 2 || destination.size() != 2)
        {
            throw new RuntimeException("Please give latitude and longitude values for source and destination");
        }

        LatLng sourcePoint = new LatLng(Double.parseDouble(source.get(0)), Double.parseDouble(source.get(1)));
        LatLng destinationPoint = new LatLng(Double.parseDouble(destination.get(0)), Double.parseDouble(destination.get(1)));
        return pathPointService.getAccurateEquidistantPathPoints(sourcePoint, destinationPoint);
    }
}
