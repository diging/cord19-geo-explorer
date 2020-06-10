package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.model.LocationMatch;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationMatchImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationType;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;
import edu.asu.diging.cord19.explorer.core.service.ElasticsearchConnector;
import edu.asu.diging.cord19.explorer.core.service.TextLocationMatcher;
import edu.asu.diging.cord19.explorer.core.service.WikipediaHelper;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties",
"classpath:/states.txt" })
public class TextLocationMatcherImpl implements TextLocationMatcher {
    
    @Value("${models.folder.name}")
    private String modelsFolderName;
    
    @Value("${app.data.path}")
    private String appdataPath;

    
    @Autowired
    private ElasticsearchConnector elastic;

    @Autowired
    private WikipediaHelper wikiHelper;

    private NameFinderME nameFinder;
    private TokenNameFinderModel model;


    @PostConstruct
    public void init() throws ClassCastException, ClassNotFoundException, IOException {
        File r = new File(appdataPath + modelsFolderName + File.separator + "en-ner-location.bin");
        model = new TokenNameFinderModel(new FileInputStream(r));
        nameFinder = new NameFinderME(model);
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.TextLocationMatcher#findLocations(edu.asu.diging.cord19.explorer.core.model.Publication)
     */
    @Override
    public List<LocationMatch> findLocations(Publication pub)
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

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.TextLocationMatcher#isValid(edu.asu.diging.cord19.explorer.core.model.LocationMatch)
     */
    @Override
    public boolean isValid(LocationMatch match) {
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

        List<Wikientry> entries = elastic.searchInTitle(match.getLocationName());

        if (entries.size() == 0) {
            return false;
        }

        if (entries.size() > 0) {
            boolean isPlace = wikiHelper.findWikiarticles(match, entries, LocationType.OTHER, this::addArticleToMatch);

            if (!isPlace) {
                return false;
            }
        }

        return true;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.TextLocationMatcher#selectArticle(edu.asu.diging.cord19.explorer.core.model.LocationMatch)
     */
    @Override
    public void selectArticle(LocationMatch match) {
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

    
    private String[] tokenize(String sentence) throws IOException {
        File r = new File(appdataPath + modelsFolderName + File.separator + "en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(new FileInputStream(r));

        TokenizerME tokenizer = new TokenizerME(tokenModel);
        return tokenizer.tokenize(sentence);
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
}
