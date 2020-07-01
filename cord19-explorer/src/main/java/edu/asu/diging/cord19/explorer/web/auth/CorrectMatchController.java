package edu.asu.diging.cord19.explorer.web.auth;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sun.mail.iap.Response;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationType;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaSelectionStatus;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.worker.ElasticsearchConnector;

@Controller
public class CorrectMatchController {
    
    @Autowired
    private ElasticsearchConnector elasticConnector;
    
    @Autowired
    private PublicationRepository repo;


    @RequestMapping(value="/auth/affiliation/wiki/find")
    public ResponseEntity<List<Wikientry>> findWikientries(@RequestParam("title") String title) {
        List<Wikientry> entries = elasticConnector.searchInTitle(title);
        return new ResponseEntity<List<Wikientry>>(entries, HttpStatus.OK);
    }
    
    @RequestMapping(value="/auth/affiliation/wiki/select", method=RequestMethod.POST)
    public ResponseEntity<String> selectEntry(@RequestParam("id") String id, @RequestParam("affiliation") String affiliation) {
        // FIXME: needs to go into manager class
        Wikientry selected = elasticConnector.findById(id);
        if (selected == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        List<PublicationImpl> pubs = repo.findByMetadataAuthorsAffiliationInstitution(affiliation);
        for (Publication pub : pubs) {
            for (PersonImpl person : pub.getMetadata().getAuthors()) {
                if (person.getAffiliation().getInstitution() != null
                        && person.getAffiliation().getInstitution().equals(affiliation)) {
                    WikipediaArticleImpl article = new WikipediaArticleImpl();
                    // article.setCompleteText(entry.getComplete_text());
                    article.setTitle(selected.getTitle());
                    article.setCoordinates(selected.getCoordinates());
                    article.setLocationType(LocationType.CURATED);
                    article.setSelectedOn(OffsetDateTime.now().toString());
                    
                    person.getAffiliation().setSelectedWikiarticle(article);
                    person.getAffiliation().setSelectionCheckedOn(OffsetDateTime.now().toString());
                    repo.save((PublicationImpl) pub);
                }
            }
        }
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
 }
