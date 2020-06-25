package edu.asu.diging.cord19.explorer.core.mongo.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;



import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationStatsProvider;


@Controller
public class PublicationStatsProviderImpl implements PublicationStatsProvider {

    @Autowired
    private MongoTemplate mongoTemplate;

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.mongo.impl.PublicationStatsProvider#getAuthorCount()
     */
    @Override
    public long getAuthorCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        CountOperation count = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.mongo.impl.PublicationStatsProvider#getPaperWithAuthorAffiliationCount()
     */
    @Override
    public long getPaperWithAuthorAffiliationCount() {
        Query query = new Query(Criteria.where("metadata.authors.affiliation.locationCountry").ne(null));
        return mongoTemplate.count(query, PublicationImpl.class);
    }
     
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.mongo.impl.PublicationStatsProvider#getPaperWithoutAuthorAffiliationCount()
     */
    @Override
    public long getPaperWithoutAuthorAffiliationCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        MatchOperation match = Aggregation.match(Criteria.where("metadata.authors").elemMatch(Criteria.where("affiliation.locationCountry").exists(false)));
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(match, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        return doc.getMappedResults().get(0).getInteger("total");
    }
}
