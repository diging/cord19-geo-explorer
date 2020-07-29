package edu.asu.diging.cord19.explorer.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
            @RequestParam("page") Optional<Integer> currentPage, @RequestParam("size") Optional<Integer> size,
            @RequestParam("totalPages") Optional<Long> totalPageNumbers) {
        int pageSize = Integer.parseInt(env.getRequiredProperty("page.size"));
        if (size.isPresent() && size.get() < 5) {
            size = Optional.of(pageSize);
        }
        List<PublicationImpl> matchedPage = pubSearchProvider.getRequestedPage(query, currentPage.orElse(1) - 1,
                size.orElse(pageSize));

        long totalPages = totalPageNumbers.orElse(pubSearchProvider.searchResultSize(query) / size.orElse(pageSize));
        addAttribute(model, matchedPage, totalPages, query);
        return "results";
    }

    private void addAttribute(Model model, List<PublicationImpl> pageResult, long totalPages, String query) {
        model.addAttribute("matchedPublicationsPage", pageResult);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("query", query);
        if (totalPages > 0) {
            List<Long> pageNumbers = LongStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());    
            model.addAttribute("pageNumbers", pageNumbers);
        }
    }

}
