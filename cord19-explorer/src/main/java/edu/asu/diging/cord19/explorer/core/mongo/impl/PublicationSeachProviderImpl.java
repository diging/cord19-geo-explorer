package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<PublicationImpl> paginateResults(Pageable pageable, List<PublicationImpl> pubs) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<PublicationImpl> list;

        if (pubs.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, pubs.size());
            list = pubs.subList(startItem, toIndex);
        }

        Page<PublicationImpl> pubPage = new PageImpl<PublicationImpl>(list, PageRequest.of(currentPage, pageSize),
                pubs.size());

        return pubPage;
    }
}
