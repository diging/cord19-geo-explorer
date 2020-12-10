package edu.asu.diging.cord19.explorer.core.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.CoordinateCleaningManager;
import edu.asu.diging.cord19.explorer.core.service.worker.CoordinateCleaner;
;

@Component
public class CoordinateCleaningManagerImpl implements CoordinateCleaningManager {
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @Autowired
	private TaskRepository taskRepo;
	
	@Autowired
    private CoordinateCleaner cleaner;
    
    @Override
    @Async
    public String startCleaningCoordinates() {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);

        /*
         * Start async import
         */
        cleaner.cleanCoordinates(task.getId());
        return task.getId();

    }

    
}