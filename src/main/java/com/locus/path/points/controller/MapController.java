package com.locus.path.points.controller;

import java.util.List;

import com.google.maps.model.LatLng;
import com.locus.path.points.service.PathPointService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/maps")
@AllArgsConstructor
public class MapController
{
    private final PathPointService pathPointService;

    @GetMapping("/path/points")
    public List<LatLng> getIntermediatePathPoints(@RequestParam(required = true) final String sourceLat, @RequestParam(required = true) final String sourceLang,
            @RequestParam(required = true) final String destLat, @RequestParam(required = true) final String destLang)
    {

        LatLng sourcePoint = new LatLng(Double.parseDouble(sourceLat), Double.parseDouble(sourceLang));
        LatLng destinationPoint = new LatLng(Double.parseDouble(destLat), Double.parseDouble(destLang));
        return pathPointService.getAllPointsUsingDirectionApi(sourcePoint, destinationPoint);
    }
}
