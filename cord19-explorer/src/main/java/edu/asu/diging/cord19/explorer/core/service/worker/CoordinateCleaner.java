package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;

public interface CoordinateCleaner {

    Publication cleanCoordinates(PublicationImpl pub);

    void calculateCountryStats();

    void startCleaningCoordinates(String taskId);

}