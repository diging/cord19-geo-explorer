package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.List;
import java.util.Set;

import edu.asu.diging.pubmeta.util.model.impl.PersonImpl;

public class AffiliationPaperAggregationOutput {

    private String id;
    private Set<String> paperId;
    private String settlement;
    private String country;
    private String wiki;
    private String coord;
    private String status;
    private String locType;
    private Set<PersonImpl> authors;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Set<String> getPaperId() {
        return paperId;
    }
    public void setPaperId(Set<String> paperId) {
        this.paperId = paperId;
    }
    public String getSettlement() {
        return settlement;
    }
    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getWiki() {
        return wiki;
    }
    public void setWiki(String wiki) {
        this.wiki = wiki;
    }
    public String getCoord() {
        return coord;
    }
    public void setCoord(String coord) {
        this.coord = coord;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getLocType() {
        return locType;
    }
    public void setLocType(String locType) {
        this.locType = locType;
    }
    public Set<PersonImpl> getAuthors() {
        return authors;
    }
    public void setAuthors(Set<PersonImpl> authors) {
        this.authors = authors;
    }
    
}
