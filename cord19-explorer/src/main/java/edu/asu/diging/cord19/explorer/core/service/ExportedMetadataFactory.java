package edu.asu.diging.cord19.explorer.core.service;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.service.impl.ExportedMetadataEntry;

public interface ExportedMetadataFactory {

    ExportedMetadataEntry createEntry(Publication pub);

}