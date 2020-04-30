package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
import edu.asu.diging.cord19.explorer.core.elastic.data.WikipediaSearchRepository;
import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
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
	private WikipediaSearchRepository searchRepo;

	@Autowired
	private ElasticsearchTemplate searchTemplate;

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
		if (match.getLocationName().isEmpty()) {
			return false;
		}
		
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
			if (matchLength >= match.getLocationName().length() - matchLength) {
				return false;
			}
		}

		Pattern pattern2 = Pattern.compile("[A-Z]*[0-9,\\.\\-\\(\\)/]+[A-Z]*");
		Matcher m2 = pattern2.matcher(match.getLocationName());
		if (m2.matches()) {
			return false;
		}

		// exclude things like "A,T,M", "A/Anhui/1/2005"
		Pattern p3 = Pattern.compile("[\\S]*[0-9/\\+,]+[\\S]*");
		Matcher m3 = p3.matcher(match.getLocationName());
		if (m3.matches()) {
			return false;
		}

		// exclude things like ADP-ribose or ADPrs
		Pattern p4 = Pattern.compile("[A-Z0-9\\+\\?]{2,}\\-+[a-z]{2,}");
		Matcher m4 = p4.matcher(match.getLocationName());
		if (m4.matches()) {
			return false;
		}

		if (match.getLocationName().startsWith("Appendix") || match.getLocationName().startsWith("A-")) {
			return false;
		}

		// exclude names with multiple /
		Pattern p5 = Pattern.compile("\\/");
		Matcher m5 = p5.matcher(match.getLocationName());
		int count = 0;
		while (m5.find()) {
			count++;
		}
		if (count > 1) {
			return false;
		}

		PageRequest page = PageRequest.of(0, 5);

		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		builder.must(QueryBuilders.queryStringQuery("title:" + prepareSearchTerm(match.getLocationName())));
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		nativeSearchQueryBuilder.withQuery(builder);
		nativeSearchQueryBuilder.withPageable(page);
		NativeSearchQuery query = nativeSearchQueryBuilder.build();
		List<Wikientry> entries = searchTemplate.queryForList(query, Wikientry.class);

		List<String> placeIndicators = Arrays.asList("republic", "land", "state", "countr", "place", "cit", "park", "region", "continent",
				"district", "metro", "town", "captial");
		if (entries.size() == 0) {
			return false;
		}
		
	    if (entries.size() > 0) {
			boolean isPlace = false;
			// if one of the first x results seems to be a place, we assume it's one
			for (Wikientry entry : entries) {
				isPlace = isPlace || entry.getCategories().stream()
						.anyMatch(c -> placeIndicators.stream().anyMatch(p -> c.toLowerCase().contains(p)));
			}
			;
			if (!isPlace) {
				return false;
			}
		}

		return true;
	}

	private String prepareSearchTerm(String term) {
		term = term.replace("/", " ").replace("(", " ").replace(")", " ");
		term = term.replace("[", " ").replace("]", " ").replace(":", " ");
		term = term.replace("{", " ").replace("}", " ").replace("~", " ");
		term = term.replace("\"", "").replace("'", "").replace("^", "");
		term = term.replace("!", "").replace("-", " ").replace(".", " ");
		term = term.replace(" OR", " ").replace(" OR ", " ");
		term = term.replace(" AND", " ").replace(" AND ", " ");
		return term;
	}

	@Override
	@Async
	public void removeUnvalid(String taskId) {
		Optional<ImportTaskImpl> optional = taskRepo.findById(taskId);
		if (!optional.isPresent()) {
			return;
			// FIXME: mark as failure
		}

		ImportTask task = optional.get();
		task.setStatus(TaskStatus.PROCESSING);

		File file = new File(appdataPath + File.separator + task.getId() + ".txt");
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
			while (docs.hasNext()) {
				PublicationImpl pub = docs.next();
				logger.debug("Cleaning: " + pub.getPaperId());
				for (ParagraphImpl para : pub.getBodyText()) {
					Iterator<LocationMatch> it = para.getLocationMatches().iterator();
					while (it.hasNext()) {
						LocationMatch match = it.next();
						if (!isValid(match)) {
							try {
								writer.newLine();
								writer.write(match.getLocationName());
								writer.flush();
							} catch (IOException e) {
								logger.error("Could not write to file.", e);
							}
							//it.remove();
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
		taskRepo.save((ImportTaskImpl) task);
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
