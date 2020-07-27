package edu.asu.diging.cord19.explorer.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String search(@RequestParam("search") String title, Model model) {
        int resultSize = pubSearchProvider.searchResultSize(title);
        int currentPage = 1;
        int pageSize = 5;
        List<PublicationImpl> matchedPage = pubSearchProvider.getRequestedPage(title, currentPage-1, pageSize);
        model.addAttribute("matchedPublicationsPage", matchedPage);
        int totalPages = resultSize / pageSize;
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("title", title);
        return "results";
    }

    @RequestMapping(value = "/page")
    public String page(@RequestParam("search") String title, Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size, @RequestParam("totalPages") int totalPages) {

        if (size.isPresent() && size.get() < 5) {
            size = Optional.of(5);
        }
        List<PublicationImpl> pageResult = pubSearchProvider.getRequestedPage(title, page.orElse(1)-1, size.orElse(5));

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("matchedPublicationsPage", pageResult);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("title", title);
        return "results";
    }

}
