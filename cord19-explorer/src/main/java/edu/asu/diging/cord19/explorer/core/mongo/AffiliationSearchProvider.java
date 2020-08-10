package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

public interface AffiliationSearchProvider {

    long searchResultSize(String title);
    
    List<String> getRequestedPage(String title, Long currentPage, Integer size);
}
