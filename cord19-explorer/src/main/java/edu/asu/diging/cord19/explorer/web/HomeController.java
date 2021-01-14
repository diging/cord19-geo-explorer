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

import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;
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
    @Autowired
    private MongoTemplate mongoTemplate;
    

    @RequestMapping(value = "/")
    public String home(Model model) throws JsonProcessingException {
        //model.addAttribute("countries", pubDao.getCountries());
        //model.addAttribute("cleanedAffiliations", pubDao.getDistinctAffiliations());
        model.addAttribute("authorCount", provider.getAuthorCount());
        model.addAttribute("paperWithAffCount", provider.getPaperWithAuthorAffiliationCount());
        model.addAttribute("years", pubDao.getYears());
        model.addAttribute("totalPublications", repo.count());
       // model.addAttribute("textCountries", pubDao.getCountriesInText());
        //model.addAttribute("publicationCount", pubDao.getPublicationCount());
        //model.addAttribute("affiliationCount", pubDao.getAffiliationCount());
        //model.addAttribute("cleanedAffiliationCount", pubDao.getDistinctAffiliationCount());
       // model.addAttribute("yearCount", pubDao.getYearCount());
        //model.addAttribute("journals", pubDao.getJournals());
        model.addAttribute("journalCount", pubDao.getJournalCount());
        //model.addAttribute("textCountriesCount", pubDao.getCountriesInTextCount());
        
		HashMap<String, List<String>> countriesMap = new HashMap<String, List<String>>();
		ArrayList<String> countriesList = new ArrayList<String>();
        //Is Atomic Int the best way to this?
        AtomicInteger highCount = new AtomicInteger(0);
        AtomicInteger lowCount = new AtomicInteger(0);
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(), CountriesImpl.class)) {
            while(countries.hasNext()) {
                CountriesImpl country = countries.next();
                if(country.getProperties().getSelectedWikipediaCount() > 0) {
                    ArrayList<String> properties = new ArrayList<String>();
                    properties.add(Integer.toString(country.getProperties().getSelectedWikipediaCount()));
                    properties.add(country.getProperties().getCountryCenter());     
                    if(country.getProperties().getSelectedWikipediaCount() > highCount.intValue()) {
                        highCount.set(country.getProperties().getSelectedWikipediaCount());
                    }
                    if(country.getProperties().getSelectedWikipediaCount() < lowCount.intValue()) {
                        lowCount.set(country.getProperties().getSelectedWikipediaCount());
					}
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.disableDefaultTyping();
					SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
					          .serializeAllExcept("id", "@type");
					      FilterProvider filters = new SimpleFilterProvider()
					          .addFilter("myFilter", theFilter);
					String str = objectMapper.writer(filters).writeValueAsString(country);
					countriesList.add(str);
                    //countriesMap.put();
                }
            }
        }
        model.addAttribute("countries", countriesList);
        model.addAttribute("low", lowCount);
        model.addAttribute("high", highCount);
        return "home";
    }
    
    @RequestMapping(value ="/login")
    public String login() {
      return "login";
    }
    @RequestMapping(value ="/loginFailed")
    public String loginFailed(Model model) {
      model.addAttribute("loginError", true);
      return "login";
    }
}