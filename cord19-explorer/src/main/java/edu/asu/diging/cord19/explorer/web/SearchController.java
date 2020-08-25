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

@Controller
public class SearchController {

    
    @Autowired
    private AffiliationSearchProvider affSearchProvider;
    
    @Autowired
    private PublicationSearchProvider pubSearchProvider;
    
    
    
    
    @RequestMapping(value = "/search")
    public String search(@RequestParam("searchType") String searchType,@RequestParam("search") String query, Model model,
            @PageableDefault(size = 20) Pageable pageable) {
        
        if (searchType.equals("affiliations")) {
            List<AffiliationPaperAggregationOutput> matchedPage = affSearchProvider.getRequestedPage(query, (long)pageable.getPageNumber(),
                    pageable.getPageSize());
            long affCount = matchedPage.size();
            model.addAttribute("total", affCount);
            model.addAttribute("matchedAffiliationsPage", matchedPage);
            model.addAttribute("pageCount", affCount/pageable.getPageSize() + (affCount%pageable.getPageSize() > 0 ? 1 : 0));
            model.addAttribute("query", query);
            model.addAttribute("page", pageable.getPageNumber());
            return "affResults";
            
            
        } else  {
            List<PublicationImpl> matchedPage = pubSearchProvider.getRequestedPage(query, (long)pageable.getPageNumber(),
                    pageable.getPageSize());
            long pubCount = pubSearchProvider.searchResultSize(query);
            model.addAttribute("total", pubCount);
            model.addAttribute("matchedPublicationsPage", matchedPage);
            model.addAttribute("pageCount", pubCount/pageable.getPageSize() + (pubCount%pageable.getPageSize() > 0 ? 1 : 0));
            model.addAttribute("query", query);
            model.addAttribute("page", pageable.getPageNumber());
            return "results";
            
        }
    }
}
