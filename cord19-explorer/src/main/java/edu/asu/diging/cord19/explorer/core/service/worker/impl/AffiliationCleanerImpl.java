package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.model.Affiliation;
import edu.asu.diging.cord19.explorer.core.model.Person;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationType;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;
import edu.asu.diging.cord19.explorer.core.service.WikipediaHelper;
import edu.asu.diging.cord19.explorer.core.service.worker.AffiliationCleaner;
import edu.asu.diging.cord19.explorer.core.service.worker.ElasticsearchConnector;

@Component
public class AffiliationCleanerImpl implements AffiliationCleaner {
    
    @Autowired
    private Environment env;
    
    @Autowired
    private ElasticsearchConnector elastic;
    
    @Autowired
    private WikipediaHelper wikiHelper;

    @Override
    public void processAffiliations(Publication pub) {
        if (pub.getMetadata() == null || pub.getMetadata().getAuthors() == null) {
            return;
        }
        for (Person author : pub.getMetadata().getAuthors()) {
            if (author.getAffiliation() == null) {
                continue;
            }

            Affiliation affiliation = author.getAffiliation();
            affiliation.setWikiarticles(new ArrayList<>());
            List<Wikientry> wikientries = null;
            if (affiliation.getInstitution() != null && !affiliation.getInstitution().trim().isEmpty()) {
                wikientries = elastic.searchInTitle(affiliation.getInstitution());
                wikiHelper.findWikiarticles(affiliation, wikientries, LocationType.INSTITUTION, this::addArticleToAffiliation);
            }
            if (affiliation.getLocationSettlement() != null && !affiliation.getLocationSettlement().trim().isEmpty()) {
                wikientries = elastic.searchInTitle(affiliation.getLocationSettlement());
                wikiHelper.findWikiarticles(affiliation, wikientries, LocationType.CITY, this::addArticleToAffiliation);
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
                wikientries = elastic.searchInTitle(locationRegion);
                wikiHelper.findWikiarticles(affiliation, wikientries, LocationType.REGION, this::addArticleToAffiliation);
            }
            if (affiliation.getLocationCountry() != null && !affiliation.getLocationCountry().trim().isEmpty()) {
                wikientries = elastic.searchInTitle(affiliation.getLocationCountry());
                wikiHelper.findWikiarticles(affiliation, wikientries, LocationType.COUNTRY, this::addArticleToAffiliation);
            }

            selectArticle(affiliation);
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
    
    
    private boolean isCountryUSA(String country) {
        if (country != null && (country.equals("USA") || country.equals("United States") || country.equals("US")
                || country.equals("United States of America"))) {
            return true;
        }
        return false;
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
    
    private boolean consistsOfTwoPlaces(String title, String place1, String place2) {
        if (title.contains(place1.trim()) && title.contains(place2.trim())
                && title.length() >= (place1.trim().length() + place2.trim().length() + 1)
                && title.length() <= (place1.trim().length() + place2.trim().length() + 5)) {
            return true;
        }
        return false;
    }
}
