package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTaskImpl;
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
        ImportTaskImpl task = new ImportTaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task = taskRepo.save(task);

        /*
         * Start async import
         */
        importer.run(path, task.getId());

        return task.getId();
    }

    @Override
    public String startYearExtraction() {
        ImportTaskImpl task = new ImportTaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task = taskRepo.save(task);

        importer.extractYears(task.getId());

        return task.getId();
    }

    @Override
    public String startLocationExtraction() throws ClassCastException, ClassNotFoundException, IOException {
        ImportTaskImpl task = new ImportTaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task = taskRepo.save(task);

        importer.extractLocations(task.getId());

        return task.getId();
    }

    @Override
    public String startLocationMatchCleaning() {
        ImportTaskImpl task = new ImportTaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task = taskRepo.save(task);

        importer.removeUnvalid(task.getId());

        return task.getId();
    }
    
    @Override
    public String startAffiliationCleaning(boolean reprocess) {
        ImportTaskImpl task = new ImportTaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task = taskRepo.save(task);

        importer.cleanAffiliations(task.getId(), reprocess);

        return task.getId();
    }
}
