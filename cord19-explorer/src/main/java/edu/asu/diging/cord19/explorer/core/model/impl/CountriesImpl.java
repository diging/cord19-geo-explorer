package edu.asu.diging.cord19.explorer.core.model.impl;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.Country;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountriesImpl implements Country {
    
    @Id
    private ObjectId id;
    
    private String countryCode;
    private String name;
    private GeometryImpl geometry;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeometryImpl getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryImpl geometry) {
        this.geometry = geometry;
    }
    
    
    
    

}
