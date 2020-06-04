package edu.asu.diging.cord19.explorer.web.auth;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class MapController {
    
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @RequestMapping(value = "/auth/map/show", method = RequestMethod.GET)
    public String show() {

        return "auth/map";
    }
    
    

    @RequestMapping(value = "/auth/map/gather", method = RequestMethod.POST)
    public String gather() throws ClassCastException, ClassNotFoundException, IOException {
        try (CloseableIterator<CountriesImpl> docs = mongoTemplate.stream(new Query(), CountriesImpl.class)) {
            System.out.print(docs.hasNext());
            while (docs.hasNext()) {
                CountriesImpl pub = docs.next();
                System.out.println(pub.getGeometry().toString());
            }
        }
        
        
        return "redirect:/";
    }
    
    
}