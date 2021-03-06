package edu.asu.diging.cord19.explorer.core.service.worker;

import org.springframework.scheduling.annotation.Async;

import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.model.task.Task;

public interface ExportHandler {

    void startExport(Task task, ExportType type);

    String getExportFolderPath();

}