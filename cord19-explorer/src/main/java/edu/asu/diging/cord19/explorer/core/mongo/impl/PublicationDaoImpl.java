package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;

@Service
public class PublicationDaoImpl implements PublicationDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.mongo.impl.PublicationDao#getCountries()
	 */
	@Override
	public List<String> getCountries() {
		String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
		DistinctIterable<String> output = mongoTemplate.getCollection(collection).distinct("metadata.authors.affiliation.locationCountry", String.class);
		List<String> results = new ArrayList<>();
		MongoCursor<String> it = output.iterator();
		while (it.hasNext()) {
			results.add(it.next());
		}
		return results;
	}
	
	@Override
	public List<Integer> getYears() {
		String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
		DistinctIterable<Integer> output = mongoTemplate.getCollection(collection).distinct("publishYear", Integer.class);
		List<Integer> results = new ArrayList<>();
		MongoCursor<Integer> it = output.iterator();
		while (it.hasNext()) {
			results.add(it.next());
		}
		return results;
	}
	
	@Override
	public List<String> getCountriesInText() {
		String collection = mongoTemplate.getCollectionName(PublicationImpl.class);
		DistinctIterable<String> output = mongoTemplate.getCollection(collection).distinct("bodyText.locationMatches.locationName", String.class);
		List<String> results = new ArrayList<>();
		MongoCursor<String> it = output.iterator();
		while (it.hasNext()) {
			results.add(it.next());
		}
		return results;
	}
	
}
