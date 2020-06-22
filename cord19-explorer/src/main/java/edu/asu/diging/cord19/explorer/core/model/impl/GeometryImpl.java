package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeometryImpl {
    private String type;
    
    private ArrayList<ArrayList<ArrayList<?>>> coordinates; 
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<ArrayList<ArrayList<?>>> getCoordinatesList() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<ArrayList<ArrayList<?>>> coordinates) {
        this.coordinates = coordinates;
    }


    
    
}