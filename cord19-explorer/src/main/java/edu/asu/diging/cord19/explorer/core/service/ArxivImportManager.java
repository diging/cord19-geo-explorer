package edu.asu.diging.cord19.explorer.core.service;

import java.io.IOException;

public interface ArxivImportManager {

    String startImport(String searchTerm) throws IOException;

}