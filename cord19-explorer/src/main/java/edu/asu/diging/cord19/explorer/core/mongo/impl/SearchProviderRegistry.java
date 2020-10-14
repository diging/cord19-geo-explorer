package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Service
public class SearchProviderRegistry {

    @Autowired
    private List<SearchProvider> searchProviders;

    Map<SearchType, SearchProvider> map = new HashMap<>();

    public SearchProvider getProvider(SearchType searchType) {
        if (map.size() == 0) {
            for (SearchProvider search : searchProviders) {
                map.put(search.getSearchType(), search);
            }
        }
        return map.get(searchType);
    }

}
