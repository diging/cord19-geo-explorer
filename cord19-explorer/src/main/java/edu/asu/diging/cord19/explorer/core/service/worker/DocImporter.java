package edu.asu.diging.cord19.explorer.core.service.worker;

import java.io.IOException;

import org.springframework.scheduling.annotation.Async;

public interface DocImporter {

    void run(String rootFolder, String taskId) throws IOException;

    void extractYears(String taskId);

    void extractLocations(String taskId) throws ClassCastException, ClassNotFoundException, IOException;

    void removeUnvalid(String taskId);

    void cleanAffiliations(String taskId, boolean reprocess);

    void selectLocationMatches(String taskId);

    void importMetadata(String taskId, String metadataFile);

}