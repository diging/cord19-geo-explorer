package edu.asu.diging.cord19.explorer.core.model.impl;

public class WikipediaArticleImpl {

    private String title;
    private String completeText;
    private String coordinates;
    private LocationType locationType;
    private String selectedOn;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompleteText() {
        return completeText;
    }

    public void setCompleteText(String completeText) {
        this.completeText = completeText;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getSelectedOn() {
        return selectedOn;
    }

    public void setSelectedOn(String selectedOn) {
        this.selectedOn = selectedOn;
    }

}
