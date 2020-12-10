package edu.asu.diging.cord19.explorer.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.Getlocation;


@Component
public class GetLocationImpl implements Getlocation {

@Autowired
private PublicationRepository pubRepo;

    public void run() {
       List<PublicationImpl> publications = pubRepo.findAll();
    }

}