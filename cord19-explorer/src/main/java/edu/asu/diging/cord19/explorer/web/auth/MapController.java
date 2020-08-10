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
        //Is Atomic Int the best way to this?
        AtomicInteger highCount = new AtomicInteger(0);
        AtomicInteger lowCount = new AtomicInteger(0);
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(), CountriesImpl.class)) {
            while(countries.hasNext()) {
                CountriesImpl country = countries.next();
                if(country.getSelectedWikipediaCount() > 0) {
                    ArrayList<String> properties = new ArrayList<String>();
                    properties.add(Integer.toString(country.getSelectedWikipediaCount()));
                    properties.add(country.getCenter());     
                    if(country.getSelectedWikipediaCount() > highCount.intValue()) {
                        highCount.set(country.getSelectedWikipediaCount());
                    }
                    if(country.getSelectedWikipediaCount() < lowCount.intValue()) {
                        lowCount.set(country.getSelectedWikipediaCount());
                    }
                    countriesMap.put(country.getProperties().getName(), properties);
                }
            }
        }
        model.addAttribute("countries", countriesMap);
        model.addAttribute("low", lowCount);
        model.addAttribute("high", highCount);
        
        return "auth/map";
    }
    
    

    @RequestMapping(value = "/auth/map/gather", method = RequestMethod.POST)
    public String gather() throws ClassCastException, ClassNotFoundException, IOException {
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(), CountriesImpl.class)) {
            while (countries.hasNext()) {
                CountriesImpl country = countries.next();
                String countryName = country.getProperties().getName();
                if(country.getGeometry().getType().equalsIgnoreCase("multipolygon")) {
                    ArrayList<ArrayList<ArrayList<?>>> countryCoords = country.getGeometry().getCoordinatesList();
                    country.setSelectedWikipediaCount(0);  
                    Iterator<ArrayList<ArrayList<?>>> countryCoordsIter = countryCoords.iterator();
                    while(countryCoordsIter.hasNext()) {
                        ArrayList<ArrayList<?>> coordList = countryCoordsIter.next();
                        final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
                        for(Object coord : coordList) {
                            if (coord instanceof ArrayList) {
                                for(Object innerCoorList : (ArrayList<?>)coord) {
                                    if (innerCoorList instanceof ArrayList) {
                                        //add points if not in list
										double x = 0.0;
										double y = 0.0;
                                        ArrayList<?> coords = (ArrayList<?>)innerCoorList;
                                        
										try {
											x = (double) coords.get(1);
										} catch(Exception e) {
											x = (double) ((Integer) coords.get(1)).intValue();
										}
										try {
											y = (double) coords.get(0);
										} catch(Exception e) {
											y = (double) ((Integer) coords.get(0)).intValue();
										}
										//TODO: not sure what to do with malformed data yet
										if(x == 0.0) {
											System.out.println('x');
											System.out.println(coordList);
										}
										if(y == 0.0) {
											System.out.println('y');
											System.out.println(coordList);
										}
				
                                        points.add(new Coordinate(x, y));
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
                                                    }
                                                } catch(Exception e) {
                                                   System.out.println("no coords");
                                                }
											}
										}
									}
								}
                            }
                        }
                    }
                }
         	}
    	}
       
        return "redirect:/";
    }
    
    
}