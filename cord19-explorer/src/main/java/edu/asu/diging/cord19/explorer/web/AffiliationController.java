package edu.asu.diging.cord19.explorer.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class AffiliationController {

    @Autowired
    private PublicationRepository pubRepo;

    @RequestMapping("/affiliation")
    public String findPublications(@RequestParam("name") String affiliation, Model model) {
        List<PublicationImpl> pubs = pubRepo.findByMetadataAuthorsAffiliationInstitution(affiliation);
        
        for (Publication pub : pubs) {
            if (pub.getMetadata() != null && pub.getMetadata().getAuthors() != null) {
                   pub.getMetadata().setAuthors(pub.getMetadata().getAuthors().stream().filter(a -> {
                       if (a.getAffiliation() != null && a.getAffiliation().getWikiarticles() != null) {
                           return a.getAffiliation().getInstitution() != null && a.getAffiliation().getInstitution().equals(affiliation);
                       }
                       return false;
                   }).collect(Collectors.toList())); 
                
            }
        }
        
       
        model.addAttribute("publications", pubs);
        model.addAttribute("institution", affiliation);
        return "affiliation";
    }
}
