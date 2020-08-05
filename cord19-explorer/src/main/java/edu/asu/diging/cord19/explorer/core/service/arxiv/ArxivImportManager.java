package edu.asu.diging.cord19.explorer.core.service.arxiv;

import java.io.IOException;

public interface ArxivImportManager {

    String startImport(String searchTerm) throws IOException;

}