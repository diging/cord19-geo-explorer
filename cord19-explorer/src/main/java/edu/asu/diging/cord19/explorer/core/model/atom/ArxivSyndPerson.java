package edu.asu.diging.cord19.explorer.core.model.atom;

import java.util.List;

import org.jdom2.Element;

import com.rometools.rome.feed.atom.Person;
import com.rometools.rome.feed.synd.SyndPersonImpl;

public class ArxivSyndPerson extends Person {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<Element> foreignMarkup;

    public List<Element> getForeignMarkup() {
        return foreignMarkup;
    }

    public void setForeignMarkup(List<Element> foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }    
}
