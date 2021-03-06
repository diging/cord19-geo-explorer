package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
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
    public List<AffiliationPaperAggregationOutput> getAffiliationsAndArticles(long start, long pageSize) {
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");

        GroupOperation group = Aggregation.group("metadata.authors.affiliation.institution").first("metadata.authors.affiliation.selectedWikiarticle.title").as("wiki")
                .first("metadata.authors.affiliation.selectionStatus").as("status")
                .first("metadata.authors.affiliation.locationSettlement").as("settlement")
                .first("metadata.authors.affiliation.locationCountry").as("country")
                .first("metadata.authors.affiliation.selectedWikiarticle.coordinates").as("coord")
                .first("metadata.authors.affiliation.selectedWikiarticle.locationType").as("locType")
                .push("paperId").as("paperId").push(new BasicDBObject
                        ("firstName", "$metadata.authors.first").append
                        ("lastName", "$metadata.authors.last")).as("authors");


        SortOperation sort = Aggregation.sort(Sort.by(Order.asc("_id")));

        SkipOperation skip = Aggregation.skip(start);
        LimitOperation limit = Aggregation.limit(pageSize);
        

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).cursorBatchSize(500).build();
        Aggregation aggregation = Aggregation.newAggregation(unwind, group, sort, skip, limit).withOptions(options);
        
        AggregationResults<AffiliationPaperAggregationOutput> results = mongoTemplate.aggregate(aggregation,
                PublicationImpl.class, AffiliationPaperAggregationOutput.class);
        return results.getMappedResults();
    }
    
    @Override
    public long getTotalAffiliation() {
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");

        GroupOperation group = Aggregation.group("metadata.authors.affiliation.institution");

        CountOperation count = Aggregation.count().as("total");
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).cursorBatchSize(500).build();
        Aggregation aggregation = Aggregation.newAggregation(unwind, group, count).withOptions(options);
        
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation,
                PublicationImpl.class, Document.class);
        return results.getRawResults().getLong("count");
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
