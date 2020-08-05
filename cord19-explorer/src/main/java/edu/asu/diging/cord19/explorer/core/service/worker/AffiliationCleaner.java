package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.Publication;

public interface AffiliationCleaner {

    void processAffiliations(Publication pub);

}