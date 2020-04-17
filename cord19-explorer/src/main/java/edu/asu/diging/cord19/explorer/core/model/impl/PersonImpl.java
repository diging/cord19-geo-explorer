package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.Person;

public class PersonImpl implements Person {

	private String first;
	private List<String> middle;
	private String last;
	private String suffix;
	private AffiliationImpl affiliation;
	private String email;
	
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getFirst()
	 */
	@Override
	public String getFirst() {
		return first;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#setFirst(java.lang.String)
	 */
	@Override
	public void setFirst(String first) {
		this.first = first;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getMiddle()
	 */
	@Override
	public List<String> getMiddle() {
		return middle;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#setMiddle(java.util.List)
	 */
	@Override
	public void setMiddle(List<String> middle) {
		this.middle = middle;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getLast()
	 */
	@Override
	public String getLast() {
		return last;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#setLast(java.lang.String)
	 */
	@Override
	public void setLast(String last) {
		this.last = last;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getSuffix()
	 */
	@Override
	public String getSuffix() {
		return suffix;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#setSuffix(java.lang.String)
	 */
	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getAffiliation()
	 */
	@Override
	public AffiliationImpl getAffiliation() {
		return affiliation;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#setAffiliation(java.util.List)
	 */
	@Override
	public void setAffiliation(AffiliationImpl affiliation) {
		this.affiliation = affiliation;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
