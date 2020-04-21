package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;

import edu.asu.diging.cord19.explorer.core.model.impl.LocationMatchImpl;

public interface LocationMatch {

	String getLocationName();

	void setLocationName(String locationName);

	int getStart();

	void setStart(int start);

	int getEnd();

	void setEnd(int end);

	double getProbability();

	void setProbability(double probability);

	String getType();

	void setType(String type);

	void setSection(String section);

	String getSection();

}