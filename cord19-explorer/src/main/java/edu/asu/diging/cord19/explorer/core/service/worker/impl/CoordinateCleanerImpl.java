package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.MapTotalsRepository;
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

import edu.asu.diging.cord19.explorer.core.model.CoordType;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.CleanedCoordinatesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.MapTotalsImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;

@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class CoordinateCleanerImpl implements CoordinateCleaner {

    @Value("${mongo.duplicate.database.name}")
    private String duplicateCollection;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private PublicationRepository pubRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MapTotalsRepository totalsRepo;

    private double processLength3Coords(List<String> coords) {
        float degrees = Float.parseFloat(coords.get(0));
        float minutes = Float.parseFloat(coords.get(1));
        float seconds = Float.parseFloat(coords.get(2));
        double decimalDegrees = 0;
        if (degrees == 0) {
            decimalDegrees = ((minutes / 60.0) + (seconds / 3600.0));
        } else {
            decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));
        }
        return decimalDegrees;
    }

    private double processLength2Coords(List<String> coords) {
        float degrees = Float.parseFloat(coords.get(0));
        float minutes = Float.parseFloat(coords.get(1));
        double decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0));
        return decimalDegrees;
    }

    private CleanedCoordinatesImpl createCleanCoords(List<Double> formattedCoords) {
        CleanedCoordinatesImpl cleanedCoords = new CleanedCoordinatesImpl();
        cleanedCoords.setType(CoordType.Point);
        cleanedCoords.setCoordinates(formattedCoords);
        return cleanedCoords;
    }

    private void parseCoords(WikipediaArticleImpl article) {
        List<String> coords = new ArrayList<String>();
        List<Double> formattedCoords = new ArrayList<Double>();
        String[] splitted = article.getCoordinates().split("\\|");
        // split on pipes then use Ordinals to gather Degree, Mins, Secs then compute
        // Decimal Degrees
        for (String s : splitted) {
            if (s.matches("-?\\d+(\\.\\d+)?")) {
                coords.add(s);

            } else if (s.equals("N") || s.equals("E")) {
                if (coords.size() == 3) {
                    formattedCoords.add(processLength3Coords(coords));
                    coords.clear();
                } else if (coords.size() == 2) {
                    formattedCoords.add(processLength2Coords(coords));
                    coords.clear();
                } else if (coords.size() == 1) {
                    Double doubleCoord = Double.parseDouble(coords.get(0));
                    formattedCoords.add(doubleCoord);
                    coords.clear();
                }

            } else if (s.equals("S") || s.equals("W")) {
                // handle negative coords
                if (coords.size() == 3) {
                    formattedCoords.add(processLength3Coords(coords) * -1);
                    coords.clear();
                } else if (coords.size() == 2) {
                    formattedCoords.add(processLength2Coords(coords) * -1);
                    coords.clear();
                } else if (coords.size() == 1) {
                    Double doubleCoord = Double.parseDouble(coords.get(0)) * -1;
                    formattedCoords.add(doubleCoord);
                    coords.clear();
                }
            }
        }

        article.setCleanedCoords(createCleanCoords(formattedCoords));
    }

    public void calculateCountryStats() {

        MapTotalsImpl totals = new MapTotalsImpl();
        try (CloseableIterator<CountriesImpl> countries = mongoTemplate.stream(new Query().noCursorTimeout(),
                CountriesImpl.class)) {
            while (countries.hasNext()) {
                CountriesImpl country = countries.next();
                if (country.getProperties().getSelectedWikipediaCount() > 0) {
                    if (country.getProperties().getSelectedWikipediaCount() > totals.getHighCount()) {
                        totals.setHighCount(country.getProperties().getSelectedWikipediaCount());
                    }
                    if (country.getProperties().getSelectedWikipediaCount() < totals.getLowCount()) {
                        totals.setLowCount(country.getProperties().getSelectedWikipediaCount());
                    }
                }
            }
        }

        totalsRepo.save(totals);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.service.worker.impl.DocumentCleaner#
     *
     */
    @Override
    @Async
    public void startCleaningCoordinates(String taskId) {

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
                PublicationImpl pub = docs.next();
                List<PersonImpl> authors = pub.getMetadata().getAuthors();
                for (PersonImpl author : authors) {
                    if (author.getAffiliation().getSelectedWikiarticle() != null) {
                        WikipediaArticleImpl article = author.getAffiliation().getSelectedWikiarticle();
                        parseCoords(article);
                    }
                }
                pubRepo.save(pub);
            }
        }

        calculateCountryStats();

        task.setStatus(TaskStatus.DONE);
        taskRepo.save((TaskImpl) task);
    }

    public Publication cleanCoordinatesImport(Publication pub) {
        List<PersonImpl> authors = pub.getMetadata().getAuthors();
        for (PersonImpl author : authors) {
            List<String> coords = new ArrayList<String>();
            List<Double> formattedCoords = new ArrayList<Double>();
            if (author.getAffiliation().getSelectedWikiarticle() != null) {
                WikipediaArticleImpl article = author.getAffiliation().getSelectedWikiarticle();
                String[] splitted = article.getCoordinates().split("\\|");
                // split on pipes then use Ordinals to gather Degree, Mins, Secs then compute
                // Decimal Degrees
                // Refactor into function
                for (String s : splitted) {
                    if (s.matches("-?\\d+(\\.\\d+)?")) {
                        coords.add(s);

                    } else if (s.equals("N") || s.equals("E")) {
                        if (coords.size() == 3) {
                            formattedCoords.add(processLength3Coords(coords));
                            coords.clear();
                        } else if (coords.size() == 2) {
                            formattedCoords.add(processLength2Coords(coords));
                            coords.clear();
                        } else if (coords.size() == 1) {
                            Double doubleCoord = Double.parseDouble(coords.get(0));
                            formattedCoords.add(doubleCoord);
                            coords.clear();
                        }

                    } else if (s.equals("S") || s.equals("W")) {
                        // handle negative coords
                        if (coords.size() == 3) {
                            formattedCoords.add(processLength3Coords(coords) * -1);
                            coords.clear();
                        } else if (coords.size() == 2) {
                            formattedCoords.add(processLength2Coords(coords) * -1);
                            coords.clear();
                        } else if (coords.size() == 1) {
                            Double doubleCoord = Double.parseDouble(coords.get(0)) * -1;
                            formattedCoords.add(doubleCoord);
                            coords.clear();
                        }
                    }
                }
                CleanedCoordinatesImpl cleanedCoords = new CleanedCoordinatesImpl();
                cleanedCoords.setType(CoordType.Point);
                cleanedCoords.setCoordinates(formattedCoords);
                article.setCleanedCoords(cleanedCoords);

            }
        }
        return pub;
    }

    @Override
    public void cleanCoordinates(String taskId) {
        // TODO Auto-generated method stub

    }

}
