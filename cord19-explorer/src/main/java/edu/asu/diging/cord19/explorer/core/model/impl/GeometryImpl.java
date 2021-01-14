package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.ArrayList;

public class GeometryImpl {
    
    /**
     * Handles Geometry for a country from the Countries Class.
     * Type can be point, Polygon, Multipolygon
     * 
     */
    
    
    private String type;
    
    private ArrayList<ArrayList<ArrayList<?>>> coordinates; 
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<ArrayList<ArrayList<?>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<ArrayList<ArrayList<?>>> coordinates) {
        this.coordinates = coordinates;
    }


    
    
}