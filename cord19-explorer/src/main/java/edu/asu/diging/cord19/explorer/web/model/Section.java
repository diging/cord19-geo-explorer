package edu.asu.diging.cord19.explorer.web.model;

import java.util.List;

import edu.asu.diging.cord19.explorer.core.model.Paragraph;

public class Section {

    private String title;
    private List<Paragraph> paragraphs;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }
    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }
}
