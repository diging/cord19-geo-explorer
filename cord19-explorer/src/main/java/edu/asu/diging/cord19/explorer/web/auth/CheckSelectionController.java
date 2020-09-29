package edu.asu.diging.cord19.explorer.web.auth;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaSelectionStatus;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class CheckSelectionController {

    @Autowired
    private PublicationRepository repo;

    @RequestMapping(value = "/auth/affiliation/status", method = RequestMethod.POST)
    public String post(String affiliation, String status, String page) {
        // FIXME: needs to go into manager class
        List<PublicationImpl> pubs = repo.findByMetadataAuthorsAffiliationInstitution(affiliation);
        for (Publication pub : pubs) {
            for (PersonImpl person : pub.getMetadata().getAuthors()) {
                if (person.getAffiliation().getInstitution() != null
                        && person.getAffiliation().getInstitution().equals(affiliation)) {
                    person.getAffiliation().setSelectionStatus(WikipediaSelectionStatus.valueOf(status));
                    person.getAffiliation().setSelectionCheckedOn(OffsetDateTime.now().toString());
                    repo.save((PublicationImpl) pub);
                }
            }
        }
        return "redirect:/auth/affiliations?page=" + page;
    }
}
