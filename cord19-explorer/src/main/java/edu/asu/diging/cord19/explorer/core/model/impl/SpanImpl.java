package edu.asu.diging.cord19.explorer.core.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.Span;

public class SpanImpl implements Span {

	private int start;
	private int end;
	private String text;
	private String mention;
	@JsonProperty("ref_id")
	private String refId;
	
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#getStart()
	 */
	@Override
	public int getStart() {
		return start;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#setStart(int)
	 */
	@Override
	public void setStart(int start) {
		this.start = start;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#getEnd()
	 */
	@Override
	public int getEnd() {
		return end;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#setEnd(int)
	 */
	@Override
	public void setEnd(int end) {
		this.end = end;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#getText()
	 */
	@Override
	public String getText() {
		return text;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String getMention() {
		return mention;
	}
	@Override
	public void setMention(String mention) {
		this.mention = mention;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#getRefId()
	 */
	@Override
	public String getRefId() {
		return refId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Span#setRefId(java.lang.String)
	 */
	@Override
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
}
