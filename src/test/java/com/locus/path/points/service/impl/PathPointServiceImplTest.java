package com.locus.path.points.service.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.LatLng;
import com.locus.path.points.service.PathPointService;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

class PathPointServiceImplTest
{
    private ObjectMapper mapper;

    @InjectMocks
    private PathPointService pathPointService;

    @BeforeMethod
    public void initMock()
    {
        mapper = new ObjectMapper();
        pathPointService = new PathPointServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIfOutputIsCorrect() throws JsonParseException, JsonMappingException, FileNotFoundException, IOException
    {
        LatLng source = new LatLng(19.0377148, 73.0138703);
        LatLng destination = new LatLng(19.0418832, 73.0287206);
        List<LatLng> responsePoints = mapper.readValue(new FileReader("./src/test/resources/sample/sample.json"), new TypeReference<List<LatLng>>()
        {
        });
        List<LatLng> points = pathPointService.getAllPointsUsingDirectionApi(source, destination);
        assertEquals(points, responsePoints);
    }

}
