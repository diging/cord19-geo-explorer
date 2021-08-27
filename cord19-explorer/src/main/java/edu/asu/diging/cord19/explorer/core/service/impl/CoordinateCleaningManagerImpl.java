package edu.asu.diging.cord19.explorer.core.service.impl;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.service.CoordinateCleaningManager;
import edu.asu.diging.cord19.explorer.core.service.worker.CoordinateCleaner;

@Component
public class CoordinateCleaningManagerImpl implements CoordinateCleaningManager {

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
        task.setType(TaskType.CLEAN);
        task = taskRepo.save(task);

        cleaner.startCleaningCoordinates(task.getId());
        return task.getId();

    }

}