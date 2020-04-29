package edu.asu.diging.cord19.explorer.core.elastic.model.impl;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "wikipedia_1", type = "wikientry")
public class Wikientry {

	@Id
	private String id;
	
	private String title;
	@Field(name="short_description", type = FieldType.Text)
	private String shortDescription;
	@Field(type=FieldType.Text)
	private String content;
	@Field(type = FieldType.Keyword, includeInParent = true)
    private List<String> categories;
	
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
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	
}
