package edu.asu.diging.cord19.explorer.core.service;

import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.model.task.Task;

public interface Exporter {

    boolean supports(ExportType type);

    Export export(Export export);

}