package com.asu.diging.cord19.explorer.core.services.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.asu.diging.cord19.explorer.core.model.CoordType;
import edu.asu.diging.cord19.explorer.core.model.impl.CleanedCoordinatesImpl;
import edu.asu.diging.cord19.explorer.core.service.worker.CoordinateParser;
import edu.asu.diging.cord19.explorer.core.service.worker.impl.CoordinateParserImpl;

public class CoordinateParserTest {
    
    
    @Test
    public void test_parseLength3Coords_success() {
        String coords = "Coord|40|00|00|N|116|19|36|E|region:CN-11_type:edu|display=inline,title";
        CoordinateParser coordinateParser = new CoordinateParserImpl();
        CleanedCoordinatesImpl cleanedCoords = coordinateParser.parse(coords);
        CleanedCoordinatesImpl coordsToTest = new CleanedCoordinatesImpl();
        coordsToTest.setType(CoordType.Point);
        List<Double> coordinates = new ArrayList<Double>();
        coordinates.add(40.0);
        coordinates.add(116.32666666666667);
        coordsToTest.setCoordinates(coordinates);
        assertEquals(cleanedCoords.getCoordinates(), coordsToTest.getCoordinates());
    }
    
    @Test
    public void test_parseLength2Coords() {
        String coords = "coord|37|34|N|126|58|E|region:KR-11|display=title,inline";
        CoordinateParser coordinateParser = new CoordinateParserImpl();
        CleanedCoordinatesImpl cleanedCoords = coordinateParser.parse(coords);
        CleanedCoordinatesImpl coordsToTest = new CleanedCoordinatesImpl();
        coordsToTest.setType(CoordType.Point);
        List<Double> coordinates = new ArrayList<Double>();
        coordinates.add(37.56666666666667);
        coordinates.add(126.96666666666667);
        coordsToTest.setCoordinates(coordinates);
        assertEquals(cleanedCoords.getCoordinates(), coordsToTest.getCoordinates());
    }
}