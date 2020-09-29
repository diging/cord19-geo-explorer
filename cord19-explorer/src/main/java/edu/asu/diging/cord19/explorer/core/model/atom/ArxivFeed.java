package edu.asu.diging.cord19.explorer.core.model.atom;

import java.util.List;

import com.rometools.rome.feed.atom.Feed;

public class ArxivFeed extends Feed {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<ArxivSyndPerson> arxivAuthors;

    public List<ArxivSyndPerson> getArxivAuthors() {
        return arxivAuthors;
    }

    public void setArxivAuthors(List<ArxivSyndPerson> authors) {
        this.arxivAuthors = authors;
    }
}
