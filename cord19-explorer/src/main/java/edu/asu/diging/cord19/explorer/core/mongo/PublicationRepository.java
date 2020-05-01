package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;

public interface PublicationRepository extends MongoRepository<PublicationImpl, String> {

    PublicationImpl findFirstByPaperId(String paperId);

    List<PublicationImpl> findByBodyTextLocationMatchesLocationName(String location);
}
