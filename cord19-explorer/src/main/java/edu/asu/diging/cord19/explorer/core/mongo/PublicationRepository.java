package edu.asu.diging.cord19.explorer.core.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;

public interface PublicationRepository extends MongoRepository<PublicationImpl, String> {

	PublicationImpl findFirstByPaperId(String paperId);
}
