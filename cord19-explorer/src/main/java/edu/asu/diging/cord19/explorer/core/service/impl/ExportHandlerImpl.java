package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.ExportRepository;
import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.model.export.impl.ExportImpl;
import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.model.task.TaskStatus;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.service.Exporter;

@Service
public class ExportHandlerImpl implements ExportHandler {

    @Autowired
    private ExportRepository exportRepo;
    
    @Autowired
    private TaskRepository taskRepo;
    
    @Autowired
    private List<Exporter> exporters;
    
    @Value("${app.data.path}")
    private String exportPath;
    
    @Value("${export.folder.name}")
    private String exportFolder;

    @Override
    @Async
    public void startExport(Task task, ExportType type) {
        ExportImpl export = new ExportImpl();
        export.setTask(task);
        export.setType(ExportType.CSV);
        export = exportRepo.save(export);
        
        for (Exporter ex : exporters) {
            if (ex.supports(type)) {
                ex.export(export, getExportFolderPath());
                break;
            }
        }
        
        task.setDateEnded(OffsetDateTime.now());
        task.setStatus(TaskStatus.DONE);
        taskRepo.save((TaskImpl)task);
        exportRepo.save(export);
    }
    
    @Override
    public String getExportFolderPath() {
        return exportPath + File.separator + exportFolder;
    }
}
