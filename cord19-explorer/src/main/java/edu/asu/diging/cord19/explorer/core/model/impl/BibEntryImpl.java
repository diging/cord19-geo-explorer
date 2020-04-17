package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.BibEntry;

public class BibEntryImpl implements BibEntry {

	@JsonProperty("ref_id")
	private String refId;
	private String title;
	private List<PersonImpl> authors;
	private int year;
	private String venue;
	private String volume;
	private String issn;
	private String pages;
	@JsonProperty("other_ids")
	private Map<String, List<String>> otherIds;
	
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getRefId()
	 */
	@Override
	public String getRefId() {
		return refId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setRefId(java.lang.String)
	 */
	@Override
	public void setRefId(String refId) {
		this.refId = refId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getAuthors()
	 */
	@Override
	public List<PersonImpl> getAuthors() {
		return authors;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setAuthors(java.util.List)
	 */
	@Override
	public void setAuthors(List<PersonImpl> authors) {
		this.authors = authors;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getYear()
	 */
	@Override
	public int getYear() {
		return year;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setYear(int)
	 */
	@Override
	public void setYear(int year) {
		this.year = year;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getVenue()
	 */
	@Override
	public String getVenue() {
		return venue;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setVenue(java.lang.String)
	 */
	@Override
	public void setVenue(String venue) {
		this.venue = venue;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getVolume()
	 */
	@Override
	public String getVolume() {
		return volume;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setVolume(java.lang.String)
	 */
	@Override
	public void setVolume(String volume) {
		this.volume = volume;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getIssn()
	 */
	@Override
	public String getIssn() {
		return issn;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setIssn(java.lang.String)
	 */
	@Override
	public void setIssn(String issn) {
		this.issn = issn;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getPages()
	 */
	@Override
	public String getPages() {
		return pages;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setPages(java.lang.String)
	 */
	@Override
	public void setPages(String pages) {
		this.pages = pages;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#getOtherIds()
	 */
	@Override
	public Map<String, List<String>> getOtherIds() {
		return otherIds;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.BibEntry#setOtherIds(java.util.Map)
	 */
	@Override
	public void setOtherIds(Map<String, List<String>> otherIds) {
		this.otherIds = otherIds;
	}
	
}
