package edu.asu.diging.cord19.explorer.core.model.task.impl;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.model.task.TaskStatus;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;

@Entity(name="ImportTaskImpl")
public class TaskImpl implements Task {

    @Id
    @GeneratedValue(generator = "task_id_generator")
    @GenericGenerator(name = "task_id_generator", parameters = @Parameter(name = "prefix", value = "TASK"), strategy = "edu.asu.diging.cord19.explorer.core.data.impl.IdGenerator")
    private String id;

    private long processed;
    private OffsetDateTime dateStarted;
    private OffsetDateTime dateEnded;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskType type;
     

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#setId(java.
     * lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#getProcessed()
     */
    @Override
    public long getProcessed() {
        return processed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#setProcessed(
     * long)
     */
    @Override
    public void setProcessed(long processed) {
        this.processed = processed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#getDateStarted
     * ()
     */
    @Override
    public OffsetDateTime getDateStarted() {
        return dateStarted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#setDateStarted
     * (java.time.OffsetDateTime)
     */
    @Override
    public void setDateStarted(OffsetDateTime dateStarted) {
        this.dateStarted = dateStarted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#getDateEnded()
     */
    @Override
    public OffsetDateTime getDateEnded() {
        return dateEnded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTask#setDateEnded(
     * java.time.OffsetDateTime)
     */
    @Override
    public void setDateEnded(OffsetDateTime dateEnded) {
        this.dateEnded = dateEnded;
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public void setType(TaskType type) {
        this.type = type;
    }

}
