package edu.asu.diging.cord19.explorer.core.service.worker;

import java.io.IOException;
import java.util.List;

import edu.asu.diging.cord19.explorer.core.model.LocationMatch;
import edu.asu.diging.cord19.explorer.core.model.Publication;

public interface TextLocationMatcher {

    List<LocationMatch> findLocations(Publication pub) throws ClassCastException, ClassNotFoundException, IOException;

    boolean isValid(LocationMatch match);

    void selectArticle(LocationMatch match);

}