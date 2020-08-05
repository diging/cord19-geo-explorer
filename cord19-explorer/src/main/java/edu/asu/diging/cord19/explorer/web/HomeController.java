package edu.asu.diging.cord19.explorer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationStatsProvider;

@Controller
public class HomeController {

    @Autowired
    private PublicationDao pubDao;
    @Autowired
    private PublicationStatsProvider provider;
    @Autowired
    private PublicationRepository repo;

    @RequestMapping(value = "/")
    public String home(Model model) {
        model.addAttribute("countries", pubDao.getCountries());
        model.addAttribute("cleanedAffiliations", pubDao.getDistinctAffiliations());
        model.addAttribute("authorCount", provider.getAuthorCount());
        model.addAttribute("paperWithAffCount", provider.getPaperWithAuthorAffiliationCount());
        model.addAttribute("years", pubDao.getYears());
        model.addAttribute("totalPublications", repo.count());
        model.addAttribute("textCountries", pubDao.getCountriesInText());
        model.addAttribute("publicationCount", pubDao.getPublicationCount());
        model.addAttribute("affiliationCount", pubDao.getAffiliationCount());
        model.addAttribute("cleanedAffiliationCount", pubDao.getDistinctAffiliationCount());
        model.addAttribute("yearCount", pubDao.getYearCount());
        model.addAttribute("journals", pubDao.getJournals());
        model.addAttribute("journalCount", pubDao.getJournalCount());
        model.addAttribute("textCountriesCount", pubDao.getCountriesInTextCount());
        return "home";
    }
}