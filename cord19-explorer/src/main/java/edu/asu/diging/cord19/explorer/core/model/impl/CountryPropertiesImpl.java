package edu.asu.diging.cord19.explorer.core.model.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Handle the properties of countries
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryPropertiesImpl {

    private String name;
    private String countryCenter;
    private int selectedWikipediaCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCenter() {
        return countryCenter;
    }

    public void setCountryCenter(String center) {
        this.countryCenter = center;
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
