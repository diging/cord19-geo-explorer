package edu.asu.diging.cord19.explorer.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;



@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = CountriesImpl.class)
public interface Countries {

}