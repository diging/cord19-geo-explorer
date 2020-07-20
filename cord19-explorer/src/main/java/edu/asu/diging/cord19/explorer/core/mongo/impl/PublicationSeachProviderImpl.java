package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationSearchProvider;

@Service
public class PublicationSeachProviderImpl implements PublicationSearchProvider {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<PublicationImpl> searchPublicationTitles(String title) {
        Criteria regex = Criteria.where("metadata.title").regex(".*" + title + ".*", "i");
        List<PublicationImpl> results = mongoTemplate.find(new Query().addCriteria(regex), PublicationImpl.class);
        return results;
    }
}
