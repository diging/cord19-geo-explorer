package edu.asu.diging.cord19.explorer.core.model.impl;

import java.sql.Array;
import java.util.List;

public class GeometryImpl {
    private String type;
    private List<CoordinatesImpl> coordinates;
    
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public List<CoordinatesImpl> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinatesImpl> coordinates) {
        this.coordinates = coordinates;
    }


    
    
}