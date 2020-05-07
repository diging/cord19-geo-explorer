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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.logging.log4j.util.TriConsumer;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.model.Affiliation;
import edu.asu.diging.cord19.explorer.core.model.LocationMatch;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationMatchImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationType;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;
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
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties",
        "classpath:/states.txt" })
public class DocImporterImpl implements DocImporter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${metadata.filename}")
    private String metadataFilename;

    @Value("${app.data.path}")
    private String appdataPath;

    @Value("${models.folder.name}")
    private String modelsFolderName;

    @Autowired
    private Environment env;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private PublicationRepository pubRepo;

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

        try (Stream<Path> paths = Files.walk(Paths.get(rootFolder))) {
            paths.forEach(p -> {
                if (Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS) && !p.getFileName().startsWith(".")) {
                    try {
                        storeFile(p.toFile(), task, writer);
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

        writer.close();

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

    private void storeFile(File f, ImportTask task, BufferedWriter writer)
            throws JsonParseException, JsonMappingException, IOException, ClassCastException, ClassNotFoundException {
        if (!f.getName().endsWith(".json")) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        PublicationImpl publication = mapper.readValue(f, PublicationImpl.class);
        List<LocationMatch> invalidMatches = findLocations(publication);
        processLocationMatches(publication);
        for (LocationMatch match : invalidMatches) {
            writer.newLine();
            writer.write(match.getLocationName());
            writer.flush();
        }
        processAffiliations(publication);
        pubRepo.save(publication);
        task.setProcessed(task.getProcessed() + 1);
        logger.debug("Stored: " + publication.getPaperId());
    }

    @Override
    @Async
    public void cleanAffiliations(String taskId, boolean reprocess) {
        Optional<ImportTaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        ImportTask task = optional.get();
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
                processAffiliations(pub);
                pubRepo.save(pub);
                counter++;
            }
        }

        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((ImportTaskImpl) task);
    }
    
    @Override
    @Async
    public void selectLocationMatches(String taskId) {
        Optional<ImportTaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        ImportTask task = optional.get();
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
        taskRepo.save((ImportTaskImpl) task);
    }

    private void processAffiliations(Publication pub) {
        if (pub.getMetadata() == null || pub.getMetadata().getAuthors() == null) {
            return;
        }
        for (PersonImpl author : pub.getMetadata().getAuthors()) {
            if (author.getAffiliation() == null) {
                continue;
            }

            Affiliation affiliation = author.getAffiliation();
            affiliation.setWikiarticles(new ArrayList<>());
            List<Wikientry> wikientries = null;
            if (affiliation.getInstitution() != null && !affiliation.getInstitution().trim().isEmpty()) {
                wikientries = searchElasticInTitle(affiliation.getInstitution());
                findWikiarticles(affiliation, wikientries, LocationType.INSTITUTION, this::addArticleToAffiliation);
            }
            if (affiliation.getLocationSettlement() != null && !affiliation.getLocationSettlement().trim().isEmpty()) {
                wikientries = searchElasticInTitle(affiliation.getLocationSettlement());
                findWikiarticles(affiliation, wikientries, LocationType.CITY, this::addArticleToAffiliation);
            }
            String locationRegion = affiliation.getLocationRegion();
            String country = affiliation.getLocationCountry();
            if (locationRegion != null && locationRegion.length() == 2) {
                if (isCountryUSA(country)) {
                    String state = env.getProperty(locationRegion);
                    if (state != null && !state.trim().isEmpty()) {
                        locationRegion = state;
                    }
                }
            }
            if (affiliation.getLocationRegion() != null && !affiliation.getLocationRegion().trim().isEmpty()) {
                wikientries = searchElasticInTitle(locationRegion);
                findWikiarticles(affiliation, wikientries, LocationType.REGION, this::addArticleToAffiliation);
            }
            if (affiliation.getLocationCountry() != null && !affiliation.getLocationCountry().trim().isEmpty()) {
                wikientries = searchElasticInTitle(affiliation.getLocationCountry());
                findWikiarticles(affiliation, wikientries, LocationType.COUNTRY, this::addArticleToAffiliation);
            }
            
            selectArticle(affiliation);
        }
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
                    selectArticle(match);
                }
            }
        }
    }

    private void selectArticle(Affiliation affiliation) {
        for (WikipediaArticleImpl article : affiliation.getWikiarticles()) {
            String articleTitle = article.getTitle();
            // if the institution is exactly the same, we assume it's the right article
            if (affiliation.getInstitution() != null && articleTitle.equals(affiliation.getInstitution())) {
                affiliation.setSelectedWikiarticle(article);
                article.setSelectedOn(OffsetDateTime.now().toString());
                return;
            }

            // e.g. Worcester, Massachusetts
            String city = affiliation.getLocationSettlement();
            String state = getState(affiliation.getLocationRegion(), affiliation.getLocationCountry());
            if (!StringUtils.isBlank(city) && !StringUtils.isBlank(state)
                    && article.getLocationType().equals(LocationType.CITY)) {
                if (consistsOfTwoPlaces(articleTitle, city, state)) {
                    affiliation.setSelectedWikiarticle(article);
                    article.setSelectedOn(OffsetDateTime.now().toString());
                    return;
                }
            }

            // e.g. Panama City, Panama
            String country = getCountry(affiliation.getLocationCountry());
            if (!StringUtils.isBlank(city) && !StringUtils.isBlank(country)) {
                if (consistsOfTwoPlaces(articleTitle, city, country)) {
                    affiliation.setSelectedWikiarticle(article);
                    article.setSelectedOn(OffsetDateTime.now().toString());
                    return;
                }
            }

            // city or state equals article title
            if ((!StringUtils.isBlank(city) && articleTitle.equals(city.trim()))
                    || (!StringUtils.isBlank(state) && articleTitle.equals(state.trim()))) {
                affiliation.setSelectedWikiarticle(article);
                article.setSelectedOn(OffsetDateTime.now().toString());
                return;
            }
            
            if (!StringUtils.isBlank(affiliation.getInstitution())) {
                JaroWinklerSimilarity sim = new JaroWinklerSimilarity();
                Double similarity = sim.apply(articleTitle, affiliation.getInstitution());
                if (similarity > 0.8) {
                    affiliation.setSelectedWikiarticle(article);
                    article.setSelectedOn(OffsetDateTime.now().toString());
                    return; 
                }
            }

            if (!StringUtils.isBlank(country) && articleTitle.equals(country.trim())) {
                affiliation.setSelectedWikiarticle(article);
                article.setSelectedOn(OffsetDateTime.now().toString());
                return;
            }
        }
    }
    
    private void selectArticle(LocationMatch match) {
        if (match.getWikipediaArticles() == null) {
            return;
        }
        Map<Double, WikipediaArticleImpl> similarities = new HashMap<Double, WikipediaArticleImpl>();
        for (WikipediaArticleImpl article : match.getWikipediaArticles()) {
            String articleTitle = article.getTitle();
            if (!StringUtils.isBlank(match.getLocationName())) {
                JaroWinklerSimilarity sim = new JaroWinklerSimilarity();
                Double similarity = sim.apply(articleTitle, match.getLocationName());
                if (similarity > 0.8) {
                    similarities.put(similarity, article);
                }
            }
        }
        
        Optional<Double> max = similarities.keySet().stream().max(Double::compareTo);
        if (max.isPresent()) {
            match.setSelectedArticle(similarities.get(max.get()));
            match.getSelectedArticle().setSelectedOn(OffsetDateTime.now().toString());
        }
    }

    private boolean consistsOfTwoPlaces(String title, String place1, String place2) {
        if (title.contains(place1.trim()) && title.contains(place2.trim())
                && title.length() >= (place1.trim().length() + place2.trim().length() + 1)
                && title.length() <= (place1.trim().length() + place2.trim().length() + 5)) {
            return true;
        }
        return false;
    }

    private String getState(String state, String country) {
        if (state != null && state.trim().length() == 2 && isCountryUSA(country)) {
            String stateName = env.getProperty(state);
            if (!StringUtils.isBlank(stateName)) {
                return stateName.trim();
            }
        }
        return state;
    }
    
    private String getCountry(String country) {
        if (StringUtils.isBlank(country)) {
            return "";
        }
        
        switch (country.trim()) {
        case "US":
            return "United States";
        case "USA":
            return "United States";
        case "UK":
            return "United Kingdom";
        }
        
        return country;
    }

    private boolean isCountryUSA(String country) {
        if (country != null && (country.equals("USA") || country.equals("United States") || country.equals("US")
                || country.equals("United States of America"))) {
            return true;
        }
        return false;
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

    private List<LocationMatch> findLocations(Publication pub)
            throws ClassCastException, ClassNotFoundException, IOException {
        List<LocationMatch> inValidMatches = new ArrayList<>();
        for (ParagraphImpl para : pub.getBodyText()) {
            if (para.getLocationMatches() == null) {
                para.setLocationMatches(new ArrayList<>());
            }
            String[] tokens = tokenize(para.getText());
            Span nameSpans[] = nameFinder.find(tokens);

            for (Span span : nameSpans) {
                LocationMatch match = createMatch(span, para, tokens);
                if (match != null) {
                    if (isValid(match)) {
                        para.getLocationMatches().add(match);
                    } else {
                        inValidMatches.add(match);
                    }
                }
            }
            nameFinder.clearAdaptiveData();
        }
        return inValidMatches;
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

        List<Wikientry> entries = searchElasticInTitle(match.getLocationName());

        if (entries.size() == 0) {
            return false;
        }

        if (entries.size() > 0) {
            boolean isPlace = findWikiarticles(match, entries, LocationType.OTHER, this::addArticleToMatch);

            if (!isPlace) {
                return false;
            }
        }

        return true;
    }

    private boolean findWikiarticles(Object match, List<Wikientry> entries, LocationType type,
            TriConsumer<Object, Wikientry, LocationType> attachMethod) {
        List<String> placeIndicators = Arrays.asList("republic", "land", "state", "countr", "place", "cit", "park",
                "region", "continent", "district", "metro", "town", "captial", "village", "settlement", "university");

        boolean isPlace = false;
        // if one of the first x results seems to be a place, we assume it's one
        for (Wikientry entry : entries) {
            if (entry.getComplete_text().trim().toLowerCase().startsWith("#redirect")) {
                Wikientry redirectEntry = followRedirect(entry);
                if (redirectEntry != null) {
                    entry = redirectEntry;
                }
            }

            isPlace = isPlace || entry.getCategories().stream()
                    .anyMatch(c -> placeIndicators.stream().anyMatch(p -> c.toLowerCase().contains(p)));

            if (entry.getCoordinates() != null && !entry.getCoordinates().trim().isEmpty()) {
                isPlace = true;
                attachMethod.accept(match, entry, type);
            }
        }
        return isPlace;
    }

    private void addArticleToMatch(Object matchObject, Wikientry entry, LocationType type) {
        LocationMatch match = (LocationMatch) matchObject;
        if (match.getWikipediaArticles() == null) {
            match.setWikipediaArticles(new ArrayList<>());
        }
        boolean exists = match.getWikipediaArticles().stream().anyMatch(a -> a.getTitle().equals(entry.getTitle()));

        if (!exists) {
            WikipediaArticleImpl article = new WikipediaArticleImpl();
            // article.setCompleteText(entry.getComplete_text());
            article.setTitle(entry.getTitle());
            article.setCoordinates(entry.getCoordinates());
            article.setLocationType(type);
            match.getWikipediaArticles().add(article);
        }
    }

    private void addArticleToAffiliation(Object affiliationObject, Wikientry entry, LocationType type) {
        Affiliation affiliation = (Affiliation) affiliationObject;
        boolean exists = affiliation.getWikiarticles().stream().anyMatch(a -> a.getTitle().equals(entry.getTitle()));

        if (!exists) {
            WikipediaArticleImpl article = new WikipediaArticleImpl();
            // article.setCompleteText(entry.getComplete_text());
            article.setTitle(entry.getTitle());
            article.setCoordinates(entry.getCoordinates());
            article.setLocationType(type);
            affiliation.getWikiarticles().add(article);
        }
    }

    private List<Wikientry> searchElasticInTitle(String location) {
        PageRequest page = PageRequest.of(0, 10);

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        String searchTerm = prepareSearchTerm(location);
        if (searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        builder.must(QueryBuilders.queryStringQuery("title:" + searchTerm));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(builder);
        nativeSearchQueryBuilder.withPageable(page);
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        List<Wikientry> entries = searchTemplate.queryForList(query, Wikientry.class);
        return entries;
    }

    private Wikientry followRedirect(Wikientry entry) {
        Pattern redirectPattern = Pattern.compile("#([rR][eE][Dd][Ii][Rr][Ee][Cc][Tt]) \\[\\[(.+?)\\]\\]");
        Matcher redirectMatcher = redirectPattern.matcher(entry.getComplete_text());

        if (redirectMatcher.find()) {
            PageRequest redirectPage = PageRequest.of(0, 1);
            String searchTerm = prepareSearchTerm(redirectMatcher.group(2));

            if (searchTerm.trim().isEmpty()) {
                return null;
            }

            BoolQueryBuilder redirectBuilder = QueryBuilders.boolQuery();
            redirectBuilder.must(QueryBuilders.termQuery("title_keyword", searchTerm));

            NativeSearchQueryBuilder redirectQueryBuilder = new NativeSearchQueryBuilder();
            redirectQueryBuilder.withQuery(redirectBuilder);
            redirectQueryBuilder.withPageable(redirectPage);
            NativeSearchQuery redirectQuery = redirectQueryBuilder.build();
            List<Wikientry> redirectEntry = searchTemplate.queryForList(redirectQuery, Wikientry.class);

            if (redirectEntry.size() > 0) {
                return redirectEntry.get(0);
            }
        }

        return null;
    }

    private String prepareSearchTerm(String term) {
        term = term.replace("/", " ").replace("(", " ").replace(")", " ");
        term = term.replace("[", " ").replace("]", " ").replace(":", " ");
        term = term.replace("{", " ").replace("}", " ").replace("~", " ");
        term = term.replace("\"", "").replace("'", "").replace("^", "");
        term = term.replace("!", "").replace("-", " ").replace(".", " ");
        term = term.replace("_", " ");
        term = term.replace(" OR", " ").replace(" OR ", " ");
        term = term.replace(" AND", " ").replace(" AND ", " ");

        // FIXME: check against US states
        if (term.trim().equals("OR") || term.trim().equals("AND")) {
            return "";
        }
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
                        if (nonValidMatches.contains(match.getLocationName()) || !isValid(match)) {
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
