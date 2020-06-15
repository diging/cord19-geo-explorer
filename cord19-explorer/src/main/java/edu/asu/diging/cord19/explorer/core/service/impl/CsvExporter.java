package edu.asu.diging.cord19.explorer.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.Exporter;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties"})
public class CsvExporter implements Exporter {
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @Value("${app.data.path}")
    private String exportPath;
    
    @Value("${export.folder.name}")
    private String exportFolder;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.Exporter#supports(edu.asu.diging.cord19.explorer.core.model.export.ExportType)
     */
    @Override
    public boolean supports(ExportType type) {
        return type == ExportType.CSV;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.Exporter#export(edu.asu.diging.cord19.explorer.core.model.task.Task)
     */
    @Override
    public Export export(Export export) {
        
    }
}
