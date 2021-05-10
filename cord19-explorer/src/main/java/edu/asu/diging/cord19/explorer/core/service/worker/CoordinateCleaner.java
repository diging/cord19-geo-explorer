package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.Publication;

public interface CoordinateCleaner {

    Publication cleanCoordinatesImport(Publication pub);

    void calculateCountryStats();

    void startCleaningCoordinates(String taskId);

}