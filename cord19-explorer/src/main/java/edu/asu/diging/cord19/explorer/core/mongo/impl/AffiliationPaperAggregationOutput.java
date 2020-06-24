package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.List;

public class AffiliationPaperAggregationOutput {

    private String id;
    private List<String> paperId;
    private String wiki;
    private String coord;
    private String status;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getPaperId() {
        return paperId;
    }
    public void setPaperId(List<String> paperId) {
        this.paperId = paperId;
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
    
}
