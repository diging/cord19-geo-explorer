package edu.asu.diging.cord19.explorer.core.service;

public interface ArxivImporter {
    
    final static String ARXIV_TOTAL_RESULTS = "totalResults";

    void importMetadata(String searchTerm);

}