package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Configuration
public class SearchProviderRegistry {

    @Autowired
    private List<SearchProvider> searchProviders;
    
    
    @Bean
    public Map<SearchType, SearchProvider> initialize(){
        Map<SearchType, SearchProvider> map = new HashMap<>();
        for (SearchProvider search : searchProviders) {
            map.put(search.getSearchType(), search);
        }
        return map;
    }
    

}
