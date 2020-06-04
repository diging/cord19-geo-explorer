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




@Controller
public class CoordinateCleaningController {
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    
    @RequestMapping(value = "/auth/coordinates/clean", method = RequestMethod.GET)
    public String show() {

        return "auth/cleanCoordinates";
    }
    
    @RequestMapping(value = "/auth/coordinates/clean", method = RequestMethod.POST)
    public String start() throws ClassCastException, ClassNotFoundException, IOException {
        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query(), PublicationImpl.class)) {
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                List<PersonImpl> authors = pub.getMetadata().getAuthors();
                for(PersonImpl author : authors) {
                    List<String> coords = new ArrayList();
                    List<Double> formattedCoords = new ArrayList();
                    if(author.getAffiliation().getSelectedWikiarticle() != null) {
                       WikipediaArticleImpl article = author.getAffiliation().getSelectedWikiarticle();
                       String [] splitted =  article.getCoordinates().split("\\|");
                       // split on pipes then use Ordinals to gather Degree, Mins, Secs then compute Decimal Degrees
                       //Refactor into function
                       for (String s: splitted) {      
                            if(s.matches("-?\\d+(\\.\\d+)?")) {
                                System.out.println(s);
                                coords.add(s);
                                
                            } else if(s.equals("N") || s.equals("E")){
                                if(coords.size() == 3) {
                                    float d = Float.parseFloat(coords.get(0));
                                    float m = Float.parseFloat(coords.get(1));
                                    float se = Float.parseFloat(coords.get(2));
                                    double dd = 0;
                                    if(d == 0) {
                                        dd = ((m / 60.0) + (se / 3600.0));
                                    } else {
                                        dd = Math.signum(d) * (Math.abs(d) + (m / 60.0) + (se / 3600.0));
                                    }
                                    formattedCoords.add(dd);
                                    coords.clear();
                                }
                                if(coords.size() == 2) {
                                    float d = Float.parseFloat(coords.get(0));
                                    float m = Float.parseFloat(coords.get(1));
                                    double dd = Math.signum(d) * (Math.abs(d) + (m / 60.0));
                                    formattedCoords.add(dd);
                                    coords.clear();
                                }
                                if(coords.size() == 1) {
                                    Double doubleCoord = Double.parseDouble(coords.get(0));
                                    formattedCoords.add(doubleCoord);
                                    coords.clear();
                                }
                               
                            } else if(s.equals("S") || s.equals("W")) {
                                // handle negative coords
                                if(coords.size() == 3) {
                                    float d = Float.parseFloat(coords.get(0));
                                    float m = Float.parseFloat(coords.get(1));
                                    float se = Float.parseFloat(coords.get(2));
                                    double dd = 0;
                                    if(d == 0) {
                                        dd = ((m / 60.0) + (se / 3600.0));
                                    } else {
                                        dd = Math.signum(d) * (Math.abs(d) + (m / 60.0) + (se / 3600.0));
                                    }
                                    dd = dd *-1;
                                    formattedCoords.add(dd);
                                    coords.clear();
                                }
                                if(coords.size() == 2) {
                                    float d = Float.parseFloat(coords.get(0));
                                    float m = Float.parseFloat(coords.get(1));
                                    double dd = Math.signum(d) * (Math.abs(d) + (m / 60.0));
                                    dd = dd *-1;
                                    formattedCoords.add(dd);
                                    coords.clear();
                                }
                                if(coords.size() == 1) {                
                                    Double doubleCoord = Double.parseDouble(coords.get(0)) * -1;
                                    formattedCoords.add(doubleCoord);
                                    coords.clear();
                                }
                            }
                       }
                       CleanedCoordinatesImpl cleanedCoords = new CleanedCoordinatesImpl();
                       cleanedCoords.setType("Point");
                       cleanedCoords.setCoordinates(formattedCoords);
                       article.setCleanedCoords(cleanedCoords);
                       System.out.println(pub.getPaperId());
                    }
                }
                pubRepo.save(pub);   
            }
        }
        

        return "redirect:/";
    }
    
}