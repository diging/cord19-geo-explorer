package edu.asu.diging.cord19.explorer.core.model.impl;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.Country;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountriesImpl implements Country {
    
    @Id
    private ObjectId _id;
    
    private String id;
    @JsonProperty("properties")
    private PropertiesImpl properties;
    @JsonProperty("geometry")
    private GeometryImpl geometry;
    private int selectedWikipediaCount;
    private String type;
    private String center;
    
    
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
    public int getSelectedWikipediaCount() {
        return selectedWikipediaCount;
    }

    public void setSelectedWikipediaCount(int selectedWikipediaCount) {
        this.selectedWikipediaCount = selectedWikipediaCount;
    }
    
    public void incrementSelectedWikipediaCount() {
        this.selectedWikipediaCount++;
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

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }
    
    
    
    

}
