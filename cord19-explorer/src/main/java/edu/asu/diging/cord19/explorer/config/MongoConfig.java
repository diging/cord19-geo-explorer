package edu.asu.diging.cord19.explorer.config;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
@EnableMongoRepositories({ "edu.asu.diging.cord19.explorer.core.mongo" })
public class MongoConfig {

    @Value("${mongo.database.name}")
    private String mongoDbName;

    @Value("${mongo.database.host}")
    private String mongoDbHost;

    @Value("${mongo.database.port}")
    private int mongoDbPort;
    
    @Value("${mongo.database.user}")
    private String mongoDbUser;
    
    @Value("${mongo.database.password}")
    private String mongoDbPassword;
    
    @Value("${mongo.database.authdb}")
    private String mongoDbAuthdb;

    @Bean
    public MongoClient mongo() throws UnknownHostException {
        Builder builder = MongoClientSettings.builder();
        if (mongoDbUser != null && !mongoDbUser.trim().isEmpty()) {
            builder = builder.credential(MongoCredential.createCredential(mongoDbUser, mongoDbAuthdb, mongoDbPassword.toCharArray()));
        }
        MongoClient mongoClient = MongoClients
                .create(builder
                        .applyToClusterSettings(
                                b -> b.hosts(Arrays.asList(new ServerAddress(mongoDbHost, mongoDbPort))))
                        .build());
        return mongoClient;
    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws UnknownHostException {
        return new SimpleMongoClientDbFactory(mongo(), mongoDbName);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(mongoDbFactory());
    }
}