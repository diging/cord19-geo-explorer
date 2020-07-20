package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;

public interface PublicationSearchProvider {

    List<PublicationImpl> searchPublicationTitles(String title);

}
