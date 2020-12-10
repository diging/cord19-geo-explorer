package edu.asu.diging.cord19.explorer.web.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.model.impl.CleanedCoordinatesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
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