package edu.asu.diging.cord19.explorer.core.service;

import java.io.IOException;

public interface DocumentImportManager {

    String startImport(String path) throws IOException;

    String startYearExtraction();

    String startLocationExtraction() throws ClassCastException, ClassNotFoundException, IOException;

    String startLocationMatchCleaning();

    String startAffiliationCleaning();

}