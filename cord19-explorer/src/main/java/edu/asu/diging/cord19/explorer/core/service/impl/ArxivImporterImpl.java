package edu.asu.diging.cord19.explorer.core.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.synd.SyndPerson;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.Affiliation;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.atom.ArxivFeed;
import edu.asu.diging.cord19.explorer.core.model.atom.ArxivSyndPerson;
import edu.asu.diging.cord19.explorer.core.model.impl.AffiliationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.CategoryImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.MetadataImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.model.task.TaskStatus;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.AffiliationCleaner;
import edu.asu.diging.cord19.explorer.core.service.ArxivImporter;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties",
"classpath:/states.txt" })
public class ArxivImporterImpl implements ArxivImporter {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${arxiv.baseurl}")
    private String arxivBaseUrl;
    
    @Value("${arxiv.search.pagesize}")
    private int arxivPagesize;
    
    @Autowired
    private PublicationRepository repo;
    
    @Autowired
    private TaskRepository taskRepo;
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @Autowired
    private ArxivAtomHttpConverter converter;
    
    @Autowired
    private AffiliationCleaner affCleaner;

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.ArxivImporter#importMetadata(java.lang.String)
     */
    @Override
    @Async
    public void importMetadata(String taskId, String searchTerm) {
        
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);
        
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);
        RestTemplate template = new RestTemplate(converters);
        
        int start = 0;
        int resultCount = 0;
        
        Feed feed = template.getForObject(arxivBaseUrl + searchTerm + "&start=" + start + "&max_results=" + arxivPagesize, Feed.class);
        start += feed.getEntries().size();
        
        List<Element> openSearch = feed.getForeignMarkup();
        for (Element elem : openSearch) {
            if (elem.getName().equals(ARXIV_TOTAL_RESULTS)) {
                resultCount = new Integer(elem.getValue());
                break;
            }     
        }
        
        handleEntries(feed);
        
        while (start < resultCount) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Could not sleep. Stopping import.", e);
                break;
            }
            feed = template.getForObject(arxivBaseUrl + searchTerm + "&start=" + start + "&max_results=" + arxivPagesize, ArxivFeed.class);
            start += feed.getEntries().size();
            
            handleEntries(feed);
        }
        
        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }

    private void handleEntries(Feed feed) {
        for (Entry entry : feed.getEntries()) {
            String arxivId = getArxivId(entry.getId());
            if (pubRepo.findFirstByArxivId(arxivId) == null) {
                PublicationImpl pub = (PublicationImpl)parseEntry(entry);
                repo.save(pub);
            }
        }
    }

    private Publication parseEntry(Entry entry) {
        Publication pub = new PublicationImpl();
        pub.setDatabase(Publication.DATABASE_ARXIV);
        
        setAbstract(entry, pub);
        setDate(entry, pub);
        
        pub.setMetadata(new MetadataImpl());
        pub.getMetadata().setTitle(entry.getTitle());
        
        setIds(entry, pub);
        setAuthors(entry, pub);
        setPdfLink(entry, pub);
        setHtmlLink(entry, pub);
        setCategories(entry, pub);
        setArxivData(entry, pub);
        
        affCleaner.processAffiliations(pub);
        return pub;
    }

    private void setArxivData(Entry entry, Publication pub) {
        for (Element elem : entry.getForeignMarkup()) {
            if (elem.getName().equals(ARXIV_DOI)) {
                pub.setDoi(elem.getValue());
            } else if(elem.getName().equals(ARXIV_JOURNAL)) {
                pub.setJournal(elem.getValue());
            } else if(elem.getName().equals(ARXIV_PRIMARY_CATEGORY)) {
                CategoryImpl cat = new CategoryImpl();
                cat.setTerm(elem.getAttributeValue("term"));
                pub.setPrimaryCategory(cat);
            } else if(elem.getName().equals(ARXIV_COMMENT)) {
                pub.setComment(elem.getValue());
            }
        }
    }

    private void setCategories(Entry entry, Publication pub) {
        pub.setCategories(new ArrayList<>());
        for (Category cat : entry.getCategories()) {
            CategoryImpl category = new CategoryImpl();
            category.setLabel(cat.getLabel());
            category.setScheme(cat.getScheme());
            category.setTerm(cat.getTerm());
            pub.getCategories().add(category);
        }
    }

    private void setHtmlLink(Entry entry, Publication pub) {
        for (Link link : entry.getAlternateLinks()) {
            if (link.getType() != null && link.getType().equals(MediaType.TEXT_HTML.toString())) {
                pub.setUrl(link.getHref());
            }
        }
    }

    private void setPdfLink(Entry entry, Publication pub) {
        for (Link link : entry.getOtherLinks()) {
            if (link.getType() != null && link.getType().equals(MediaType.APPLICATION_PDF.toString())) {
                pub.setDocumentUrl(link.getHref());
                pub.setDocumentType(link.getType());
            }
        }
    }

    private void setAuthors(Entry entry, Publication pub) {
        pub.getMetadata().setAuthors(new ArrayList<>());
        for (SyndPerson person : entry.getAuthors()) {
            PersonImpl author = new PersonImpl();
            author.setName(person.getName());
            author.setEmail(person.getEmail());
            author.setUri(person.getUri());
            
            for (Element elem : ((ArxivSyndPerson)person).getForeignMarkup()) {
                if (elem.getName().equals(ARXIV_AFFILIATION)) {
                    Affiliation aff = new AffiliationImpl();
                    aff.setInstitution(elem.getValue());
                    author.setAffiliation(aff);
                }
            }
            pub.getMetadata().getAuthors().add(author);
        }
    }

    private void setIds(Entry entry, Publication pub) {
        pub.setPaperId(getArxivId(entry.getId()));
        pub.setArxivId(pub.getPaperId());
        pub.setUrl(entry.getId());
    }

    private void setDate(Entry entry, Publication pub) {
        Date published = entry.getPublished();
        pub.setPublishTime(published.toString());
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(published);
        pub.setPublishYear(cal.get(Calendar.YEAR));
    }

    private void setAbstract(Entry entry, Publication pub) {
        ParagraphImpl abstractText = new ParagraphImpl();
        abstractText.setText(entry.getSummary().getValue());
        pub.setAbstracts(new ArrayList<ParagraphImpl>());
        pub.getAbstracts().add(abstractText);
    }
    
    private String getArxivId(String uri) {
        if(uri != null && !uri.trim().isEmpty()) {
            int idxSlash = uri.lastIndexOf("/");
            if (idxSlash > -1) {
                return uri.substring(idxSlash+1);
            }
            return uri;
        }
        return "";
    }
}
