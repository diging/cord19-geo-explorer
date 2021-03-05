package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.List;

import edu.asu.diging.cord19.explorer.web.model.SearchType;


public interface SearchProvider {
    
    SearchType getSearchType();

    long getTotalResults(String title);

    List<?> search(String title, Long currentPage, Integer size);
    
}
