package edu.asu.diging.cord19.explorer.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.asu.diging.cord19.explorer.core.data.MapTotalsRepository;
import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.MapTotalsImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationStatsProvider;
import edu.asu.diging.cord19.explorer.core.service.impl.CountryManager;

@Controller
public class HomeController {

    @Autowired
    private PublicationDao pubDao;
    @Autowired
    private PublicationStatsProvider provider;
    @Autowired
    private PublicationRepository repo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MapTotalsRepository totalsRepo;
    @Autowired
    private CountryManager countryManager;

    @RequestMapping(value = "/")
    public String home(Model model) throws JsonProcessingException {
        model.addAttribute("authorCount", provider.getAuthorCount());
        model.addAttribute("paperWithAffCount", provider.getPaperWithAuthorAffiliationCount());
        model.addAttribute("years", pubDao.getYears());
        model.addAttribute("totalPublications", repo.count());
        model.addAttribute("journalCount", pubDao.getJournalCount());
        
        ArrayList<String> countriesList = countryManager.getCountries();
        
        MapTotalsImpl totals = totalsRepo.findFirstByOrderByIdDesc();
        model.addAttribute("countries", countriesList);
        model.addAttribute("low", totals.getLowCount());
        model.addAttribute("high", totals.getHighCount());
        return "home";
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/loginFailed")
    public String loginFailed(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }
}