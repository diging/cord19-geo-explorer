package edu.asu.diging.cord19.explorer.core.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;

@Component
public class CountryManagerImpl implements CountryManager {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.CountryManager#getCountries()
     */
    public ArrayList<String> getCountries() {
		ArrayList<String> countriesList = new ArrayList<String>();
		try(CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(),CountriesImpl.class)) {
			while (countries.hasNext()) {
				CountriesImpl country = countries.next();
				if (country.getProperties().getSelectedWikipediaCount() > 0) {
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.disableDefaultTyping();
					SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept("id", "@type");
					FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);
					String str = objectMapper.writer(filters).writeValueAsString(country);
					countriesList.add(str);
				}
			}
		} catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return countriesList;
	}
}
