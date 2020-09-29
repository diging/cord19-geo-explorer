package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;

import edu.asu.diging.cord19.explorer.core.model.Affiliation;
import edu.asu.diging.cord19.explorer.core.model.Person;

public class PersonImpl implements Person {

    private String first;
    private List<String> middle;
    private String last;
    private String suffix;
    private AffiliationImpl affiliation;
    private String email;
    private String name;
    private String uri;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getFirst()
     */
    @Override
    public String getFirst() {
        return first;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Person#setFirst(java.lang.
     * String)
     */
    @Override
    public void setFirst(String first) {
        this.first = first;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getMiddle()
     */
    @Override
    public List<String> getMiddle() {
        return middle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Person#setMiddle(java.util.
     * List)
     */
    @Override
    public void setMiddle(List<String> middle) {
        this.middle = middle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getLast()
     */
    @Override
    public String getLast() {
        return last;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#setLast(java.lang.
     * String)
     */
    @Override
    public void setLast(String last) {
        this.last = last;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getSuffix()
     */
    @Override
    public String getSuffix() {
        return suffix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Person#setSuffix(java.lang.
     * String)
     */
    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getAffiliation()
     */
    @Override
    public Affiliation getAffiliation() {
        return affiliation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Person#setAffiliation(java.
     * util.List)
     */
    @Override
    public void setAffiliation(Affiliation affiliation) {
        this.affiliation = (AffiliationImpl)affiliation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Person#getEmail()
     */
    @Override
    public String getEmail() {
        return email;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Person#setEmail(java.lang.
     * String)
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        
        String fName = first != null ? first : "";
        String lName = last != null ? last : "";
        return fName + " " + lName;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

}
