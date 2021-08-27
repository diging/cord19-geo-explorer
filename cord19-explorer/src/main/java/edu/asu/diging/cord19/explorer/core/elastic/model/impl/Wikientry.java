package edu.asu.diging.cord19.explorer.core.elastic.model.impl;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "#{@indexName}", type = "wikientry")
public class Wikientry {

    @Id
    private String id;

    private String title;
    @Field(name = "short_description", type = FieldType.Text)
    private String shortDescription;
    @Field(type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Text)
    private String complete_text;
    @Field(type = FieldType.Text)
    private String coordinates;
    @Field(type = FieldType.Text, includeInParent = true)
    private List<String> categories;
    
    private String redirectsTo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getComplete_text() {
        return complete_text;
    }

    public void setComplete_text(String complete_text) {
        this.complete_text = complete_text;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getRedirectsTo() {
        return redirectsTo;
    }

    public void setRedirectsTo(String redirectsTo) {
        this.redirectsTo = redirectsTo;
    }

}
