package edu.asu.diging.cord19.explorer.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.SpanImpl;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    defaultImpl=SpanImpl.class)
public interface Span {

	int getStart();

	void setStart(int start);

	int getEnd();

	void setEnd(int end);

	String getText();

	void setText(String text);

	String getRefId();

	void setRefId(String refId);

	void setMention(String mention);

	String getMention();

}