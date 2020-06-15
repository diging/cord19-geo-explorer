package edu.asu.diging.cord19.explorer.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.ExportRepository;
import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.model.export.impl.ExportImpl;
import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.service.Exporter;

@Service
public class ExportHandlerImpl {

    @Autowired
    private ExportRepository exportRepo;
    
    @Autowired
    private List<Exporter> exporters;

    @Async
    public void startExport(Task task, ExportType type) {
        ExportImpl export = new ExportImpl();
        export.setTask(task);
        export.setType(ExportType.CSV);
        export = exportRepo.save(export);
        
        for (Exporter ex : exporters) {
            if (ex.supports(type)) {
                ex.export(export);
                break;
            }
        }
        
        exportRepo.save(export);
    }
}
