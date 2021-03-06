package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Service
public class AffiliationSearchProviderImpl implements SearchProvider {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Returns a {@code List<AffiliationPaperAggregationOutput>} instance
     * representing the affiliations matched with the input parameter 'title' .
     * 
     * @param title       Aggregation title to be queried
     * 
     * @param currentPage index of current page starting from 0
     * 
     * @param size        length of result sublist
     * 
     * @return List of AffiliationPaperAggregationOutput instances containing the
     *         'title' string
     * 
     **/
    @Override
    public List<AffiliationPaperAggregationOutput> search(String title, Long currentPage, Integer size) {
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        GroupOperation group = Aggregation.group("metadata.authors.affiliation.institution")
                .first("metadata.authors.affiliation.selectedWikiarticle.title").as("wiki");
        SortOperation sort = Aggregation.sort(Sort.by(Order.asc("_id")));
        SkipOperation skip = Aggregation.skip(currentPage * size);
        LimitOperation limit = Aggregation.limit(size);
        Criteria regex = Criteria.where("wiki").regex(".*" + title + ".*", "i");
        MatchOperation match = Aggregation.match(regex);

        Aggregation aggregation = Aggregation.newAggregation(unwind, group, match, sort, skip, limit);

        AggregationResults<AffiliationPaperAggregationOutput> results = aggregate(aggregation);

        return results.getMappedResults();
    }

    @Override
    public long getTotalResults(String title) {
        UnwindOperation unwind = Aggregation.unwind("metadata.authors");
        GroupOperation group = Aggregation.group("metadata.authors.affiliation.institution")
                .first("metadata.authors.affiliation.selectedWikiarticle.title").as("wiki");
        Criteria regex = Criteria.where("wiki").regex(".*" + title + ".*", "i");
        MatchOperation match = Aggregation.match(regex);

        Aggregation aggregation = Aggregation.newAggregation(unwind, group, match,
                Aggregation.group().count().as("count"));

        AggregationResults<AffiliationPaperAggregationOutput> results = aggregate(aggregation);

        return results.getMappedResults().size() != 0 ? Long.parseLong(results.getMappedResults().get(0).getCount())
                : 0;

    }

    @Override
    public SearchType getSearchType() {
        return SearchType.AFFILIATIONS;
    }

    private AggregationResults<AffiliationPaperAggregationOutput> aggregate(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, PublicationImpl.class, AffiliationPaperAggregationOutput.class);

    }
}
