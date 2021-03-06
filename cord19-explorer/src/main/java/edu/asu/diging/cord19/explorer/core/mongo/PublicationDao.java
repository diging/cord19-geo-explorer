package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

import edu.asu.diging.cord19.explorer.core.mongo.impl.AffiliationPaperAggregationOutput;

public interface PublicationDao {

    List<String> getCountries();

    List<Integer> getYears();

    List<String> getCountriesInText();

    long getAffiliationCount();

    long getPublicationCount();

    long getYearCount();

    List<String> getJournals();

    long getJournalCount();

    List<String> getDistinctAffiliations();

    long getDistinctAffiliationCount();

    long getCountriesInTextCount();

    long getCountOfPublicationsWithLocation();
    
    List<String> getDistinctAffiliationsTop();

    List<String> getCountriesTop();

    List<String> getCountriesInTextTop();

    List<AffiliationPaperAggregationOutput> getAffiliationsAndArticles(long start, long pageSize);

    long getTotalAffiliation();

}