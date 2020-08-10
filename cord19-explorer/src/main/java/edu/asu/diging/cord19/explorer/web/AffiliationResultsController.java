package edu.asu.diging.cord19.explorer.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.mongo.AffiliationSearchProvider;
import edu.asu.diging.cord19.explorer.core.mongo.impl.AffiliationPaperAggregationOutput;

@Controller
public class AffiliationResultsController {
    
    @Autowired
    private AffiliationSearchProvider affSearchProvider;
    
    
    @RequestMapping(value = "/affSearch")
    public String search(@RequestParam("search") String query, Model model,
            @PageableDefault(size = 20) Pageable pageable) {
        List<AffiliationPaperAggregationOutput> matchedPage = affSearchProvider.getRequestedPage(query, (long)pageable.getPageNumber(),
                pageable.getPageSize());
        long pubCount = matchedPage.size();
        model.addAttribute("total", pubCount);
        model.addAttribute("matchedAffiliationsPage", matchedPage);
        model.addAttribute("pageCount", pubCount/pageable.getPageSize() + (pubCount%pageable.getPageSize() > 0 ? 1 : 0));
        model.addAttribute("query", query);
        model.addAttribute("page", pageable.getPageNumber());
        return "affResults";
    }

    

}
