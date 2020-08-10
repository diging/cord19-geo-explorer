package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.mongo.AffiliationSearchProvider;


@Service
public class AffiliationSearchProviderImpl implements AffiliationSearchProvider {


    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public long searchResultSize(String title) {
        Criteria regex = Criteria.where("metadata.authors.affiliation.selectedWikiarticle.title").regex(".*" + title + ".*", "i");
        return mongoTemplate.count(new Query().addCriteria(regex), String.class);
    }
    
    @Override
    public List<String> getRequestedPage(String title, Long currentPage, Integer size) {
        Criteria regex = Criteria.where("metadata.authors.affiliation.selectedWikiarticle.title").regex(".*" + title + ".*", "i");
        long startItem = currentPage * size;
        return mongoTemplate.find(new Query().addCriteria(regex).skip(startItem).limit(size), String.class);
    }
    
    
}
