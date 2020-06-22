package edu.asu.diging.cord19.explorer.core.model.task;

import java.time.OffsetDateTime;

public interface Task {

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

    void setType(TaskType type);

    TaskType getType();

}