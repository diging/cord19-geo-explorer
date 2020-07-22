package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;

public interface PublicationSearchProvider {

    List<PublicationImpl> searchPublicationTitles(String title);
    
    Page<PublicationImpl> paginateResults(Pageable pageable, List<PublicationImpl> pubs);

}
