package edu.asu.diging.cord19.explorer.web.auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class GetCoordinatesController {
    
    @Autowired
    private PublicationDao pubDao;
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @RequestMapping(value = "/auth/locate", method=RequestMethod.GET)
    public String show() {
        
        return "auth/locate";
    }
    
    @RequestMapping(value = "/auth/locate", method=RequestMethod.POST)
    public String getCoordinates(Model model) {
        model.addAttribute("countries", pubDao.getCollection());
        System.out.print(pubRepo.findAll());
        return "coordinates";
        
    }
}