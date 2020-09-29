package edu.asu.diging.cord19.explorer.core.mongo;

public interface PublicationStatsProvider {

    long getAuthorCount();

    long getPaperWithAuthorAffiliationCount();

    long getPaperWithoutAuthorAffiliationCount();

    long getAuthorWithAffiliationAndWikiarticleCount();

    long getAuthorWithAffiliationCount();

    long getPaperWithAtLeastOneAffiliationAndWikiarticleCount();

    long getAuthorsWithAffiliationAndIncorrectWikiarticleCount();

    long getAuthorsWithAffiliationAndCorrectRegionWikiarticleCount();

    long getPapersWithAffiliationAndIncorrectWikiarticleCount();

}