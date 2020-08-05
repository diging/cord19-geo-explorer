package edu.asu.diging.cord19.explorer.web.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.service.DocumentCleaningManager;

@Controller
public class PublicationCleaningController {

    
    @Autowired
    private DocumentCleaningManager manager;
    
    @RequestMapping(value="/auth/publications/clean", method=RequestMethod.GET)
    public String get() {
        return "auth/cleanPublications";
    }
    
    @RequestMapping(value="/auth/publications/clean", method=RequestMethod.POST)
    public String post() {
        manager.startDuplicateRemoval();
        return "auth/cleaningStarted";
    }
}
