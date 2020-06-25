package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.LocationMatch;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.model.task.TaskStatus;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.AffiliationCleaner;
import edu.asu.diging.cord19.explorer.core.service.DocImporter;
import edu.asu.diging.cord19.explorer.core.service.TextLocationMatcher;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties",
        "classpath:/states.txt" })
public class DocImporterImpl implements DocImporter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${metadata.filename}")
    private String metadataFilename;

    @Value("${metadata.filename.msid}")
    private String metadataFilenameMsId;

    @Value("${app.data.path}")
    private String appdataPath;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private PublicationRepository pubRepo;

    @Autowired
    private AffiliationCleaner affCleaner;

    @Autowired
    private TextLocationMatcher locMatcher;

    @Autowired
    private MongoTemplate mongoTemplate;

    private ObjectMapper mapper;

    @PostConstruct
    public void init() {
        mapper = new ObjectMapper();
    }

    @Override
    @Async
    public void run(String rootFolder, String taskId) throws IOException {
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);

        File file = new File(appdataPath + File.separator + "logs" + File.separator + task.getId() + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error("Could not create outfile.", e);
            return;
        }

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            logger.error("Could not create outfile.", e);
            return;
        }

        long counter = 1;
        try (Stream<Path> paths = Files.walk(Paths.get(rootFolder))) {
            Iterator<Path> pathIterator = paths.iterator();
            while (pathIterator.hasNext()) {
                Path path = pathIterator.next();
                if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) && !path.getFileName().startsWith(".")) {
                    try {
                        storeFile(path.toFile(), task, writer);
                        logger.info("Processing file #" + counter);
                    } catch (JsonParseException e) {
                        logger.error("Could not store file " + path.getFileName(), e);
                    } catch (JsonMappingException e) {
                        logger.error("Could not store file " + path.getFileName(), e);
                    } catch (IOException e) {
                        logger.error("Could not store file " + path.getFileName(), e);
                    } catch (ClassCastException e) {
                        logger.error("Could not store file " + path.getFileName(), e);
                    } catch (ClassNotFoundException e) {
                        logger.error("Could not store file " + path.getFileName(), e);
                    }
                    counter++;
                }
            }
            ;
        }

        writer.close();

        parseMetadataCsv(rootFolder + File.separator + metadataFilename);

        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

    private void parseMetadataCsv(String metadataFilename) throws FileNotFoundException {
        File metadataFile = new File(metadataFilename);

        CsvToBean<MetadataEntry> bean = new CsvToBeanBuilder<MetadataEntry>(new FileReader(metadataFile)).withType(MetadataEntry.class)
                .withIgnoreLeadingWhiteSpace(true).withSeparator(',').build();

        Iterator<MetadataEntry> it = bean.iterator();
        while (it.hasNext()) {
            MetadataEntry entry = it.next();
            logger.debug("Parsing metadata entry: "  + entry.getCord_uid());
            PublicationImpl pub = null;
            if (entry.getSha() != null && !entry.getSha().isEmpty()) {
                pub = pubRepo.findFirstByPaperId(entry.getSha());
            }
            if (pub == null && entry.getPmcid() != null && !entry.getPmcid().isEmpty()) {
                pub = pubRepo.findFirstByPaperId(entry.getPmcid());
            }

            if (pub != null) {
                pub.setHasPdfParse(entry.getPdfJsonFiles() != null && !entry.getPdfJsonFiles().trim().isEmpty());
                pub.setPdfJsonFiles(entry.getPdfJsonFiles());
                pub.setCordId(entry.getCord_uid());
                pub.setDoi(entry.getDoi());
                pub.setHasPmcXmlParse(entry.getPmcJsonFiles() != null && entry.getPmcJsonFiles().trim().isEmpty());
                pub.setPmcJsonFiles(entry.getPmcJsonFiles());
                pub.setJournal(entry.getJournal());
                pub.setLicense(entry.getLicense());
                pub.setMsAcademicPaperId(entry.getMsAcademicPaperId());
                pub.setPmcid(entry.getPmcid());
                pub.setPublishTime(entry.getPublishTime());
                pub.setPubmedId(entry.getPubmed_id());
                pub.setSha(entry.getSha());
                pub.setSourceX(entry.getSourceX());
                pub.setUrl(entry.getUrl());
                pub.setWhoCovidence(entry.getWhoCov());
                pub.setArxivId(entry.getArxivId());
                extractYear(pub);
                pubRepo.save(pub);
            }
        }
        
    }

    private void storeFile(File f, Task task, BufferedWriter writer)
            throws JsonParseException, JsonMappingException, IOException, ClassCastException, ClassNotFoundException {
        if (!f.getName().endsWith(".json")) {
            return;
        }
        PublicationImpl publication = mapper.readValue(f, PublicationImpl.class);

        // do not reimport existing publications
        Publication storedPub = pubRepo.findFirstByPaperId(publication.getPaperId());
        if (storedPub != null) {
            return;
        }

        List<LocationMatch> invalidMatches = locMatcher.findLocations(publication);
        processLocationMatches(publication);
        for (LocationMatch match : invalidMatches) {
            writer.newLine();
            writer.write(match.getLocationName());
            writer.flush();
        }
        affCleaner.processAffiliations(publication);
        pubRepo.save(publication);
        task.setProcessed(task.getProcessed() + 1);
        logger.debug("Stored: " + publication.getPaperId());
    }

    @Override
    @Async
    public void importMetadata(String taskId, String metadataFile) {
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);
        
        try {
            parseMetadataCsv(metadataFile);
            task.setStatus(TaskStatus.DONE);
        } catch (FileNotFoundException e) {
            logger.error("Could not parse metadata file.", e);
            task.setStatus(TaskStatus.FAILURE);
        }
        
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

    @Override
    @Async
    public void cleanAffiliations(String taskId, boolean reprocess) {
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);

        Query query;
        if (reprocess) {
            query = new Query();
        } else {
            query = new Query(Criteria.where("metadata.authors.affiliation.selectedWikiarticle").exists(false));
        }

        long total = mongoTemplate.count(query, PublicationImpl.class);
        long counter = 1;
        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(query.noCursorTimeout(),
                PublicationImpl.class)) {
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                logger.info(String.format("Cleaning affiliations %d of %d for: %s", counter, total, pub.getPaperId()));
                affCleaner.processAffiliations(pub);
                pubRepo.save(pub);
                counter++;
            }
        }

        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

    @Override
    @Async
    public void selectLocationMatches(String taskId) {
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);

        Query query = new Query();
        long total = mongoTemplate.count(query, PublicationImpl.class);
        long counter = 1;
        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(query.noCursorTimeout(),
                PublicationImpl.class)) {
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                logger.info(String.format("Cleaning affiliations %d of %d for: %s", counter, total, pub.getPaperId()));
                processLocationMatches(pub);
                pubRepo.save(pub);
                counter++;
            }
        }

        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

    private void processLocationMatches(Publication pub) {
        if (pub.getBodyText() == null) {
            return;
        }
        for (ParagraphImpl paragraph : pub.getBodyText()) {
            if (paragraph.getLocationMatches() == null) {
                continue;
            }
            for (LocationMatch match : paragraph.getLocationMatches()) {
                if (match != null) {
                    locMatcher.selectArticle(match);
                }
            }
        }
    }

    @Override
    @Async
    public void extractYears(String taskId) {
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);

        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query(), PublicationImpl.class)) {
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                extractYear(pub);
                pubRepo.save(pub);
            }
        }

        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

    private void extractYear(Publication pub) {
        if (pub.getPublishTime() != null && !pub.getPublishTime().isEmpty()) {
            if (pub.getPublishTime().contains("-")) {
                LocalDate date = LocalDate.parse(pub.getPublishTime());
                pub.setPublishYear(date.getYear());
            } else if (pub.getPublishTime().length() == 4) {
                try {
                    pub.setPublishYear(new Integer(pub.getPublishTime()));
                } catch (NumberFormatException e) {
                    // well too bad
                    logger.error("Couldn't parse date for " + pub.getPaperId() + ": " + pub.getPublishTime());
                }
            }
        }
    }

    @Override
    @Async
    public void extractLocations(String taskId) throws ClassCastException, ClassNotFoundException, IOException {
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);

        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query(), PublicationImpl.class)) {
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                locMatcher.findLocations(pub);
                pubRepo.save(pub);
            }
        }

        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

    @Override
    @Async
    public void removeUnvalid(String taskId) {
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);

        File file = new File(appdataPath + File.separator + "logs" + File.separator + task.getId() + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error("Could not create outfile.", e);
            return;
        }

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            logger.error("Could not create outfile.", e);
            return;
        }
        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query(), PublicationImpl.class)) {
            Set<String> nonValidMatches = new HashSet<>();
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                logger.debug("Cleaning: " + pub.getPaperId());
                for (ParagraphImpl para : pub.getBodyText()) {
                    Iterator<LocationMatch> it = para.getLocationMatches().iterator();
                    while (it.hasNext()) {
                        LocationMatch match = it.next();
                        if (nonValidMatches.contains(match.getLocationName()) || !locMatcher.isValid(match)) {
                            try {
                                nonValidMatches.add(match.getLocationName());
                                writer.newLine();
                                writer.write(match.getLocationName());
                                writer.flush();
                            } catch (IOException e) {
                                logger.error("Could not write to file.", e);
                            }
                            it.remove();
                        }
                    }
                }
                pubRepo.save(pub);
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            logger.error("Could not close file.", e);
        }

        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

}
