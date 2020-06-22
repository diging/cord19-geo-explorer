package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.service.DocImporter;
import edu.asu.diging.cord19.explorer.core.service.DocumentImportManager;

@Service
public class DocumentImportManagerImpl implements DocumentImportManager {

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private DocImporter importer;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.service.impl.DocumentImportManager#
     * startImport(java.lang.String)
     */
    @Override
    public String startImport(String path) throws IOException {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);

        /*
         * Start async import
         */
        importer.run(path, task.getId());

        return task.getId();
    }

    @Override
    public String startYearExtraction() {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);
        

        importer.extractYears(task.getId());

        return task.getId();
    }

    @Override
    public String startLocationExtraction() throws ClassCastException, ClassNotFoundException, IOException {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);
        
        importer.extractLocations(task.getId());

        return task.getId();
    }

    @Override
    public String startLocationMatchCleaning() {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task = taskRepo.save(task);

        importer.removeUnvalid(task.getId());

        return task.getId();
    }
    
    @Override
    public String startLocationMatchSelection() {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);

        importer.selectLocationMatches(task.getId());

        return task.getId();
    }
    
    @Override
    public String startAffiliationCleaning(boolean reprocess) {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);

        importer.cleanAffiliations(task.getId(), reprocess);

        return task.getId();
    }
}
