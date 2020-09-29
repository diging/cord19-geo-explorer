package edu.asu.diging.cord19.explorer.core.model.export.impl;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;

@Entity
public class ExportImpl implements Export {

    @Id
    @GeneratedValue(generator = "export_id_generator")
    @GenericGenerator(name = "export_id_generator", parameters = @Parameter(name = "prefix", value = "EXP"), strategy = "edu.asu.diging.cord19.explorer.core.data.impl.IdGenerator")
    private String id;
    
    @Enumerated(EnumType.STRING)
    private ExportType type;
    
    @OneToOne(targetEntity=TaskImpl.class)
    private Task task;
    
    private String filename;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ExportType getType() {
        return type;
    }

    @Override
    public void setType(ExportType type) {
        this.type = type;
    }
    
    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

}
