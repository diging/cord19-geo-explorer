package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.model.task.TaskStatus;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.worker.CoordinateCleaner;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.CleanedCoordinatesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;


@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class CoordinateCleanerImpl implements CoordinateCleaner {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${mongo.duplicate.database.name}")
    private String duplicateCollection;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private PublicationRepository pubRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.service.worker.impl.DocumentCleaner#
     * removeDuplicatePmcPubs(java.lang.String)
     */
    @Override
    @Async
    public void cleanCoordinates(String taskId) {
        
        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);
        taskRepo.save((TaskImpl) task);

        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query(), PublicationImpl.class)) {
            while (docs.hasNext()) {
                // extract to own method and move into parse csv 
                PublicationImpl pub = docs.next();
                List<PersonImpl> authors = pub.getMetadata().getAuthors();
                for(PersonImpl author : authors) {
                    List<String> coords = new ArrayList<String>();
                    List<Double> formattedCoords = new ArrayList<Double>();
                    if(author.getAffiliation().getSelectedWikiarticle() != null) {
                       WikipediaArticleImpl article = author.getAffiliation().getSelectedWikiarticle();
                       String [] splitted =  article.getCoordinates().split("\\|");
                       // split on pipes then use Ordinals to gather Degree, Mins, Secs then compute Decimal Degrees
                       //Refactor into function
                       for (String s: splitted) {      
                            if(s.matches("-?\\d+(\\.\\d+)?")) {
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
                       
                    }
                }
                pubRepo.save(pub);   
            }
        }
    
    }
    
    
    public Publication cleanCoordinatesImport(Publication pub) {
        List<PersonImpl> authors = pub.getMetadata().getAuthors();
        for(PersonImpl author : authors) {
            List<String> coords = new ArrayList<String>();
            List<Double> formattedCoords = new ArrayList<Double>();
            if(author.getAffiliation().getSelectedWikiarticle() != null) {
               WikipediaArticleImpl article = author.getAffiliation().getSelectedWikiarticle();
               String [] splitted =  article.getCoordinates().split("\\|");
               // split on pipes then use Ordinals to gather Degree, Mins, Secs then compute Decimal Degrees
               //Refactor into function
               for (String split_string: splitted) {      
                    if(split_string.matches("-?\\d+(\\.\\d+)?")) {
                        coords.add(split_string);
                        
                    } else if(split_string.equals("N") || split_string.equals("E")){
                        if(coords.size() == 3) {
                            float degrees = Float.parseFloat(coords.get(0));
                            float minutes = Float.parseFloat(coords.get(1));
                            float seconds = Float.parseFloat(coords.get(2));
                            double decimalDegrees = 0;
                            if(degrees == 0) {
                                decimalDegrees = ((minutes / 60.0) + (seconds / 3600.0));
                            } else {
                                decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));
                            }
                            formattedCoords.add(decimalDegrees);
                            coords.clear();
                        }
                        if(coords.size() == 2) {
                            float degrees = Float.parseFloat(coords.get(0));
                            float minutes = Float.parseFloat(coords.get(1));
                            double decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0));
                            formattedCoords.add(decimalDegrees);
                            coords.clear();
                        }
                        if(coords.size() == 1) {
                            Double doubleCoord = Double.parseDouble(coords.get(0));
                            formattedCoords.add(doubleCoord);
                            coords.clear();
                        }
                       
                    } else if(split_string.equals("S") || split_string.equals("W")) {
                        // handle negative coords
                        if(coords.size() == 3) {
                            float degrees = Float.parseFloat(coords.get(0));
                            float minutes = Float.parseFloat(coords.get(1));
                            float seconds = Float.parseFloat(coords.get(2));
                            double decimalDegrees = 0;
                            if(degrees == 0) {
                                decimalDegrees = ((minutes / 60.0) + (seconds / 3600.0));
                            } else {
                                decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));
                            }
                            decimalDegrees = decimalDegrees *-1;
                            formattedCoords.add(decimalDegrees);
                            coords.clear();
                        }
                        if(coords.size() == 2) {
                            float degrees = Float.parseFloat(coords.get(0));
                            float minutes = Float.parseFloat(coords.get(1));
                            double decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0));
                            decimalDegrees = decimalDegrees *-1;
                            formattedCoords.add(decimalDegrees);
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
               
            }
        }
        return pub;
    }

}
