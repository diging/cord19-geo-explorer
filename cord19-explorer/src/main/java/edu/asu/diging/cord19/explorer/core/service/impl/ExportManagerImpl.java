package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.ExportRepository;
import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.model.export.impl.ExportImpl;
import edu.asu.diging.cord19.explorer.core.model.task.TaskType;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.service.ExportManager;

@Service
public class ExportManagerImpl implements ExportManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskRepository taskRepo;
    
    @Autowired
    private ExportHandler exportHandler;
    
    @Autowired
    private ExportRepository exportRepo;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.ExportManager#startExport()
     */
    @Override
    public String startExport() {
        
        TaskImpl task = new TaskImpl();
        task.setDateStarted(OffsetDateTime.now());
        task.setProcessed(0);
        task.setType(TaskType.EXPORT);
        task = taskRepo.save(task);
        
        exportHandler.startExport(task, ExportType.CSV);
        
        return task.getId();
    }
    
    @Override
    public Page<ExportImpl> listExports(Pageable pageable) {
        return exportRepo.findAll(pageable);
    }
    
    @Override
    public Export get(String id) {
        Optional<ExportImpl> export = exportRepo.findById(id);
        if (export.isPresent()) {
            return export.get();
        }
        return null;
    }
    
    @Override
    public byte[] getExportFile(Export export) {
        String filepath = exportHandler.getExportFolderPath() + File.separator + export.getFilename();
        try {
            return IOUtils.toByteArray(new FileInputStream(new File(filepath)));
        } catch (IOException e) {
            logger.error("Could not read file.", e);
        }
        return null;
    }
}
