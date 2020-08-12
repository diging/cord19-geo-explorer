package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.Publication;

public interface DimensionsMapper {

    void map(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub);

}