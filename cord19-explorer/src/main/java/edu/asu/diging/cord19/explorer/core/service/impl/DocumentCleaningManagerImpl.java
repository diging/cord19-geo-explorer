package edu.asu.diging.cord19.explorer.core.service.impl;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.service.DocumentCleaningManager;
import edu.asu.diging.cord19.explorer.core.service.worker.DocumentCleaner;

@Service
public class DocumentCleaningManagerImpl implements DocumentCleaningManager {

    @Autowired
    private TaskRepository taskRepo;
    
    @Autowired
    private DocumentCleaner cleaner;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.DocumentCleaningManager#startDuplicateRemoval()
     */
    @Override
    public String startDuplicateRemoval() {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);

        cleaner.removeDuplicatePmcPubs(task.getId());

        return task.getId();
    }
}
