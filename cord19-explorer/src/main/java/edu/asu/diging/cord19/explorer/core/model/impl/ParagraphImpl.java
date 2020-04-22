package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.LocationMatch;
import edu.asu.diging.cord19.explorer.core.model.Paragraph;

public class ParagraphImpl implements Paragraph {

	private ObjectId id;
	private String text;
	@JsonProperty("cite_spans")
	private List<SpanImpl> citeSpans;
	@JsonProperty("ref_spans")
	private List<SpanImpl> refSpans;
	@JsonProperty("eq_spans")
	private List<SpanImpl> eqSpans;
	private String section;
	
	private List<LocationMatch> locationMatches;
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#getText()
	 */
	@Override
	public String getText() {
		return text;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#getCiteSpans()
	 */
	@Override
	public List<SpanImpl> getCiteSpans() {
		return citeSpans;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#setCiteSpans(java.util.List)
	 */
	@Override
	public void setCiteSpans(List<SpanImpl> citeSpans) {
		this.citeSpans = citeSpans;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#getRefSpans()
	 */
	@Override
	public List<SpanImpl> getRefSpans() {
		return refSpans;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#setRefSpans(java.util.List)
	 */
	@Override
	public void setRefSpans(List<SpanImpl> refSpans) {
		this.refSpans = refSpans;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#getEqSpans()
	 */
	@Override
	public List<SpanImpl> getEqSpans() {
		return eqSpans;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#setEqSpans(java.util.List)
	 */
	@Override
	public void setEqSpans(List<SpanImpl> eqSpans) {
		this.eqSpans = eqSpans;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#getSection()
	 */
	@Override
	public String getSection() {
		return section;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Paragraph#setSection(java.lang.String)
	 */
	@Override
	public void setSection(String section) {
		this.section = section;
	}
	
	@Override
	public List<LocationMatch> getLocationMatches() {
		return locationMatches;
	}
	@Override
	public void setLocationMatches(List<LocationMatch> locationMatches) {
		this.locationMatches = locationMatches;
	}
}
