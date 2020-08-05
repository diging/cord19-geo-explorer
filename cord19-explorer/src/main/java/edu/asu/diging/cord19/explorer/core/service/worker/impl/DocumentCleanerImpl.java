package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.task.Task;
import edu.asu.diging.cord19.explorer.core.model.task.TaskStatus;
import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.worker.DocumentCleaner;

@Service
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class DocumentCleanerImpl implements DocumentCleaner {

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
    public void removeDuplicatePmcPubs(String taskId) {

        Optional<TaskImpl> optional = taskRepo.findById(taskId);
        if (!optional.isPresent()) {
            return;
            // FIXME: mark as failure
        }

        Task task = optional.get();
        task.setStatus(TaskStatus.PROCESSING);
        taskRepo.save((TaskImpl) task);

        long count = 0;
        Query query = new Query(Criteria.where("pmcid").ne(null));
        CloseableIterator<PublicationImpl> it = mongoTemplate.stream(query, PublicationImpl.class);
        try {
            while (it.hasNext()) {
                Publication pub = it.next();
                PublicationImpl duplicate = pubRepo.findFirstByPaperId(pub.getPmcid());
                if (duplicate != null && !pub.getPaperId().equals(duplicate.getPaperId())) {
                    duplicate.setDuplicate(true);
                    mongoTemplate.save(duplicate, duplicateCollection);
                    pubRepo.delete(duplicate);
                    count++;
                }
            }
        } finally {
            it.close();
        }

        logger.info(count + " duplicates!");

        task.setProcessed(count);
        task.setStatus(TaskStatus.DONE);
        task.setDateEnded(OffsetDateTime.now());
        taskRepo.save((TaskImpl) task);
    }
}
