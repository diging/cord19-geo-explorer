package edu.asu.diging.cord19.explorer.web;

import java.util.List;

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
        List<PublicationImpl> matched = pubSearchProvider.searchPublicationTitles(title);
        model.addAttribute("matchedPublications", matched);
        return "results";
    }

}
