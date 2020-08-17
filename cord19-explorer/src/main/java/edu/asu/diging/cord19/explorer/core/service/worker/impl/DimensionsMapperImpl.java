package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.model.Affiliation;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.PublicationExtraData;
import edu.asu.diging.cord19.explorer.core.model.PublicationType;
import edu.asu.diging.cord19.explorer.core.model.impl.AffiliationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.CategoryImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.service.worker.AffiliationCleaner;
import edu.asu.diging.cord19.explorer.core.service.worker.DimensionsMapper;
import edu.asu.diging.pubmeta.util.model.Person;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class DimensionsMapperImpl implements DimensionsMapper {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${metadata.for.classification.scheme}")
    private String forScheme;
    
    @Autowired
    private AffiliationCleaner affCleaner;
    
    private Map<edu.asu.diging.pubmeta.util.model.PublicationType, PublicationType> typeMap;
    
    @PostConstruct
    public void init() {
        typeMap = new HashMap<>();
        typeMap.put(edu.asu.diging.pubmeta.util.model.PublicationType.ARTICLE, PublicationType.ARTICLE);
        typeMap.put(edu.asu.diging.pubmeta.util.model.PublicationType.CHAPTER, PublicationType.CHAPTER);
        typeMap.put(edu.asu.diging.pubmeta.util.model.PublicationType.BOOK, PublicationType.BOOK);
        typeMap.put(edu.asu.diging.pubmeta.util.model.PublicationType.PREPRINT, PublicationType.PREPRINT);
        typeMap.put(edu.asu.diging.pubmeta.util.model.PublicationType.POSTER, PublicationType.POSTER);
        typeMap.put(edu.asu.diging.pubmeta.util.model.PublicationType.PRESENTATION, PublicationType.PRESENTATION);
        typeMap.put(edu.asu.diging.pubmeta.util.model.PublicationType.TALK, PublicationType.TALK);
    }
    
    @Override
    public void map(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub) {
        addExtraData(entry, pub);
        
        if (pub.getUrl() == null || pub.getUrl().trim().isEmpty()) {
            pub.setUrl(entry.getUrl());
        }
        if (pub.getDoi() == null || pub.getDoi().trim().isEmpty()) {
            pub.setDoi(entry.getDoi() != null ? entry.getDoi().trim() : null);
        }
        
        if (pub.getCategories() == null) {
            pub.setCategories(new ArrayList<>());
        }
        
        addCategories(entry, pub);
        
        if (pub.getFunder() == null || pub.getFunder().trim().isEmpty()) {
            pub.setFunder(entry.getFunder() != null ? entry.getFunder().trim() : "");
        }
        
        addMeshTerms(entry, pub);
        
        if (pub.getPublishTime() != null) {
            pub.setPublishTime(entry.getPublishTime() != null ? entry.getPublishTime().trim() : "");
        }
        
        setPublishYear(entry, pub);
        
        if (pub.getVolume() == null || pub.getVolume().trim().isEmpty()) {
            pub.setVolume(entry.getVolume() != null ? entry.getVolume().trim() : "");
        }
        
        if (pub.getIssue() == null || pub.getIssue().trim().isEmpty()) {
            pub.setIssue(entry.getIssue() != null ? entry.getIssue().trim() : "");
        }
        
        if (pub.getPages() == null || pub.getPages().trim().isEmpty()) {
            pub.setPages(entry.getPages()!= null ? entry.getPages().trim() : "");
        }
        
        if (pub.getPublicationType() == null && entry.getPublicationType() != null) {
            pub.setPublicationType(typeMap.get(entry.getPublicationType()));
        }
        
        setAuthors(entry, pub);
        setResearchCountries(entry, pub);
        
        if (pub.getFunder() == null || pub.getFunder().trim().isEmpty()) {
            pub.setFunder(entry.getFunder());
        }
        
        if (pub.getTimesCited() <= 0) {
            pub.setTimesCited(entry.getTimesCited());
        }
        
        if (pub.getRecentCitations() <= 0) {
            pub.setRecentCitations(new Integer(entry.getRecentCitations()));
        }
    }

    private void setPublishYear(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub) {
        if (pub.getPublishYear() <= 0) {
            try {
                pub.setPublishYear(entry.getPublishYear());
            } catch(NumberFormatException ex) {
                logger.error("Could not set publication year.", ex);
            }
        }
    }

    private void addMeshTerms(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub) {
        if (pub.getMeshTerms() == null || pub.getMeshTerms().isEmpty()) {
            pub.setMeshTerms(entry.getMeshTerms());
        }
    }

    private void addCategories(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub) {
        if (entry.getCategories() != null && !entry.getCategories().isEmpty() && (pub.getCategories() == null || pub.getCategories().isEmpty())) {
            if (pub.getCategories() == null) {
                pub.setCategories(new ArrayList<>());
            }
            
            entry.getCategories().forEach(c -> pub.getCategories().add(new CategoryImpl(c.getTerm(), c.getScheme(), c.getLabel())));
        }
    }

    private void addExtraData(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub) {
        if (pub.getExtraData() == null) {
            pub.setExtraData(new HashMap<String, Object>());
        }
        
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_ALTMETRIC, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_ALTMETRIC));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_ANTHOLOGY_TITLE, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_ANTHOLOGY_TITLE));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_CORRESPONDING_AUTHOR, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_CORRESPONDING_AUTHOR));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_FCR, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_FCR));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_RCR, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_RCR));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_OPEN_ACCESS, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_OPEN_ACCESS));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_STANDARDIZED_ORGANIZATIONS, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_STANDARDIZED_ORGANIZATIONS));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_GRID_IDS, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_GRID_IDS));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_LINK, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_LINK));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_DEVELOPMENT_GOALS, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_DEVELOPMENT_GOALS));
        pub.getExtraData().put(PublicationExtraData.DIMENSIONS_RANK, entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_RANK));
    }

    private void setAuthors(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub) {
        if ((pub.getMetadata().getAuthors() == null || pub.getMetadata().getAuthors().isEmpty()) && entry.getAuthors() != null) {
            if (pub.getMetadata().getAuthors() == null) {
                pub.getMetadata().setAuthors(new ArrayList<>());
            }
            for (Person person : entry.getAuthors()) {
                PersonImpl author = new PersonImpl();
                author.setFirst(person.getFirstName());
                author.setMiddle(person.getMiddleNames());
                author.setLast(person.getLastName());
                if (person.getAffiliation() != null && !person.getAffiliation().trim().isEmpty()) {
                    Affiliation affiliation = new AffiliationImpl();
                    affiliation.setInstitution(person.getAffiliation());
                    author.setAffiliation(affiliation);
                    affCleaner.processAffiliation(affiliation);
                }
                pub.getMetadata().getAuthors().add(author);
            }
        }
    }
    
    private void setResearchCountries(edu.asu.diging.pubmeta.util.model.Publication entry, Publication pub) {
        if (pub.getMetadata().getAffiliationCountries() != null && !pub.getMetadata().getAffiliationCountries().isEmpty()) {
            return;
        }
        
        if (pub.getMetadata().getAffiliationCountries() == null) {
            pub.getMetadata().setAffiliationCountries(new ArrayList<>());
        }
        
        @SuppressWarnings("unchecked")
        List<String> countries = (List<String>)entry.getExtraData().get(edu.asu.diging.pubmeta.util.model.PublicationExtraData.DIMENSIONS_AFFILIATION_COUNTRIES);
        if (countries != null && !countries.isEmpty()) {
            for (String country : countries) {
                if (!country.trim().isEmpty()) {
                    AffiliationImpl aff = new AffiliationImpl();
                    aff.setLocationCountry(country.trim());
                    pub.getMetadata().getAffiliationCountries().add(aff);
                    affCleaner.processAffiliation(aff);
                }
            }
        }
    }
}
