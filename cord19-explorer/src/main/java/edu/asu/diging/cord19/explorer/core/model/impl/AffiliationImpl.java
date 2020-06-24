package edu.asu.diging.cord19.explorer.core.model.impl;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.Affiliation;

public class AffiliationImpl implements Affiliation {

    private String laboratory;
    private String institution;
    private String locationSettlement;
    private String locationRegion;
    private String locationCountry;
    private Map<String, Object> other;
    
    private List<WikipediaArticleImpl> wikiarticles;
    private WikipediaArticleImpl selectedWikiarticle;
    
    private WikipediaSelectionStatus selectionStatus;
    private String selectionCheckedOn;

    @JsonProperty("location")
    private void unpackLocation(Map<String, Object> location) {
        this.locationSettlement = (String) location.get("settlement");
        this.locationRegion = (String) location.get("region");
        this.locationCountry = (String) location.get("country");
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        if (other == null) {
            other = new HashMap<>();
        }
        other.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#getLaboratory()
     */
    @Override
    public String getLaboratory() {
        return laboratory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#setLaboratory(java
     * .lang.String)
     */
    @Override
    public void setLaboratory(String laboratory) {
        this.laboratory = laboratory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#getInstitution()
     */
    @Override
    public String getInstitution() {
        return institution;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#setInstitution(
     * java.lang.String)
     */
    @Override
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#
     * getLocationSettlement()
     */
    @Override
    public String getLocationSettlement() {
        return locationSettlement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#
     * setLocationSettlement(java.lang.String)
     */
    @Override
    public void setLocationSettlement(String locationSettlement) {
        this.locationSettlement = locationSettlement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#getLocationRegion(
     * )
     */
    @Override
    public String getLocationRegion() {
        return locationRegion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#setLocationRegion(
     * java.lang.String)
     */
    @Override
    public void setLocationRegion(String locationRegion) {
        this.locationRegion = locationRegion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#getLocationCountry
     * ()
     */
    @Override
    public String getLocationCountry() {
        return locationCountry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#setLocationCountry
     * (java.lang.String)
     */
    @Override
    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#getOther()
     */
    @Override
    public Map<String, Object> getOther() {
        return other;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Affiliation#setOther(java.util
     * .Map)
     */
    @Override
    public void setOther(Map<String, Object> other) {
        this.other = other;
    }

    @Override
    public List<WikipediaArticleImpl> getWikiarticles() {
        return wikiarticles;
    }

    @Override
    public void setWikiarticles(List<WikipediaArticleImpl> wikiarticles) {
        this.wikiarticles = wikiarticles;
    }

    @Override
    public WikipediaArticleImpl getSelectedWikiarticle() {
        return selectedWikiarticle;
    }

    @Override
    public void setSelectedWikiarticle(WikipediaArticleImpl selectedWikiarticle) {
        this.selectedWikiarticle = selectedWikiarticle;
    }

    @Override
    public WikipediaSelectionStatus getSelectionStatus() {
        return selectionStatus;
    }

    @Override
    public void setSelectionStatus(WikipediaSelectionStatus selectionStatus) {
        this.selectionStatus = selectionStatus;
    }

    @Override
    public String getSelectionCheckedOn() {
        return selectionCheckedOn;
    }

    @Override
    public void setSelectionCheckedOn(String selectionCheckedOn) {
        this.selectionCheckedOn = selectionCheckedOn;
    }

}
