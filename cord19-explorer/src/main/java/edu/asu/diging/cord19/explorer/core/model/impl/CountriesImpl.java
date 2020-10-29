package edu.asu.diging.cord19.explorer.core.model.impl;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.Country;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFilter("myFilter")
public class CountriesImpl implements Country {
    
    @Id
    private ObjectId _id;
    
    private String id;
    @JsonProperty("properties")
    private PropertiesImpl properties;
    @JsonProperty("geometry")
    private GeometryImpl geometry;
    private String type;
    
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PropertiesImpl getProperties() {
        return properties;
    }

    public void setProperties(PropertiesImpl properties) {
        this.properties = properties;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getCountryCode() {
        return id;
    }

    public void setCountryCode(String countryCode) {
        this.id = countryCode;
    }

    public GeometryImpl getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryImpl geometry) {
        this.geometry = geometry;
    }
    

}
