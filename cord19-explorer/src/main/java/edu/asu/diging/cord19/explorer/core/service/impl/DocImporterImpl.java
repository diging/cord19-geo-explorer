package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
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
import edu.asu.diging.cord19.explorer.core.model.impl.LocationMatchImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.task.ImportTask;
import edu.asu.diging.cord19.explorer.core.model.task.impl.ImportTaskImpl;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskStatus;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.DocImporter;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class DocImporterImpl implements DocImporter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String classifier = "/Users/jdamerow/jars/stanford-english-corenlp-2018-10-05-models/edu/stanford/nlp/models/ner/english.all.3class.caseless.distsim.crf.ser.gz";
	// private final String classifier =
	// "/classifier/english.muc.7class.distsim.crf.ser.gz";

	@Value("${metadata.filename}")
	private String metadataFilename;

	@Value("${app.data.path}")
	private String appdataPath;

	@Value("${models.folder.name}")
	private String modelsFolderName;

	@Autowired
	private TaskRepository taskRepo;

	@Autowired
	private PublicationRepository pubRepo;

	@Autowired
	private MongoTemplate mongoTemplate;

	private TokenNameFinderModel model;

	private NameFinderME nameFinder;

	@PostConstruct
	public void init() throws ClassCastException, ClassNotFoundException, IOException {
		File r = new File(appdataPath + modelsFolderName + File.separator + "en-ner-location.bin");
		model = new TokenNameFinderModel(new FileInputStream(r));
		nameFinder = new NameFinderME(model);
	}

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
						logger.error("Could not store file " + p.getFileName(), e);
					} catch (ClassCastException e) {
						logger.error("Could not store file " + p.getFileName(), e);
					} catch (ClassNotFoundException e) {
						logger.error("Could not store file " + p.getFileName(), e);
					}
				}
			});
		}

		File metadataFile = new File(rootFolder + File.separator + metadataFilename);
		CsvToBean<MetadataEntry> bean = new CsvToBeanBuilder(new FileReader(metadataFile)).withType(MetadataEntry.class)
				.withIgnoreLeadingWhiteSpace(true).build();

		Iterator<MetadataEntry> it = bean.iterator();
		while (it.hasNext()) {
			MetadataEntry entry = it.next();
			PublicationImpl pub = null;
			if (entry.getSha() != null && !entry.getSha().isEmpty()) {
				pub = pubRepo.findFirstByPaperId(entry.getSha());
			}
			if (pub == null && entry.getPmcid() != null && !entry.getPmcid().isEmpty()) {
				pub = pubRepo.findFirstByPaperId(entry.getPmcid());
			}

			if (pub != null) {
				pub.setHasPdfParse(entry.getHasPdfParse().equals("TRUE"));
				pub.setCordId(entry.getCord_uid());
				pub.setDoi(entry.getDoi());
				pub.setHasPmcXmlParse(entry.getHasPmcXmlParse().equals("TRUE"));
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
				extractYear(pub);
				pubRepo.save(pub);
			}
		}

		task.setStatus(TaskStatus.DONE);
		task.setDateEnded(OffsetDateTime.now());
		taskRepo.save((ImportTaskImpl) task);
	}

	private void storeFile(File f, ImportTask task)
			throws JsonParseException, JsonMappingException, IOException, ClassCastException, ClassNotFoundException {
		if (!f.getName().endsWith(".json")) {
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		PublicationImpl publication = mapper.readValue(f, PublicationImpl.class);
		findLocations(publication);
		pubRepo.save(publication);
		task.setProcessed(task.getProcessed() + 1);
		logger.debug("Stored: " + publication.getPaperId());
	}

	@Override
	@Async
	public void extractYears(String taskId) {
		Optional<ImportTaskImpl> optional = taskRepo.findById(taskId);
		if (!optional.isPresent()) {
			return;
			// FIXME: mark as failure
		}

		ImportTask task = optional.get();
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
		taskRepo.save((ImportTaskImpl) task);
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
		Optional<ImportTaskImpl> optional = taskRepo.findById(taskId);
		if (!optional.isPresent()) {
			return;
			// FIXME: mark as failure
		}

		ImportTask task = optional.get();
		task.setStatus(TaskStatus.PROCESSING);

		try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query(), PublicationImpl.class)) {
			while (docs.hasNext()) {
				PublicationImpl pub = docs.next();
				findLocations(pub);
				pubRepo.save(pub);
			}
		}

		task.setStatus(TaskStatus.DONE);
		task.setDateEnded(OffsetDateTime.now());
		taskRepo.save((ImportTaskImpl) task);
	}

	private void findLocations(Publication pub) throws ClassCastException, ClassNotFoundException, IOException {
		for (ParagraphImpl para : pub.getBodyText()) {
			if (para.getLocationMatches() == null) {
				para.setLocationMatches(new ArrayList<>());
			}
			String[] tokens = tokenize(para.getText());
			Span nameSpans[] = nameFinder.find(tokens);

			for (Span span : nameSpans) {
				LocationMatch match = createMatch(span, para, tokens);
				if (match != null && isValid(match)) {
					para.getLocationMatches().add(match);
				}
			}
			nameFinder.clearAdaptiveData();
		}
	}

	private boolean isValid(LocationMatch match) {
		// we do want only numbers
		Pattern pattern = Pattern.compile("[0-9,\\.\\-\\:&\\W]+");
		Matcher m = pattern.matcher(match.getLocationName());
		if (m.matches()) {
			return false;
		}

		// let's exclude 2 letter words
		if (match.getLocationName().length() <= 2) {
			return false;
		}
		
		if (m.find()) {
			// length of match
			int matchLength = m.group().length();
			// if the match is as long as rest of string, we assume it's not a location
			if (matchLength >= match.getLocationName().length()-matchLength) {
				return false;
			}
		}
		
		Pattern pattern2 = Pattern.compile("[A-Z]*[0-9,\\.\\-\\(\\)/]+[A-Z]*");
		Matcher m2 = pattern2.matcher(match.getLocationName());
		if (m2.matches()) {
			return false;
		}
		
		return true;
	}

	private LocationMatch createMatch(Span span, ParagraphImpl para, String[] tokens) {
		StringBuilder sb = new StringBuilder();
		for (int i = span.getStart(); i <= span.getEnd(); i++) {
			if (tokens.length > i) {
				String location = tokens[i];
				Pattern pattern = Pattern.compile("[0-9,\\.\\-\\:&\\W]+");
				Matcher m = pattern.matcher(location);
				
				Pattern pattern2 = Pattern.compile("[^A-Z].*");
				Matcher m2 = pattern2.matcher(location);
				if (!m.matches() && !m2.matches()) {
					sb.append(" ");
					sb.append(location);
				}
			}
		}
		if (sb.toString().trim().isEmpty()) {
			return null;
		}
		
		LocationMatch match = new LocationMatchImpl();
		match.setId(new ObjectId());
		match.setStart(span.getStart());
		match.setType(span.getType());
		match.setSection(para.getSection());
		match.setLocationName(sb.toString().trim());
		match.setEnd(match.getStart() + match.getLocationName().length());

		return match;
	}

	public String[] tokenize(String sentence) throws IOException {
		File r = new File(appdataPath + modelsFolderName + File.separator + "en-token.bin");
		TokenizerModel tokenModel = new TokenizerModel(new FileInputStream(r));

		TokenizerME tokenizer = new TokenizerME(tokenModel);
		return tokenizer.tokenize(sentence);
	}
}
