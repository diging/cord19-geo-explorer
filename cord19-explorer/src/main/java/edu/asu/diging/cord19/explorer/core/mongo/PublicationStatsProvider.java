package edu.asu.diging.cord19.explorer.core.mongo;

public interface PublicationStatsProvider {

    long getAuthorCount();

    long getPaperWithAuthorAffiliationCount();

    long getPaperWithoutAuthorAffiliationCount();

}