package edu.asu.diging.cord19.explorer.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.cord19.explorer.core.mongo.impl.SearchProviderRegistry;
import edu.asu.diging.cord19.explorer.web.model.SearchType;

@Controller
public class SearchController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SearchProviderRegistry searchProviderRegistry;

    private Map<SearchType, String> views = new HashMap<>();

    @PostConstruct
    public void init() {
        views.put(SearchType.AFFILIATIONS, "affResults");
        views.put(SearchType.PUBLICATIONS, "results");
    }

    @RequestMapping(value = "/{searchType}/search")
    public String search(@PathVariable("searchType") String searchType, @RequestParam("search") String query,
            Model model, @PageableDefault(size = 20) Pageable pageable, RedirectAttributes redirectAttrs) {
        SearchType search;
        try {
            searchType = searchType.toUpperCase();
            search = SearchType.valueOf(searchType);
        } catch (IllegalArgumentException e) {
            logger.error("searchType doesn't exist", e);
            redirectAttrs.addFlashAttribute("show_alert", true);
            redirectAttrs.addFlashAttribute("alert_type", "danger");
            redirectAttrs.addFlashAttribute("alert_msg", "Search Failed. Invalid search Type");
            return "redirect:/";
        }
        if (query.isEmpty()) {
            return "redirect:/";
        }
        long count = searchProviderRegistry.getProvider(search).getTotalResults(query);
        model.addAttribute("matchedResultsPage", searchProviderRegistry.getProvider(search).search(query,
                (long) pageable.getPageNumber(), pageable.getPageSize()));
        model.addAttribute("total", count);
        model.addAttribute("query", query);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount", count / pageable.getPageSize() + (count % pageable.getPageSize() > 0 ? 1 : 0));
        return views.get(search);

    }

}
