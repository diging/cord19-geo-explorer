package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;

@Service
public class PublicationDaoImpl implements PublicationDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.mongo.impl.PublicationDao#getCountries()
     */
    @Override
    public List<String> getCountries() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        DistinctIterable<String> output = mongoTemplate.getCollection(collection)
                .distinct("metadata.authors.affiliation.institution", String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        while (it.hasNext()) {
            results.add(it.next());
        }
        return results;
    }

    @Override
    public List<String> getCountriesTop() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        DistinctIterable<String> output = mongoTemplate.getCollection(collection)
                .distinct("metadata.authors.affiliation.institution", String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        int i = 0;
        while (it.hasNext() && i < 50) {
            results.add(it.next());
            i++;
        }
        return results;
    }
    
    @Override
    public List<String> getDistinctAffiliations() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        DistinctIterable<String> output = mongoTemplate.getCollection(collection)
                .distinct("metadata.authors.affiliation.selectedWikiarticle.title", String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        while (it.hasNext()) {
            results.add(it.next());
        }
        return results;
    }

    @Override
    public List<String> getDistinctAffiliationsTop() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        DistinctIterable<String> output = mongoTemplate.getCollection(collection)
                .distinct("metadata.authors.affiliation.selectedWikiarticle.title", String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        int i = 0;
        while (it.hasNext() && i < 50) {
            results.add(it.next());
            i++;
        }
        return results;
    }
    
    @Override
    public long getDistinctAffiliationCount() {
        Query query = new Query(Criteria.where("metadata.authors.affiliation.selectedWikiarticle.title").ne(null));
        return mongoTemplate.count(query, PublicationImpl.class);
    }

    @Override
    public long getPublicationCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        return mongoTemplate.getCollection(collection).countDocuments();
    }

    @Override
    public long getAffiliationCount() {
        Query query = new Query(Criteria.where("metadata.authors.affiliation.institution").ne(null));
        return mongoTemplate.count(query, PublicationImpl.class);
    }

    @Override
    public long getYearCount() {
        Query query = new Query(Criteria.where("publishYear").ne(0));
        return mongoTemplate.count(query, PublicationImpl.class);
    }

    @Override
    public long getJournalCount() {
        Query query = new Query(Criteria.where("journal").ne(null));
        return mongoTemplate.count(query, PublicationImpl.class);
    }

    @Override
    public List<Integer> getYears() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        DistinctIterable<Integer> output = mongoTemplate.getCollection(collection).distinct("publishYear",
                Integer.class);
        List<Integer> results = new ArrayList<>();
        MongoCursor<Integer> it = output.iterator();
        while (it.hasNext()) {
            results.add(it.next());
        }
        return results;
    }

    @Override
    public List<String> getJournals() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        DistinctIterable<String> output = mongoTemplate.getCollection(collection).distinct("journal", String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        while (it.hasNext()) {
            results.add(it.next());
        }
        return results;
    }

    @Override
    public long getCountriesInTextCount() {
        Query query = new Query(Criteria.where("bodyText.locationMatches.selectedArticle").ne(null));
        return mongoTemplate.count(query, PublicationImpl.class);
    }

    @Override
    public List<String> getCountriesInText() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        Criteria criteria = Criteria.where("bodyText.locationMatches.selectedArticle").ne(null);
        Query query = new Query();
        query.addCriteria(criteria);

        DistinctIterable<String> output = mongoTemplate.getCollection(collection)
                .distinct("bodyText.locationMatches.locationName", query.getQueryObject(), String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        while (it.hasNext()) {
            results.add(it.next());
        }
        return results;
    }
    
    @Override
    public List<String> getCountriesInTextTop() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        Criteria criteria = Criteria.where("bodyText.locationMatches.selectedArticle").ne(null);
        Query query = new Query();
        query.addCriteria(criteria);
        
        DistinctIterable<String> output = mongoTemplate.getCollection(collection)
                .distinct("bodyText.locationMatches.locationName", query.getQueryObject(), String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        int i = 0;
        while (it.hasNext() && i < 50) {
            results.add(it.next());
            i++;
        }
        return results;
    }

    @Override
    public long getCountOfPublicationsWithLocation() {
        Query query = new Query(Criteria.where("metadata.authors.affiliation.selectedWikiarticle").ne(null));
        return mongoTemplate.count(query, PublicationImpl.class);
    }

}
