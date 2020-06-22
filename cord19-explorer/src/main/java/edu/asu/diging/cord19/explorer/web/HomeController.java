package edu.asu.diging.cord19.explorer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;

@Controller
public class HomeController {

    @Autowired
    private PublicationDao pubDao;

    @RequestMapping(value = "/")
    public String home(Model model) {
        //model.addAttribute("countries", pubDao.getCountries());
        //model.addAttribute("cleanedAffiliations", pubDao.getDistinctAffiliations());
        //model.addAttribute("years", pubDao.getYears());
        //model.addAttribute("textCountries", pubDao.getCountriesInText());
        model.addAttribute("publicationCount", pubDao.getPublicationCount());
        model.addAttribute("affiliationCount", pubDao.getAffiliationCount());
        model.addAttribute("cleanedAffiliationCount", pubDao.getDistinctAffiliationCount());
        model.addAttribute("yearCount", pubDao.getYearCount());
        //model.addAttribute("journals", pubDao.getJournals());
        model.addAttribute("journalCount", pubDao.getJournalCount());
        model.addAttribute("textCountriesCount", pubDao.getCountriesInTextCount());
        return "home";
    }
}