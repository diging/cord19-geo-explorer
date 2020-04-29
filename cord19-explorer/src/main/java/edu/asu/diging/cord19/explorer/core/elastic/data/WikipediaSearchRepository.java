package edu.asu.diging.cord19.explorer.core.elastic.data;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;

public interface WikipediaSearchRepository extends ElasticsearchRepository<Wikientry, String> {

	List<Wikientry> findByContent(String term);
}
