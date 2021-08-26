package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.ArrayList;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import edu.asu.diging.cord19.explorer.core.model.CoordType;

/**
 * Handles Geometry for a country from the Countries Class. Type can be point,
 * Polygon, Multipolygon
 */

public class GeometryImpl {
    
    @Enumerated(EnumType.STRING)
    private CoordType type;


    private ArrayList<ArrayList<ArrayList<?>>> coordinates;

    public CoordType getType() {
        return type;
    }

    public void setType(CoordType type) {
        this.type = type;
    }

    public ArrayList<ArrayList<ArrayList<?>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<ArrayList<ArrayList<?>>> coordinates) {
        this.coordinates = coordinates;
    }
}