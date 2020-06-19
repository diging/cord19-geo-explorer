package edu.asu.diging.cord19.explorer.core.model.export;

import edu.asu.diging.cord19.explorer.core.model.task.Task;

public interface Export {

    void setTask(Task task);

    Task getTask();

    void setType(ExportType type);

    ExportType getType();

    void setId(String id);

    String getId();

    void setFilename(String filename);

    String getFilename();

}