package edu.asu.diging.cord19.explorer.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.CleanCoordinates;

@Component
public class CleanCoordinatesImpl implements CleanCoordinates {
    
    @Autowired
    private PublicationRepository pubRepo;
    
    @Override
    @Async
    public void run() {
        Pageable page = PageRequest.of(0, 2);
        Page<PublicationImpl> pubs = pubRepo.findAll(page);
        System.out.println(pubs);

    }
    
}