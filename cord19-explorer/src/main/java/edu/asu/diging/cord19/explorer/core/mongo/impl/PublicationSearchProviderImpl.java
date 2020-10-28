package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Service
public class PublicationSearchProviderImpl implements SearchProvider {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long getTotalResults(String title) {
        Query query = buildQuery(title);
        return mongoTemplate.count(query, PublicationImpl.class);

    }

    /**
     * Returns a {@code List<PublicationImpl>} instance representing the publication
     * titles matched with the input parameter 'title' .
     * 
     * @param title       Metadata title to be queried
     * 
     * @param currentPage index of current page starting from 0
     * 
     * @param size        length of result sublist
     * 
     * @return List of PublicationImpl instances containing the 'title' string
     * 
     **/
    @Override
    public List<PublicationImpl> search(String title, Long currentPage, Integer size) {
        long startItem = currentPage * size;

        Query query = buildQuery(title).sortByScore().skip(startItem).limit(size);

        return mongoTemplate.find(query, PublicationImpl.class);
    }

    @Override
    public SearchType getSearchType() {
        return SearchType.PUBLICATIONS;
    }

    private TextQuery buildQuery(String title) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(title);
        return TextQuery.queryText(criteria);

    }
}
