package edu.asu.diging.cord19.explorer.config;

import java.net.UnknownHostException;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.TerminalClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = { "edu.asu.diging.cord19.explorer.core.elastic.data" })
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class ElasticConfig extends ElasticsearchConfigurationSupport {

    @Value("${elasticsearch.indexName}")
    private String indexName;
    
    @Value("${elasticsearch.host}")
    private String host;
    
    @Value("${elasticsearch.user}")
    private String user;

    @Value("${elasticsearch.password}")
    private String password;

    @Bean
    public String indexName() {
        return indexName;
    }
    
    @Bean
    public RestHighLevelClient elasticsearchRestClient() {
        TerminalClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(host);
                //.usingSsl();
        if (user != null && !user.trim().isEmpty()) {
            builder.withBasicAuth(user, password); 
        }
        final ClientConfiguration clientConfiguration = builder.build();
        return RestClients.create(clientConfiguration).rest();
    }


    @Bean(name = { "elasticsearchOperations", "elasticsearchTemplate" })
    public ElasticsearchRestTemplate elasticsearchTemplate() throws UnknownHostException {
        return new ElasticsearchRestTemplate(elasticsearchRestClient(), entityMapper());
    }
     

    @Bean
    @Override
    public EntityMapper entityMapper() {
        ElasticsearchEntityMapper entityMapper = new ElasticsearchEntityMapper(elasticsearchMappingContext(),
                new DefaultConversionService());
        entityMapper.setConversions(elasticsearchCustomConversions());
        return entityMapper;
    }
}
