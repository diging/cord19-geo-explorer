package edu.asu.diging.cord19.explorer.core.model.task;

import java.time.OffsetDateTime;

import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskStatus;

public interface ImportTask {

    String getId();

    void setId(String id);

    long getProcessed();

    void setProcessed(long processed);

    OffsetDateTime getDateStarted();

    void setDateStarted(OffsetDateTime dateStarted);

    OffsetDateTime getDateEnded();

    void setDateEnded(OffsetDateTime dateEnded);

    void setStatus(TaskStatus status);

    TaskStatus getStatus();

}