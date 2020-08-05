package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Component;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import edu.asu.diging.cord19.explorer.core.data.ExportRepository;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.ExportType;
import edu.asu.diging.cord19.explorer.core.model.export.impl.ExportImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.service.worker.ExportedMetadataFactory;
import edu.asu.diging.cord19.explorer.core.service.worker.Exporter;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties"})
public class CsvExporter implements Exporter {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private ExportedMetadataFactory factory;
    
    @Autowired
    private ExportRepository repo;
    
    @Value("${export.file.name.prefix}")
    private String exportFilenamePrefix;
    
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
    public void export(Export export, String exportFolder) {
        
        File file = new File(exportFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        
        String filename = exportFilenamePrefix + export.getId() + ".csv";
        export.setFilename(filename);
        repo.save((ExportImpl)export);
        
        Writer writer = null;
        try {
            writer = new FileWriter(file.getAbsolutePath() + File.separator + filename);
        } catch (IOException e) {
            logger.error("Could not write csv file.", e);
            return;
        }
        StatefulBeanToCsv<ExportedMetadataEntry> beanToCsv = new StatefulBeanToCsvBuilder<ExportedMetadataEntry>(writer).build();
        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query().noCursorTimeout(),
                PublicationImpl.class)) {
            while (docs.hasNext()) {
                Publication pub = docs.next();
                try {
                    beanToCsv.write(factory.createEntry(pub));
                } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                    logger.error("Can't write csv.", e);
                }
            }
        }
    }
}
