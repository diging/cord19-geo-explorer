package edu.asu.diging.cord19.explorer.core.model.impl;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.Country;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountriesImpl implements Country{
    
    @Id
    private ObjectId id;
    
    @JsonProperty("geometry")
    private GeometryImpl geometry;
    
    public GeometryImpl getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryImpl geometry) {
        this.geometry = geometry;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    
    
    
    
    

}
