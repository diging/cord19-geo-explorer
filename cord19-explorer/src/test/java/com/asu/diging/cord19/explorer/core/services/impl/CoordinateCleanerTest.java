package com.asu.diging.cord19.explorer.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.service.worker.impl.CoordinateCleanerImpl;

public class CoordinateCleanerTest {
    
    @Mock
    private PublicationRepository pubRepo;
    
    @Mock
    private TaskRepository taskRepo;
    
    @InjectMocks
    private CoordinateCleanerImpl serviceToTest;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Publication pub = new PublicationImpl();
    }
    

}
