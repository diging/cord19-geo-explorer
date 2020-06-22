package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;

public class CleanedCoordinatesImpl {
    private String type;
    private List<Double> coordinates; 
    
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public List<Double> getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    
    
    
}