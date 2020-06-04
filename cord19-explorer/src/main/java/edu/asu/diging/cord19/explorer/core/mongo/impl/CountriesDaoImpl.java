package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;

import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;
import edu.asu.diging.cord19.explorer.core.mongo.CountriesDao;



@Service
public class CountriesDaoImpl implements CountriesDao {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public List<String> getCoordinates() {
        String collection = mongoTemplate.getCollectionName(CountriesImpl.class);
        DistinctIterable<String> output = mongoTemplate.getCollection(collection)
                .distinct("geometry.coordinates", String.class);
        List<String> results = new ArrayList<>();
        MongoCursor<String> it = output.iterator();
        while (it.hasNext()) {
            results.add(it.next());
        }
        return results;
    }
    
}