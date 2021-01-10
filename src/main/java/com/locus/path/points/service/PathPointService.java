package com.locus.path.points.service;

import java.util.List;

import com.google.maps.model.LatLng;

public interface PathPointService
{
    List<LatLng> getAllFuzzyPathPoints(LatLng source, LatLng destination);

    List<LatLng> getAccurateEquidistantPathPoints(LatLng source, LatLng destination);
}
