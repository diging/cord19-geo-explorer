package edu.asu.diging.cord19.explorer.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.GeometryImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = GeometryImpl.class)
public interface Geometry {

}