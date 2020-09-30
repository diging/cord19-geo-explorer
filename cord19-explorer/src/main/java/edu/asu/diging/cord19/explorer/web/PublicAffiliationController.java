package edu.asu.diging.cord19.explorer.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class PublicAffiliationController {

    @Autowired
    private PublicationRepository pubRepo;

    @RequestMapping("/publications")
    public String findPublications(@RequestParam("name") String affiliation, Model model) {
        List<PublicationImpl> pubs = pubRepo.findByMetadataAuthorsAffiliationInstitution(affiliation);
        model.addAttribute("publications", pubs);
        model.addAttribute("institution", affiliation);
        return "publicAffiliations";
    }
}
