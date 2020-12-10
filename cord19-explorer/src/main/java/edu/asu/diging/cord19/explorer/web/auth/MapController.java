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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String show(Model model) throws IOException {
        
        HashMap<String, List<String>> countriesMap = new HashMap<String, List<String>>();
        //Is Atomic Int the best way to this?
        AtomicInteger highCount = new AtomicInteger(0);
        AtomicInteger lowCount = new AtomicInteger(0);
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(), CountriesImpl.class)) {
            while(countries.hasNext()) {
                CountriesImpl country = countries.next();
                if(country.getProperties().getSelectedWikipediaCount() > 0) {
                    ArrayList<String> properties = new ArrayList<String>();
                    properties.add(Integer.toString(country.getProperties().getSelectedWikipediaCount()));
                    properties.add(country.getProperties().getCenter());     
                    if(country.getProperties().getSelectedWikipediaCount() > highCount.intValue()) {
                        highCount.set(country.getProperties().getSelectedWikipediaCount());
                    }
                    if(country.getProperties().getSelectedWikipediaCount() < lowCount.intValue()) {
                        lowCount.set(country.getProperties().getSelectedWikipediaCount());
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
                if(country.getGeometry().getType().equalsIgnoreCase("multipolygon")) {
                    ArrayList<ArrayList<ArrayList<?>>> countryCoords = country.getGeometry().getCoordinates();
                    country.getProperties().setSelectedWikipediaCount(0);  
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
										}
										if(y == 0.0) {
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
                                                        country.getProperties().incrementSelectedWikipediaCount();
                                                    }
                                                } catch(Exception e) {
                                                }
											}
										}
									}
								}
                            }
                        }
                    }
                }
                countriesRepo.save(country);
         	}
    	}
       
        return "redirect:/";
    }
    
    @RequestMapping(value = "/auth/map/get", method = RequestMethod.GET)
    public  ResponseEntity<Map<String,  List<CountriesImpl>>> get(Model model) throws IOException {
        List<CountriesImpl> countries = countriesRepo.findAll();
        Map<String, List<CountriesImpl>> data = new HashMap<String,  List<CountriesImpl>>();
        data.put("countries", countries);
        return new ResponseEntity<Map<String, List<CountriesImpl>>>(data, HttpStatus.OK);
    
    }
    
}