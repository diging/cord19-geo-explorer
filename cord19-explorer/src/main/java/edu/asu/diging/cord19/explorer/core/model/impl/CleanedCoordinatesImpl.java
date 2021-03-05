package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import edu.asu.diging.cord19.explorer.core.model.CoordType;

public class CleanedCoordinatesImpl {
    
    @Enumerated(EnumType.STRING)
    private CoordType type;

    private List<Double> coordinates; 
    
    
    public CoordType getType() {
        return getType();
    }
    
    public void setType(CoordType point) {
        this.type = point;
    }
    
    public List<Double> getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
    
}