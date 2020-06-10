package edu.asu.diging.cord19.explorer.core.service;

import java.util.List;

import org.apache.logging.log4j.util.TriConsumer;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationType;

public interface WikipediaHelper {

    boolean findWikiarticles(Object match, List<Wikientry> entries, LocationType type,
            TriConsumer<Object, Wikientry, LocationType> attachMethod);

}