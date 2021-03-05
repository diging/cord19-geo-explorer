package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.Publication;

public interface CoordinateCleaner {

	void cleanCoordinates(String taskId);

    Publication cleanCoordinatesImport(Publication pub);
    
    void calculateCountryStats();

    void startCleaningCoordinates(String taskId);

}