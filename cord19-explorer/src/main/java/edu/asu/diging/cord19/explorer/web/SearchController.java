package edu.asu.diging.cord19.explorer.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.impl.AffiliationPaperAggregationOutput;
import edu.asu.diging.cord19.explorer.core.mongo.impl.SearchProvider;
import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Controller
public class SearchController {

    @Autowired
    private Map<SearchType, SearchProvider> searchProviderRegistry;

    @RequestMapping(value = "/search")
    public String search(@RequestParam("searchType") String searchType, @RequestParam("search") String query,
            Model model, @PageableDefault(size = 20) Pageable pageable) {

        if (searchType.equals(SearchType.AFFILIATIONS.toString())) {
            @SuppressWarnings("unchecked")
            List<AffiliationPaperAggregationOutput> matchedAffiliationsList = (List<AffiliationPaperAggregationOutput>) searchProviderRegistry
                    .get(SearchType.AFFILIATIONS)
                    .search(query, (long) pageable.getPageNumber(), pageable.getPageSize());
            long affCount = searchProviderRegistry.get(SearchType.AFFILIATIONS).getTotalResults(query);
            model.addAttribute("matchedAffiliationsPage", matchedAffiliationsList);
            addAttributes(model, pageable, query, affCount);
            return "affResults";

        } else {
            @SuppressWarnings("unchecked")
            List<PublicationImpl> matchedPublicationsList = (List<PublicationImpl>)searchProviderRegistry.get(SearchType.PUBLICATIONS).search(query,
                    (long) pageable.getPageNumber(), pageable.getPageSize());
            
            
            long pubCount = searchProviderRegistry.get(SearchType.PUBLICATIONS).getTotalResults(query);
            addAttributes(model, pageable, query, pubCount);
            model.addAttribute("matchedPublicationsPage", matchedPublicationsList);
            return "results";

        }
    }

    private void addAttributes(Model model, Pageable pageable, String query, long count) {
        model.addAttribute("total", count);
        model.addAttribute("query", query);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount", count / pageable.getPageSize() + (count % pageable.getPageSize() > 0 ? 1 : 0));

    }
}
