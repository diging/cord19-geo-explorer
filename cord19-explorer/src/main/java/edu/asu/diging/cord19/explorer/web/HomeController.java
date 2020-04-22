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
    	model.addAttribute("countries", pubDao.getCountries());
    	model.addAttribute("years", pubDao.getYears());
    	model.addAttribute("textCountries", pubDao.getCountriesInText());
        return "home";
    }
}