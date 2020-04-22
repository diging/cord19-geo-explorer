package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.SpanImpl;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    defaultImpl=ParagraphImpl.class)
public interface Paragraph {

	String getText();

	void setText(String text);

	List<SpanImpl> getCiteSpans();

	void setCiteSpans(List<SpanImpl> citeSpans);

	List<SpanImpl> getRefSpans();

	void setRefSpans(List<SpanImpl> refSpans);

	List<SpanImpl> getEqSpans();

	void setEqSpans(List<SpanImpl> eqSpans);

	String getSection();

	void setSection(String section);

	void setLocationMatches(List<LocationMatch> locationMatches);

	List<LocationMatch> getLocationMatches();
}