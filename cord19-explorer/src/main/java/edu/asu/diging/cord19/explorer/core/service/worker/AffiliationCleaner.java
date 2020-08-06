package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.Affiliation;
import edu.asu.diging.cord19.explorer.core.model.Publication;

public interface AffiliationCleaner {

    void processAuthorAffiliations(Publication pub);

    void processAffiliation(Affiliation affiliation);

}