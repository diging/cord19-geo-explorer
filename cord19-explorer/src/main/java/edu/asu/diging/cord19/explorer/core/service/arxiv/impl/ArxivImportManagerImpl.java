package edu.asu.diging.cord19.explorer.core.service.arxiv.impl;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.service.arxiv.ArxivImportManager;
import edu.asu.diging.cord19.explorer.core.service.arxiv.ArxivImporter;

@Service
public class ArxivImportManagerImpl implements ArxivImportManager {

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private ArxivImporter importer;

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.ArxivImportManager#startImport(java.lang.String)
     */
    @Override
    public String startImport(String searchTerm) throws IOException {
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.IMPORT);
        task = taskRepo.save(task);

        importer.importMetadata(task.getId(), searchTerm);

        return task.getId();
    }
}
