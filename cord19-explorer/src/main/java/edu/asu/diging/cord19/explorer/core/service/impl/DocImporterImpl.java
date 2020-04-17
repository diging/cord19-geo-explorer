package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.task.ImportTask;
import edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTaskImpl;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskStatus;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.DocImporter;

@Component
@PropertySource({"classpath:config.properties", "${appConfigFile:classpath:}/app.properties"})
public class DocImporterImpl implements DocImporter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${metadata.filename}")
	private String metadataFilename;
	
	@Autowired
	private TaskRepository taskRepo;
	
	@Autowired
	private PublicationRepository pubRepo;

	@Override
	@Async
	public void run(String rootFolder, String taskId) throws IOException {
		Optional<ImportTaskImpl> optional = taskRepo.findById(taskId);
		if (!optional.isPresent()) {
			return;
			// FIXME: mark as failure
		}
		
		ImportTask task = optional.get();
		task.setStatus(TaskStatus.PROCESSING);
		
		try (Stream<Path> paths = Files.walk(Paths.get(rootFolder))) {
		    paths.forEach(p -> {
		    	if (Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS) && !p.getFileName().startsWith(".")) {
		    		try {
						storeFile(p.toFile(), task);
					} catch (JsonParseException e) {
						logger.error("Could not store file " + p.getFileName(), e);
					} catch (JsonMappingException e) {
						logger.error("Could not store file " + p.getFileName(), e);
					} catch (IOException e) {
						logger.error("Could not store file "  + p.getFileName(), e);
					}
		    	}
		    });
		}
		
		task.setStatus(TaskStatus.DONE);
		taskRepo.save((ImportTaskImpl)task);
	}
	
	private void storeFile(File f, ImportTask task) throws JsonParseException, JsonMappingException, IOException {
		if (!f.getName().endsWith(".json")) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		PublicationImpl publication = mapper.readValue(f, PublicationImpl.class);
		pubRepo.save(publication);
		task.setProcessed(task.getProcessed() + 1);
	}
	
}
