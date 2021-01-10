package com.locus.path.points.dto;

import java.util.List;

import com.google.maps.model.LatLng;

import lombok.Data;

@Data
public class MapsResponse
{
    private List<LatLng> pathPoints;
}
