package edu.asu.diging.cord19.explorer.core.service.arxiv;

public interface ArxivImporter {
    
    final static String ARXIV_TOTAL_RESULTS = "totalResults";
    final static String ARXIV_DOI = "doi";
    final static String ARXIV_JOURNAL = "journal_ref";
    final static String ARXIV_PRIMARY_CATEGORY = "primary_category";
    final static String ARXIV_COMMENT = "comment";
    final static String ARXIV_AFFILIATION = "affiliation";
    
    void importMetadata(String taskId, String searchTerm);

}