package edu.asu.diging.cord19.explorer.core.data;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.cord19.explorer.core.model.impl.MapTotalsImpl;

@Repository
public interface MapTotalsRepository extends PagingAndSortingRepository<MapTotalsImpl, String> {

    public MapTotalsImpl findFirstByOrderByIdDesc();
    
}
