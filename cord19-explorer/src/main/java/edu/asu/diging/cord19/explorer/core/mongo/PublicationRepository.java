package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;

public interface PublicationRepository extends MongoRepository<PublicationImpl, String> {

    PublicationImpl findFirstByPaperId(String paperId);
    
    PublicationImpl findFirstByArxivId(String arxivId);

    List<PublicationImpl> findByBodyTextLocationMatchesLocationName(String location);
    
    List<PublicationImpl> findByMetadataAuthorsAffiliationInstitution(String institution);
    
    List<PublicationImpl> findByMetadataAuthorsAffiliationLocationSettlement(String settlement);
    
    List<PublicationImpl> findByMetadataAuthorsAffiliationLocationRegion(String region);
    
    List<PublicationImpl> findByMetadataAuthorsAffiliationLocationCountry(String country);
    
    List<PublicationImpl> findByDatabase(String database, Pageable pageable);
    
    List<PublicationImpl> findByArxivIdIsNotNullOrDatabase(String database, Pageable pageable);
    
    long countByArxivIdIsNotNullOrDatabase(String database);
    
    long deleteByDatabase(String database);
}
