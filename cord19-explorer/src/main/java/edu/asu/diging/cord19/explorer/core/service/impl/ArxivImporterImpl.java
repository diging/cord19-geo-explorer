package edu.asu.diging.cord19.explorer.core.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.MetadataImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.service.ArxivImporter;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties",
"classpath:/states.txt" })
public class ArxivImporterImpl implements ArxivImporter {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private RestTemplate template;
    
    @Value("${arxiv.baseurl}")
    private String arxivBaseUrl;

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.ArxivImporter#importMetadata(java.lang.String)
     */
    @Override
    @Async
    public void importMetadata(String searchTerm) {
        
        template = new RestTemplate();
        int start = 0;
        int resultCount = 0;
        boolean hasMore = true;
        while (hasMore) {
            Feed feed = template.getForObject(arxivBaseUrl + searchTerm, Feed.class);
           
            List<Element> openSearch = feed.getForeignMarkup();
            for (Element elem : openSearch) {
                if (elem.getName().equals(ARXIV_TOTAL_RESULTS)) {
                    resultCount = new Integer(elem.getValue());
                    break;
                }     
            }
            
            for (Entry entry : feed.getEntries()) {
                PublicationImpl pub = new PublicationImpl();
                pub.setDatabase(Publication.DATABASE_ARXIV);
                ParagraphImpl abstractText = new ParagraphImpl();
                abstractText.setText(entry.getSummary().getValue());
                pub.setAbstracts(new ArrayList<ParagraphImpl>());
                pub.getAbstracts().add(abstractText);
                
                Date published = entry.getPublished();
                pub.setPublishTime(published.toString());
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(published);
                pub.setPublishYear(cal.get(Calendar.YEAR));
                
                pub.setMetadata(new MetadataImpl());
                pub.getMetadata().setTitle(entry.getTitle());
                pub.setPaperId(entry.getId());
                
                // FIXME : continue
            }
        }
        
        
    }
}
