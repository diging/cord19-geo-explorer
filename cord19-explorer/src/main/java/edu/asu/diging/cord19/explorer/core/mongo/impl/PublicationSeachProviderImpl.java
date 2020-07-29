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
    public long searchResultSize(String title) {
        Criteria regex = Criteria.where("metadata.title").regex(".*" + title + ".*", "i");
        return mongoTemplate.count(new Query().addCriteria(regex), PublicationImpl.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.mongo.PublicationSearchProvider#
     * getRequestedPage(String title, Inter currentPage, Integer size) 
     * @param  title to be searched for
     * @param  currentPage the index of page starting from 0
     * @size length of result sublist
     * @return List of PublicationImpl instances containing the 'title' parameter
     * 
     * 
     */
    @Override
    public List<PublicationImpl> getRequestedPage(String title, Integer currentPage, Integer size) {
        Criteria regex = Criteria.where("metadata.title").regex(".*" + title + ".*", "i");
        int startItem = currentPage * size;
        return mongoTemplate.find(new Query().addCriteria(regex).skip(startItem).limit(size), PublicationImpl.class);
    }
}
