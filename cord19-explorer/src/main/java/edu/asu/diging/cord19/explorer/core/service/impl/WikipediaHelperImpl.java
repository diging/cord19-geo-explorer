package edu.asu.diging.cord19.explorer.core.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.model.impl.LocationType;
import edu.asu.diging.cord19.explorer.core.service.WikipediaHelper;
import edu.asu.diging.cord19.explorer.core.service.worker.ElasticsearchConnector;

@Component
public class WikipediaHelperImpl implements WikipediaHelper {
    
    @Autowired
    private ElasticsearchConnector elastic;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.WikipediaHelper#findWikiarticles(java.lang.Object, java.util.List, edu.asu.diging.cord19.explorer.core.model.impl.LocationType, org.apache.logging.log4j.util.TriConsumer)
     */
    @Override
    public boolean findWikiarticles(Object match, List<Wikientry> entries, LocationType type,
            TriConsumer<Object, Wikientry, LocationType> attachMethod) {
        List<String> placeIndicators = Arrays.asList("republic", "land", "state", "countr", "place", "cit", "park",
                "region", "continent", "district", "metro", "town", "captial", "village", "settlement", "university");

        boolean isPlace = false;
        // if one of the first x results seems to be a place, we assume it's one
        for (Wikientry entry : entries) {
            if (entry.getComplete_text().trim().toLowerCase().startsWith("#redirect")) {
                Wikientry redirectEntry = elastic.followRedirect(entry);
                if (redirectEntry != null) {
                    entry = redirectEntry;
                }
            }

            isPlace = isPlace || entry.getCategories().stream()
                    .anyMatch(c -> placeIndicators.stream().anyMatch(p -> c.toLowerCase().contains(p)));

            if (entry.getCoordinates() != null && !entry.getCoordinates().trim().isEmpty()) {
                isPlace = true;
                attachMethod.accept(match, entry, type);
            }
        }
        return isPlace;
    }

}
