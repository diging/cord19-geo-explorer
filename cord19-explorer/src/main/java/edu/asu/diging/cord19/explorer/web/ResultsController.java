package edu.asu.diging.cord19.explorer.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @RequestMapping(value = "/result")
    public String search(@RequestParam("search") String title, Model model, @RequestParam("page") Optional<Integer> page, 
            @RequestParam("size") Optional<Integer> size) {
        List<PublicationImpl> matched =  pubSearchProvider.searchPublicationTitles(title);
        
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<PublicationImpl> matchedPage = pubSearchProvider.paginateResults(PageRequest.of(currentPage - 1, pageSize), matched);
        
        model.addAttribute("matchedPublicationsPage", matchedPage);
        int totalPages = matchedPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("title", title);
        return "results";
    }

}
