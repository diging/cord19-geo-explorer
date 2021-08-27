package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import edu.asu.diging.cord19.explorer.core.service.worker.CoordinateParser;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.MapTotalsImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;

@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class CoordinateCleanerImpl implements CoordinateCleaner {

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private PublicationRepository pubRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MapTotalsRepository totalsRepo;
    

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
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);
        taskRepo.save((TaskImpl) task);

        try (CloseableIterator<PublicationImpl> docs = mongoTemplate.stream(new Query().noCursorTimeout(), PublicationImpl.class)) {
            while (docs.hasNext()) {
                PublicationImpl pub = docs.next();
                cleanCoordinates(pub);
                pubRepo.save(pub);
            }
        }

        calculateCountryStats();

        task.setStatus(TaskStatus.DONE);
        taskRepo.save((TaskImpl) task);
    }

    public PublicationImpl cleanCoordinates(PublicationImpl pub) {
        List<PersonImpl> authors = pub.getMetadata().getAuthors();
        CoordinateParser coordinateParser = new CoordinateParserImpl();
        for (PersonImpl author : authors) {
            if (author.getAffiliation().getSelectedWikiarticle() != null) {
                WikipediaArticleImpl article = author.getAffiliation().getSelectedWikiarticle();
                article.setCleanedCoords(coordinateParser.parse(article.getCoordinates()));

            }
        }
        return pub;
    }

}
