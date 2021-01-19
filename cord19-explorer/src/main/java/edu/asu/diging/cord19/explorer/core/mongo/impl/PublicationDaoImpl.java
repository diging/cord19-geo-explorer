package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.web.model.SortType;

@Service
public class PublicationDaoImpl implements PublicationDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    
    @Autowired
    private PublicationRepository pubRepo;

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

    /**
     * Returns a {@code List<PublicationImpl>} instance
     * representing all publications sorted based on sortType from @code Pageable instance.
     * 
     * @param pageable   The pagination instance with sort and current page information
     * 
     * @param firstPubId PublicationId of the first title on the current page
     * 
     * @param lastPubId PublicationId of the last title on the current page
     * 
     * @param currentPage index of current page starting from 0
     * 
     * @return List of PublicationImpl instances containing sorted by fields in {@code Pageable} instance
     * 
     **/
    @Override
    public List<PublicationImpl> getPublications(Pageable pageable, String firstPubId, String lastPubId,
            int currentPage) {

        int requestedPage = pageable.getPageNumber() + 1;
        PublicationImpl pub = null;
        if (requestedPage > currentPage) {
            pub = (PublicationImpl) pubRepo.findFirstByPaperId(lastPubId);
        } else {
            pub = (PublicationImpl) pubRepo.findFirstByPaperId(firstPubId);
        }

        String[] sortType = pageable.getSort().toString().split(": ");
        int limit = pageable.getPageSize();
        Criteria criteria1 = null;
        Criteria criteria2 = null;

        if (requestedPage == 1) {
            if (sortType[0].equals(SortType.PUBLISHYEAR.toString())) {
                criteria1 = Criteria.where(sortType[0]).gt(0);
                criteria2 = Criteria.where(sortType[0]).is(0);
            } else {
                criteria1 = Criteria.where(sortType[0]).gt("");
                criteria2 = Criteria.where(sortType[0]).is("");
            }
        } else if (requestedPage > currentPage) {
            if (sortType[0].equals(SortType.PUBLISHYEAR.toString())) {
                criteria1 = Criteria.where(sortType[0]).gt(pub.getPublishYear());
                criteria2 = Criteria.where(sortType[0]).is(pub.getPublishYear());
            } else if (sortType[0].equals(SortType.JOURNAL.toString())) {
                criteria1 = Criteria.where(sortType[0]).gt(pub.getJournal());
                criteria2 = Criteria.where(sortType[0]).is(pub.getJournal());
            } else {
                criteria1 = Criteria.where(sortType[0]).gt(pub.getMetadata().getTitle());
                criteria2 = Criteria.where(sortType[0]).is(pub.getMetadata().getTitle());
            }

            criteria2 = criteria2.andOperator(Criteria.where("id").gt(new ObjectId(pub.getId().toString())));

        } else {
            if (sortType[0].equals(SortType.PUBLISHYEAR.toString())) {
                criteria1 = Criteria.where(sortType[0]).lt(pub.getPublishYear());
                criteria2 = Criteria.where(sortType[0]).is(pub.getPublishYear());
            } else if (sortType[0].equals(SortType.JOURNAL.toString())) {
                criteria1 = Criteria.where(sortType[0]).lt(pub.getJournal());
                criteria2 = Criteria.where(sortType[0]).is(pub.getJournal());
            } else {
                criteria1 = Criteria.where(sortType[0]).lt(pub.getMetadata().getTitle());
                criteria2 = Criteria.where(sortType[0]).is(pub.getMetadata().getTitle());
            }
            criteria2 = criteria2.andOperator(Criteria.where("id").lt(new ObjectId(pub.getId().toString())));
        }

        Sort sort = Sort.by(new Order(sortType[1].equals("ASC") ? Direction.ASC : Direction.DESC, sortType[0]));
        if (pub != null)
            sort = sort.and(Sort
                    .by(new Order(sortType[1].equals("ASC") ? Direction.ASC : Direction.DESC, pub.getId().toString())));

        Query query = new Query(new Criteria().orOperator(criteria1, criteria2)).with(sort).limit(limit);

//        Query query = new Query(new Criteria().orOperator(criteria1, criteria2))
//                .with(Sort.by(new Order(sortType[1].equals("ASC") ? Direction.ASC : Direction.DESC, sortType[0]))
//                        .and(Sort.by(new Order(sortType[1].equals("ASC") ? Direction.ASC : Direction.DESC, pub.getId().toString())))).limit(limit);

        return mongoTemplate.find(query, PublicationImpl.class);
    }

    @Override
    public List<AffiliationPaperAggregationOutput> getAffiliationsAndArticles(long start, long pageSize) {
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");

        GroupOperation group = Aggregation.group("metadata.authors.affiliation.institution")
                .first("metadata.authors.affiliation.selectedWikiarticle.title").as("wiki")
                .first("metadata.authors.affiliation.selectionStatus").as("status")
                .first("metadata.authors.affiliation.locationSettlement").as("settlement")
                .first("metadata.authors.affiliation.locationCountry").as("country")
                .first("metadata.authors.affiliation.selectedWikiarticle.coordinates").as("coord")
                .first("metadata.authors.affiliation.selectedWikiarticle.locationType").as("locType").push("paperId")
                .as("paperId").push(new BasicDBObject("firstName", "$metadata.authors.first").append("lastName",
                        "$metadata.authors.last"))
                .as("authors");

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
        Aggregation aggregation = Aggregation.newAggregation(unwind, group, count);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, PublicationImpl.class,
                Document.class);
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
