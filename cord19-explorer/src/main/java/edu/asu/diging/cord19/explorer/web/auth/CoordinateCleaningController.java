package edu.asu.diging.cord19.explorer.web.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.service.CoordinateCleaningManager;




@Controller
public class CoordinateCleaningController {
    
    @Autowired
    private CoordinateCleaningManager cleaningManager;
    
    @RequestMapping(value = "/auth/coordinates/clean", method = RequestMethod.GET)
    public String show() {

        return "auth/cleanCoordinates";
    }
    
    @RequestMapping(value = "/auth/coordinates/clean", method = RequestMethod.POST)
    public String start() throws ClassCastException, ClassNotFoundException, IOException {
		cleaningManager.startCleaningCoordinates();
        
        return "redirect:/";
    }
    
}