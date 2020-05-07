package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = PersonImpl.class)
public interface Person {

    String getFirst();

    void setFirst(String first);

    List<String> getMiddle();

    void setMiddle(List<String> middle);

    String getLast();

    void setLast(String last);

    String getSuffix();

    void setSuffix(String suffix);

    Affiliation getAffiliation();

    void setAffiliation(Affiliation affiliation);

    String getEmail();

    void setEmail(String email);

}