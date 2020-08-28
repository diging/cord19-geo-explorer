package edu.asu.diging.cord19.explorer.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.AffiliationSearchProvider;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationSearchProvider;
import edu.asu.diging.cord19.explorer.core.mongo.impl.AffiliationPaperAggregationOutput;
import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Controller
public class SearchController {

    
    @Autowired
    private AffiliationSearchProvider affSearchProvider;
    
    @Autowired
    private PublicationSearchProvider pubSearchProvider;
    
    
    
    
    @RequestMapping(value = "/search")
    public String search(@RequestParam("searchType") String searchType,@RequestParam("search") String query, Model model,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if (searchType.equals(SearchType.AFFILIATIONS.toString())) {
            List<AffiliationPaperAggregationOutput> matchedAffiliationsList = affSearchProvider.getRequestedPage(query, (long)pageable.getPageNumber(),
                    pageable.getPageSize());
            long affCount = affSearchProvider.searchResultSize(query);
            model.addAttribute("matchedAffiliationsPage", matchedAffiliationsList);
            addAttribute(model, pageable, query, affCount);
            return "affResults";
            
            
        } else  {
            List<PublicationImpl> matchedPublicationsList = pubSearchProvider.getRequestedPage(query, (long)pageable.getPageNumber(),
                    pageable.getPageSize());
            long pubCount = pubSearchProvider.searchResultSize(query);
            addAttribute(model, pageable, query, pubCount);
            model.addAttribute("matchedPublicationsPage", matchedPublicationsList);
            return "results";
            
        }
    }
    
    private void addAttribute(Model model,Pageable pageable, String query, long count) {
        model.addAttribute("total", count);
        model.addAttribute("query", query);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount", count/pageable.getPageSize() + (count%pageable.getPageSize() > 0 ? 1 : 0));
        
    }
}
