package edu.asu.diging.cord19.explorer.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.CategoryImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = CategoryImpl.class)
public interface Category {

    String getTerm();

    void setTerm(String term);

    String getLabel();

    void setLabel(String label);

    String getScheme();

    void setScheme(String scheme);

}