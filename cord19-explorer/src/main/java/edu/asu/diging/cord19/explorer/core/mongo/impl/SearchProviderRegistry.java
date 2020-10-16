package edu.asu.diging.cord19.explorer.core.mongo.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Service
public class SearchProviderRegistry {

    @Autowired
    private List<SearchProvider> searchProviders;

    private Map<SearchType, String> views = new HashMap<>();

    private Map<SearchType, SearchProvider> map = new HashMap<>();

    @PostConstruct
    public void init() {
        for (SearchProvider search : searchProviders) {
            map.put(search.getSearchType(), search);
        }
        views.put(SearchType.AFFILIATIONS, "affResults");
        views.put(SearchType.PUBLICATIONS, "results");
    }

    public SearchProvider getProvider(SearchType searchType) {
        return map.get(searchType);
    }

    public String getView(SearchType searchType) {
        return views.get(searchType);
    }

}
