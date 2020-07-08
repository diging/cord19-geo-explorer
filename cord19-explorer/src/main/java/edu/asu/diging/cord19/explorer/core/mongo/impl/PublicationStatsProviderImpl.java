package edu.asu.diging.cord19.explorer.core.mongo.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;


import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaSelectionStatus;
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
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.mongo.impl.PublicationStatsProvider#getPaperWithAuthorAffiliationCount()
     */
    @Override
    public long getPaperWithAuthorAffiliationCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        MatchOperation match = Aggregation.match(Criteria.where("metadata.authors.affiliation.locationCountry").exists(true));
        GroupOperation group = Aggregation.group("paperId");
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, match, group, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
     
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.mongo.impl.PublicationStatsProvider#getPaperWithoutAuthorAffiliationCount()
     */
    @Override
    public long getPaperWithoutAuthorAffiliationCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        MatchOperation match = Aggregation.match(Criteria.where("metadata.authors").not().elemMatch(Criteria.where("affiliation.locationCountry").exists(true)));
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(match, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    @Override
    public long getAuthorWithAffiliationCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        MatchOperation match = Aggregation.match(Criteria.where("metadata.authors.affiliation.locationCountry").exists(true));
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, match, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    @Override
    public long getAuthorWithAffiliationAndWikiarticleCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        MatchOperation match1 = Aggregation.match(Criteria.where("metadata.authors.affiliation.locationCountry").exists(true));
        MatchOperation match2 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectedWikiarticle").exists(true));
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, match1, match2, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    @Override
    public long getPaperWithAtLeastOneAffiliationAndWikiarticleCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        MatchOperation match1 = Aggregation.match(Criteria.where("metadata.authors.affiliation.locationCountry").exists(true));
        MatchOperation match2 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectedWikiarticle").exists(true));
        GroupOperation group = Aggregation.group("paperId");
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, match1, match2, group, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    @Override
    public long getAuthorsWithAffiliationAndIncorrectWikiarticleCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        MatchOperation match1 = Aggregation.match(Criteria.where("metadata.authors.affiliation.locationCountry").exists(true));
        MatchOperation match2 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectedWikiarticle").exists(true));
        MatchOperation match3 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectionStatus").is(WikipediaSelectionStatus.INCORRECT));
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, match1, match2, match3, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    @Override
    public long getPapersWithAffiliationAndIncorrectWikiarticleCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        MatchOperation match1 = Aggregation.match(Criteria.where("metadata.authors.affiliation.locationCountry").exists(true));
        MatchOperation match2 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectedWikiarticle").exists(true));
        MatchOperation match3 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectionStatus").is(WikipediaSelectionStatus.INCORRECT));
        GroupOperation group = Aggregation.group("paperId");
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, match1, match2, match3, group, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
    
    @Override
    public long getAuthorsWithAffiliationAndCorrectRegionWikiarticleCount() {
        String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
        
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        MatchOperation match1 = Aggregation.match(Criteria.where("metadata.authors.affiliation.locationCountry").exists(true));
        MatchOperation match2 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectedWikiarticle").exists(true));
        MatchOperation match3 = Aggregation.match(Criteria.where("metadata.authors.affiliation.selectionStatus").is(WikipediaSelectionStatus.CORRECT_REGION));
        CountOperation count  = Aggregation.count().as("total");
        
        Aggregation aggregation = Aggregation.newAggregation(unwind, match1, match2, match3, count);
        AggregationResults<Document> doc = mongoTemplate.aggregate(aggregation, collection, Document.class); 
        
        if (doc.getMappedResults().isEmpty()) {
            return 0;
        }
        return doc.getMappedResults().get(0).getInteger("total");
    }
}
