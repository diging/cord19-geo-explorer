package edu.asu.diging.cord19.explorer.web.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class AffiliationListController {

    @Autowired
    private PublicationRepository pubRepo;

    @RequestMapping("/affiliations")
    public String get(Model model) {
        return null;
    }
}
