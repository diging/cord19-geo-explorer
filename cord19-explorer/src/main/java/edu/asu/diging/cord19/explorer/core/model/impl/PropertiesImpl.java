package edu.asu.diging.cord19.explorer.core.model.impl;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;




@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertiesImpl {
    
    
    private String name;
    private String center;
    private int selectedWikipediaCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
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
    

}
