package edu.asu.diging.cord19.explorer.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.mongo.impl.SearchProvider;
import edu.asu.diging.cord19.explorer.core.mongo.impl.SearchProviderRegistry;
import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Controller
public class SearchController {

    @Autowired
    private SearchProviderRegistry searchProviderRegistry;

    @RequestMapping(value = "/{searchType}/search")
    public String search(@PathVariable("searchType") String searchType, @RequestParam("search") String query,
            Model model, @PageableDefault(size = 20) Pageable pageable) {

        if (searchType.equalsIgnoreCase(SearchType.AFFILIATIONS.toString())) {
            addAttributes(model, pageable, query, searchProviderRegistry.getProvider(SearchType.AFFILIATIONS),
                    "matchedAffiliationsPage");
            return "affResults";

        } else {
            addAttributes(model, pageable, query, searchProviderRegistry.getProvider(SearchType.PUBLICATIONS),
                    "matchedPublicationsPage");
            return "results";

        }
    }

    private void addAttributes(Model model, Pageable pageable, String query, SearchProvider searchProvider,
            String listType) {
        long count = searchProvider.getTotalResults(query);
        model.addAttribute(listType,
                searchProvider.search(query, (long) pageable.getPageNumber(), pageable.getPageSize()));
        model.addAttribute("total", count);
        model.addAttribute("query", query);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount", count / pageable.getPageSize() + (count % pageable.getPageSize() > 0 ? 1 : 0));

    }
}
