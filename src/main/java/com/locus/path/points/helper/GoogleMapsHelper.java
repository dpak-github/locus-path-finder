package com.locus.path.points.helper;

import java.util.ArrayList;
import java.util.List;

import com.google.maps.GeoApiContext;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GoogleMapsHelper
{

    private static final int SPACING_BETWEEN_POINTS = 50;

    private GoogleMapsHelper()
    {

    }

    public static final String MAPS_DIRECTION_API_KEY = "AIzaSyAb8ohmBXqtK4y2_a5CFnFnfLGiOsuwjIo";
    public static final String MAPS_DISTANCE_MATRIX_API_KEY = "AIzaSyAb8ohmBXqtK4y2_a5CFnFnfLGiOsuwjIo";
    public static final String MAPS_ELEVATION_API_KEY = "AIzaSyAb8ohmBXqtK4y2_a5CFnFnfLGiOsuwjIo";

    public static GeoApiContext getApiContext()
    {
        return new GeoApiContext.Builder().apiKey(GoogleMapsHelper.MAPS_DIRECTION_API_KEY).build();
    }

    // returns the last point from the startPoint
    public static List<LatLng> findEquidistantPointsFromEncodedPolyline(final EncodedPolyline encodedPolyline, LatLng startPoint)
    {
        final List<LatLng> pathPoints = new ArrayList<>();
        final List<LatLng> coords = encodedPolyline.decodePath();
        for (LatLng currentPoint : coords)
        {
            int distance = getDistanceBetweenPoints(currentPoint, startPoint);
            if (distance > 50)
            {
                pathPoints.addAll(getAllIntermediatePoints(currentPoint, startPoint, distance));
                startPoint = pathPoints.get(pathPoints.size() - 1);
            }
            else if (distance == 50)
            {
                pathPoints.add(currentPoint);
                startPoint = pathPoints.get(pathPoints.size() - 1);
            }

        }
        return pathPoints;
    }

    private static int getDistanceBetweenPoints(LatLng currentPoint, LatLng prevPoint)
    {
        int R = 6371000;
        double dLat = toRadians(currentPoint.lat - prevPoint.lat);
        double dLon = toRadians(currentPoint.lng - prevPoint.lng);
        double initialLat = toRadians(prevPoint.lat);
        double finalLat = toRadians(currentPoint.lat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (R * c);
    }

    private static double toRadians(double deg)
    {
        return deg * (Math.PI / 180);
    }

    private static List<LatLng> getAllIntermediatePoints(LatLng currentPoint, LatLng startPoint, int distance)
    {
        List<LatLng> paths = new ArrayList<>();
        double dlat = currentPoint.lat - startPoint.lat;
        double dlng = currentPoint.lng - startPoint.lng;

        double addLat = (SPACING_BETWEEN_POINTS * dlat) / distance;
        double addLng = (SPACING_BETWEEN_POINTS * dlng) / distance;

        int i = 1;
        int l = 0;
        while (l < distance)
        {
            double lat = startPoint.lat + addLat * i;
            double lng = startPoint.lng + addLng * i;
            paths.add(new LatLng(lat, lng));
            i = i + 1;
            l = l + SPACING_BETWEEN_POINTS;
        }

        return paths;
    }
}
