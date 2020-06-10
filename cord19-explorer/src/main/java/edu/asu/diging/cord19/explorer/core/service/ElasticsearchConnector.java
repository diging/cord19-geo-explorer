package edu.asu.diging.cord19.explorer.core.service;

import java.util.List;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;

public interface ElasticsearchConnector {

    Wikientry followRedirect(Wikientry entry);

    List<Wikientry> searchInTitle(String location);

}