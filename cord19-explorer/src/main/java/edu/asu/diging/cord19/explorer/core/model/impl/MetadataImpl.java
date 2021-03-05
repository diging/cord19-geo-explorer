package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;

import org.springframework.data.mongodb.core.index.TextIndexed;

import edu.asu.diging.cord19.explorer.core.model.Metadata;

public class MetadataImpl implements Metadata {
    @TextIndexed
    private String title;
    private List<PersonImpl> authors;
    private List<AffiliationImpl> affiliationCountries;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#getTitle()
     */
    @Override
    public String getTitle() {
        return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Metadata#setTitle(java.lang.
     * String)
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#getAuthors()
     */
    @Override
    public List<PersonImpl> getAuthors() {
        return authors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.Metadata#setAuthors(java.util.
     * List)
     */
    @Override
    public void setAuthors(List<PersonImpl> authors) {
        this.authors = authors;
    }

    @Override
    public List<AffiliationImpl> getAffiliationCountries() {
        return affiliationCountries;
    }

    @Override
    public void setAffiliationCountries(List<AffiliationImpl> affiliationCountries) {
        this.affiliationCountries = affiliationCountries;
    }

}
