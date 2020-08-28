package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

import edu.asu.diging.cord19.explorer.core.mongo.impl.AffiliationPaperAggregationOutput;

public interface AffiliationSearchProvider {

    List<AffiliationPaperAggregationOutput> getRequestedPage(String title, Long currentPage, Integer size);

    long searchResultSize(String title);
}
