package edu.asu.diging.cord19.explorer.web.auth;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



import edu.asu.diging.cord19.explorer.core.model.impl.CleanedCoordinatesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.CountriesRepository;


@Controller
public class MapController {
    
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private CountriesRepository countriesRepo;
    
    @RequestMapping(value = "/auth/map/show", method = RequestMethod.GET)
    public String show() {

        return "auth/map";
    }
    
    

    @RequestMapping(value = "/auth/map/gather", method = RequestMethod.POST)
    public String gather() throws ClassCastException, ClassNotFoundException, IOException {

        
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(), CountriesImpl.class)) {
            while (countries.hasNext()) {
                CountriesImpl country = countries.next();
                System.out.println(country.getProperties().getName());
                String countryName = country.getProperties().getName();
                String countryCode = country.getCountryCode();
                ArrayList<ArrayList<?>> countryCoords = country.getGeometry().getCoordinatesList().get(0);
                //System.out.println("New Coord");
                //System.out.println(countryCoords);
                //for(ArrayList<?> x: countryCoords) {
                //    System.out.println(x);
                    System.out.println("<><><><><><><><><><><><><><><>");
                //}
                country.setSelectedWikipediaCount(0);          
                //System.out.println(country.getGeometry().getType());
                //System.out.println(countryCoords.size());
                if(country.getGeometry().getType().equalsIgnoreCase("polygon")) {
                    Iterator<ArrayList<?>> countryCoordsIter = countryCoords.iterator();
                    ArrayList<?> coord = countryCoordsIter.next();
                    final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
                    //create country polygon
                    
                    //Path2D countryPoly = new Path2D.Double();
                    double x = (double) coord.get(1);
                    double y = (double) coord.get(0);
                    //countryPoly.moveTo(x, y);
                    points.add(new Coordinate(x, y));
                    while(countryCoordsIter.hasNext()) {
                        coord = countryCoordsIter.next();
                        
                        try {
                            x = (double) coord.get(1);
                        } catch(Exception e) {
                            x = (double) ((Integer) coord.get(1)).intValue();
                        }
                        try {
                            y = (double) coord.get(0);
                        } catch(Exception e) {
                            y = (double) ((Integer) coord.get(0)).intValue();
                        }
                        if(x == 0.0) {
                            System.out.println('x');
                            System.out.println(coord);
                        }
                        if(y == 0.0) {
                            System.out.println('y');
                            System.out.println(coord);
                        }
                        points.add(new Coordinate(x, y));
                        //countryPoly.lineTo(x, y);
                    }
                    //countryPoly.closePath();
                    //System.out.println(countryPoly.getBounds());
                    final GeometryFactory gf = new GeometryFactory();
                    final org.locationtech.jts.geom.Polygon polygon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])), gf), null);
                    //check for contains
                    try (CloseableIterator<PublicationImpl> pubs = mongoTemplate.stream(new Query().noCursorTimeout(), PublicationImpl.class)) {
                        while (pubs.hasNext()) {
                            PublicationImpl pub = pubs.next();
                            List<PersonImpl> authors = pub.getMetadata().getAuthors();
                            for(PersonImpl author : authors) {
                                
                                if(author.getAffiliation().getSelectedWikiarticle() != null) {
                                    CleanedCoordinatesImpl cleanedCoords = author.getAffiliation().getSelectedWikiarticle().getCleanedCoords();
                                    try { 
                                        Double cleanedX = cleanedCoords.getCoordinates().get(0);
                                        Double cleanedY = cleanedCoords.getCoordinates().get(1);
                                        Coordinate coordToFind = new Coordinate(cleanedX, cleanedY);
                                        Point point = gf.createPoint(coordToFind);
                                     if(point.within(polygon)) {
                                            country.incrementSelectedWikipediaCount();
                                            System.out.println("Found in");
                                            System.out.println(countryName);
                                            System.out.println(author.getFirst().concat(" ").concat(author.getLast()));
                                            System.out.println(pub.getPaperId());
                                            System.out.println(cleanedCoords.getCoordinates());
                                        }
                                    } catch(Exception e) {
                                       //System.out.println("no coords");
                                    }
                                }
                            }
                            
                        }
                   
                    }

                    System.out.println(country.getProperties().getName());
                    System.out.println(country.getSelectedWikipediaCount());

                    country.setCountryCode(countryCode);
                    countriesRepo.save(country);
                    
                } //end polygon block

                

                
                

         }
      }
       
        
        
        return "redirect:/";
    }
    
    
}