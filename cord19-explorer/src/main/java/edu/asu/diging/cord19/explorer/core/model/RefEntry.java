package edu.asu.diging.cord19.explorer.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.RefEntryImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = RefEntryImpl.class)
public interface RefEntry {

    String getText();

    void setText(String text);

    String getType();

    void setType(String type);

    void setLatex(String latex);

    String getLatex();

    void setHtml(String html);

    String getHtml();

}