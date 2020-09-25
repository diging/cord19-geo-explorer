package edu.asu.diging.cord19.explorer.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;

@Configuration
@DependsOn("mongoTemplate")
public class CollectionsConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps(PublicationImpl.class) 
            .ensureIndex(
                new Index().on("metadata.title", Sort.Direction.ASC)
        );
    }
}