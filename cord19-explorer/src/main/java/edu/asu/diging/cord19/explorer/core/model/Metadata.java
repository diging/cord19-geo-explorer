package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.AffiliationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.MetadataImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = MetadataImpl.class)
public interface Metadata {

    String getTitle();

    void setTitle(String title);

    List<PersonImpl> getAuthors();

    void setAuthors(List<PersonImpl> authors);

    void setAffiliationCountries(List<AffiliationImpl> affiliationCountries);

    List<AffiliationImpl> getAffiliationCountries();

}