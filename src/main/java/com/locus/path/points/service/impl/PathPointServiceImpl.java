package com.locus.path.points.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.ElevationResult;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.locus.path.points.helper.GoogleMapsHelper;
import com.locus.path.points.service.PathPointService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PathPointServiceImpl implements PathPointService
{

    //@formatter:off
    /*
    @Override
    public List<LocationPoint> getAllPathPoints(LocationPoint source, LocationPoint destination)
    {

        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GoogleMapsHelper.BASE_PATH);
        builder.queryParam("origin", String.join(",", Arrays.asList(source.getLatitude(), source.getLogitude())));
        builder.queryParam("destination", String.join(",", Arrays.asList(destination.getLatitude(), destination.getLogitude())));
        builder.queryParam("key", GoogleMapsHelper.MAPS_API_KEY);
        URI uri = builder.build().encode().toUri();
        MapsResponse response = restTemplate.getForObject(uri, MapsResponse.class);
        

        return getIntermediatePoints(response);
    }
    */
    //@formatter:on
    public List<LatLng> getAllFuzzyPathPoints(LatLng source, LatLng destination)
    {
        List<LatLng> path = new ArrayList<>();
        path.add(source);

        // Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder().apiKey(GoogleMapsHelper.MAPS_DIRECTION_API_KEY).build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, source.lat + "," + source.lng, destination.lat + "," + destination.lng).units(Unit.METRIC);
        try
        {
            DirectionsResult res = req.await();
            // Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0)
            {
                DirectionsRoute route = res.routes[0];
                log.info(res.routes.toString());
                LatLng prevPoint = source;
                if (route.legs != null)
                {
                    for (int i = 0; i < route.legs.length; i++)
                    {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null)
                        {
                            for (int j = 0; j < leg.steps.length; j++)
                            {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0)
                                {
                                    for (int k = 0; k < step.steps.length; k++)
                                    {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null)
                                        {
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1)
                                            {
                                                LatLng currentPoint = new LatLng(coord1.lat, coord1.lng);
                                                // if (getDistanceBetweenPoints(currentPoint, prevPoint) < 50.0)
                                                // continue;
                                                path.add(currentPoint);
                                                prevPoint = currentPoint;
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null)
                                    {
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords)
                                        {
                                            LatLng currentPoint = new LatLng(coord.lat, coord.lng);
                                            // if (getDistanceBetweenPoints(currentPoint, prevPoint) < 50.0)
                                            // continue;
                                            path.add(currentPoint);
                                            prevPoint = currentPoint;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        path.add(destination);
        return path;
    }

    private double getDistanceBetweenPoints(LatLng currentPoint, LatLng prevPoint)
    {
        int R = 6371000;
        double dLat = toRadians(currentPoint.lat - prevPoint.lat);
        double dLon = toRadians(currentPoint.lng - prevPoint.lng);
        double initialLat = toRadians(prevPoint.lat);
        double finalLat = toRadians(currentPoint.lat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double toRadians(double deg)
    {
        return deg * (Math.PI / 180);
    }

    @Override
    public List<LatLng> getAccurateEquidistantPathPoints(final LatLng source, final LatLng destination)
    {

        long distance = getDistanceBetweenTwoPointsUsingDistanceMatrixApi(source, destination);
        return getAllPathSamples(source, destination, distance);
    }

    private List<LatLng> getAllPathSamples(final LatLng source, final LatLng destination, final long distance)
    {
        List<LatLng> path = new ArrayList<>();
        path.add(source);
        GeoApiContext context = new GeoApiContext.Builder().apiKey(GoogleMapsHelper.MAPS_ELEVATION_API_KEY).build();
        try
        {
            ElevationResult[] result = ElevationApi.getByPath(context, (int) distance / 50, source, destination).await();
            if (result != null)
            {
                for (int i = 0; i < result.length; i++)
                {
                    path.add(result[i].location);
                }
            }
        }

        catch (Exception e)
        {
            log.error(e.getMessage());
        }
        return path;
    }

    private long getDistanceBetweenTwoPointsUsingDistanceMatrixApi(final LatLng source, final LatLng destination)
    {
        long distance = 0l;
        GeoApiContext context = new GeoApiContext.Builder().apiKey(GoogleMapsHelper.MAPS_DISTANCE_MATRIX_API_KEY).build();
        try
        {
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
            DistanceMatrix trix = req.origins(source.lat + "," + source.lng).destinations(destination.lat + "," + destination.lng).mode(TravelMode.DRIVING).await();
            if (trix != null && trix.rows != null && trix.rows.length > 0)
            {
                DistanceMatrixRow matrixRow = trix.rows[0];

                if (matrixRow.elements != null && matrixRow.elements.length > 0)
                {
                    DistanceMatrixElement element = matrixRow.elements[0];
                    distance = element.distance.inMeters;
                    log.info("the distance between two points is" + element.distance.inMeters);
                }

            }
        }
        catch (ApiException e)
        {
            log.error(e.getMessage());
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }

        return distance;
    }
}
