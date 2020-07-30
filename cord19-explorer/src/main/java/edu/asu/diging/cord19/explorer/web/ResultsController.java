package edu.asu.diging.cord19.explorer.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationSearchProvider;

@Controller
public class ResultsController {

    @Autowired
    private PublicationSearchProvider pubSearchProvider;
    
    @Autowired
    private Environment env;

    @RequestMapping(value = "/search")
    public String search(@RequestParam("search") String query, Model model,
            @PageableDefault(size = 20) Pageable pageable) {
        int pageSize = Integer.parseInt(env.getRequiredProperty("page.size"));
        List<PublicationImpl> matchedPage = pubSearchProvider.getRequestedPage(query, (long)pageable.getPageNumber(),
                pageable.getPageSize());
        long pubCount = pubSearchProvider.searchResultSize(query);

        model.addAttribute("matchedPublicationsPage", matchedPage);
        model.addAttribute("pageCount", pubCount/pageable.getPageSize() + (pubCount%pageable.getPageSize() > 0 ? 1 : 0));
        model.addAttribute("query", query);
        model.addAttribute("page", pageable.getPageNumber());
        return "results";
    }


}
