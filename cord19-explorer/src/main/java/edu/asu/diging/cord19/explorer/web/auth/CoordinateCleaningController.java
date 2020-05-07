package edu.asu.diging.cord19.explorer.web.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;




@Controller
public class CoordinateCleaningController {
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    
    @RequestMapping(value = "/auth/coordinates/clean", method = RequestMethod.GET)
    public String show() {

        return "auth/cleanCoordinates";
    }
    
    @RequestMapping(value = "/auth/coordinates/clean", method = RequestMethod.POST)
    public String start() throws ClassCastException, ClassNotFoundException, IOException {
        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query(), PublicationImpl.class)) {
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                List<PersonImpl> authors = pub.getMetadata().getAuthors();
                for(PersonImpl author : authors) {
                    if(author.getAffiliation().getSelectedWikiArticle() != null) {
                       String article = author.getAffiliation().getSelectedWikiArticle();
                        ObjectMapper mapper = new ObjectMapper();
                         JsonNode js = mapper.readTree(article);
                    }
                }
            }
        }
        

        return "redirect:/";
    }
    
}