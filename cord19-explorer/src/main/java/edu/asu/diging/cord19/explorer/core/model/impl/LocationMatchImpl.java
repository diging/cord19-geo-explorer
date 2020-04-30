package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;

import org.bson.types.ObjectId;

import edu.asu.diging.cord19.explorer.core.model.LocationMatch;

public class LocationMatchImpl implements LocationMatch {

	private ObjectId id;
	private String locationName;
	private int start;
	private int end;
	private double probability;
	private String type;
	private String section;
	private List<WikipediaArticleImpl> wikipediaArticles;
	
	@Override
	public ObjectId getId() {
		return id;
	}
	@Override
	public void setId(ObjectId id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#getLocationName()
	 */
	@Override
	public String getLocationName() {
		return locationName;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#setLocationName(java.lang.String)
	 */
	@Override
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#getStart()
	 */
	@Override
	public int getStart() {
		return start;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#setStart(int)
	 */
	@Override
	public void setStart(int start) {
		this.start = start;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#getEnd()
	 */
	@Override
	public int getEnd() {
		return end;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#setEnd(int)
	 */
	@Override
	public void setEnd(int end) {
		this.end = end;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#getProbability()
	 */
	@Override
	public double getProbability() {
		return probability;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#setProbability(double)
	 */
	@Override
	public void setProbability(double probability) {
		this.probability = probability;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#getType()
	 */
	@Override
	public String getType() {
		return type;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.LocationMatch#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String getSection() {
		return section;
	}
	@Override
	public void setSection(String section) {
		this.section = section;
	}
	@Override
	public List<WikipediaArticleImpl> getWikipediaArticles() {
		return wikipediaArticles;
	}
	@Override
	public void setWikipediaArticles(List<WikipediaArticleImpl> wikipediaArticles) {
		this.wikipediaArticles = wikipediaArticles;
	}
	
}
