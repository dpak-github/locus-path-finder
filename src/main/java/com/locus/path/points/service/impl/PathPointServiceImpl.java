package com.locus.path.points.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;
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

    @Override
    public List<LatLng> getAllPointsUsingDirectionApi(LatLng source, LatLng destination)
    {
        List<LatLng> pathPoints = new ArrayList<>();
        pathPoints.add(source);

        GeoApiContext context = GoogleMapsHelper.getApiContext();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, source.lat + "," + source.lng, destination.lat + "," + destination.lng).units(Unit.METRIC).mode(TravelMode.DRIVING);
        try
        {
            DirectionsResult res = req.await();

            if (Objects.nonNull(res.routes) && res.routes.length > 0)
            {
                DirectionsRoute route = res.routes[0];
                LatLng prevPoint = source;
                if (Objects.nonNull(route.legs))
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
                                        EncodedPolyline encodedPolyline = step1.polyline;
                                        if (Objects.nonNull(encodedPolyline))
                                        {
                                            pathPoints.addAll(GoogleMapsHelper.findEquidistantPointsFromEncodedPolyline(encodedPolyline, prevPoint));
                                            prevPoint = pathPoints.get(pathPoints.size() - 1);

                                        }
                                    }
                                }
                                else
                                {
                                    EncodedPolyline encodedPolyline = step.polyline;
                                    if (Objects.nonNull(encodedPolyline))
                                    {
                                        pathPoints.addAll(GoogleMapsHelper.findEquidistantPointsFromEncodedPolyline(encodedPolyline, prevPoint));
                                        prevPoint = pathPoints.get(pathPoints.size() - 1);
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
            log.error(ex.getMessage());
        }

        if (!pathPoints.get(pathPoints.size() - 1).equals(destination))
        {
            pathPoints.add(destination);
        }

        return pathPoints;
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

        catch (Exception e)
        {
            log.error(e.getMessage());
        }

        return distance;
    }
}
