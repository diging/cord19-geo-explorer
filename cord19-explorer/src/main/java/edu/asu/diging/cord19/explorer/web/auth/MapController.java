package edu.asu.diging.cord19.explorer.web.auth;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

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
    public String show(Model model) throws IOException {
        
        HashMap<String, List<String>> countriesMap = new HashMap<String, List<String>>();
        AtomicInteger high = new AtomicInteger(0);
        AtomicInteger low = new AtomicInteger(0);
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(), CountriesImpl.class)) {
            while(countries.hasNext()) {
                CountriesImpl country = countries.next();
                System.out.println(country.getSelectedWikipediaCount());
                if(country.getSelectedWikipediaCount() > 0) {
                    System.out.println(country.getProperties().getName());
                    ArrayList<String> properties = new ArrayList<String>();
                    int count = country.getSelectedWikipediaCount();
                    properties.add(Integer.toString(country.getSelectedWikipediaCount()));
                    properties.add(country.getCenter());
                    if(country.getSelectedWikipediaCount() > high.intValue()) {
                        high.set(country.getSelectedWikipediaCount());
                    }
                    if(country.getSelectedWikipediaCount() < low.intValue()) {
                        low.set(country.getSelectedWikipediaCount());
                    }
                        
                    countriesMap.put(country.getProperties().getName(), properties);
                    
                }
            }
            
        }
        //final ByteArrayOutputStream out = new ByteArrayOutputStream();
        //ObjectMapper mapper = new ObjectMapper();
        //String x = mapper.writeValueAsString(countryList);
        System.out.println("Countries");
        System.out.println(countriesMap);
        model.addAttribute("countries", countriesMap);
        model.addAttribute("low", low);
        model.addAttribute("high", high);
        
        return "auth/map";
    }
    
    

    @RequestMapping(value = "/auth/map/gather", method = RequestMethod.POST)
    public String gather() throws ClassCastException, ClassNotFoundException, IOException {

        
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(), CountriesImpl.class)) {
            while (countries.hasNext()) {
                CountriesImpl country = countries.next();
                String countryName = country.getProperties().getName();
                String countryCode = country.getCountryCode();

                
                if(country.getGeometry().getType().equalsIgnoreCase("multipolygon")) {
                    System.out.println(country.getProperties().getName());
                    ArrayList<ArrayList<ArrayList<?>>> countryCoords = country.getGeometry().getCoordinatesList();
                    //System.out.println("New Coord");
                    System.out.println(countryCoords);
                    country.setSelectedWikipediaCount(0);  
                    Iterator<ArrayList<ArrayList<?>>> countryCoordsIter = countryCoords.iterator();
                    while(countryCoordsIter.hasNext()) {
                        ArrayList<ArrayList<?>> coord = countryCoordsIter.next();
                        final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
                        for(Object x : coord) {
                            if (x instanceof ArrayList) {
                                //System.out.println("Multi");
                                
                                for(Object y : (ArrayList<?>)x) {
                                    System.out.println(y); 
                                    if (y instanceof ArrayList) {
                                        //add points
										double x1 = 0.0;
										double y1 = 0.0;
					
                                        ArrayList<?> coords = (ArrayList<?>)y;

										try {
											x1 = (double) coords.get(1);
										} catch(Exception e) {
											x1 = (double) ((Integer) coords.get(1)).intValue();
										}
										try {
											y1 = (double) coords.get(0);
										} catch(Exception e) {
											y1 = (double) ((Integer) coords.get(0)).intValue();
										}
										if(x1 == 0.0) {
											System.out.println('x');
											System.out.println(coord);
										}
										if(y1 == 0.0) {
											System.out.println('y');
											System.out.println(coord);
										}
				
                                        points.add(new Coordinate(x1, y1));
                                    }
								}
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
                                //((ArrayList)x)
                            }
                        }
                    }
                }
         	}
    	}
       
        
        
        return "redirect:/";
    }
    
    
}