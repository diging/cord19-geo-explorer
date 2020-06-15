package edu.asu.diging.cord19.explorer.core.service.impl;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;

@Service
public class ExportManagerImpl {

    @Autowired
    private TaskRepository taskRepo;
    
    public String startExport() {
        
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.EXPORT);
        task = taskRepo.save(task);
        
        
        return task.getId();
    }
    
}
