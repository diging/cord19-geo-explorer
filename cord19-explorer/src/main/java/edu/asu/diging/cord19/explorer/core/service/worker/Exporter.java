package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;

public interface Exporter {

    boolean supports(ExportType type);

    void export(Export export, String exportFolder);

}