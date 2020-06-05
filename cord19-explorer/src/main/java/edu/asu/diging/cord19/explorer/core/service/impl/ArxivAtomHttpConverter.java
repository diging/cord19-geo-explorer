package edu.asu.diging.cord19.explorer.core.service.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.sax.XMLReaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Person;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SAXBuilder;
import com.rometools.rome.io.WireFeedInput;
import com.rometools.rome.io.impl.Atom10Parser;

import edu.asu.diging.cord19.explorer.core.model.atom.ArxivSyndPerson;

@Component
public class ArxivAtomHttpConverter extends AtomFeedHttpMessageConverter {
    
    final private Namespace ns = Namespace.getNamespace("http://www.w3.org/2005/Atom");
    
    @Override
    protected boolean supports(Class<?> clazz) {
        return Feed.class.isAssignableFrom(clazz);
    }

    @Override
    protected Feed readInternal(Class<? extends Feed> arg0, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        // modified from: AtomFeedHttpMessageConverter
        WireFeedInput feedInput = new WireFeedInput();
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = (contentType != null && contentType.getCharset() != null ?
                contentType.getCharset() : DEFAULT_CHARSET);
        
        Feed feed = null;
        String message = null;
        try {
            Reader reader = new InputStreamReader(inputMessage.getBody(), charset);
            message = IOUtils.toString(reader);
            feed = (Feed) feedInput.build(new InputStreamReader(IOUtils.toInputStream(message, Charset.forName("UTF-8"))));
        }
        catch (FeedException ex) {
            throw new HttpMessageNotReadableException("Could not read WireFeed: " + ex.getMessage(), ex, inputMessage);
        }
        
        if (message != null) {
            addAffiliations(message, feed);
        }
        
        return feed;
    }
    
    private void addAffiliations(String message, Feed feed) {
        final SAXBuilder saxBuilder = createBuilder();
        
        Document document = null;
        try {
            document = saxBuilder.build(IOUtils.toInputStream(message, Charset.forName("UTF-8")));
        } catch (JDOMException | IOException e) {
           logger.error("Could not build DOM.", e);
           return;
        }
        
        Map<String, Entry> entriesById = new HashMap<String, Entry>();
        feed.getEntries().forEach(e -> entriesById.put(e.getId(), e));
        
        Element root = document.getRootElement();
        final List<Element> entries = root.getChildren("entry", ns);
        
        if (!entries.isEmpty()) {
            for (Element entry : entries) {
                // there should be just one
                List<Element> ids = entry.getChildren("id", ns);
                if (ids == null || ids.isEmpty()) {
                    // we skip in this case
                    continue;
                }
                
                String currentId = ids.get(0).getText();
                
                List<Element> authors = entry.getChildren("author", ns);
                if (!authors.isEmpty()) {
                    Entry arxivEntry = entriesById.get(currentId);
                    arxivEntry.setAuthors(new ArrayList<SyndPerson>());
                    for (Element author : authors) {
                        arxivEntry.getAuthors().add(parsePerson(author));
                    }
                }
            }
        }
    }
    
    /**
     * Modified from {@link Atom10Parser} -> parsePerson().
     * 
     * @param baseURI
     * @param person
     * @param locale
     * @return
     */
    private Person parsePerson(Element person) {

        final ArxivSyndPerson arxivPerson = new ArxivSyndPerson();

        final Element name = person.getChild("name", ns);
        if (name != null) {
            arxivPerson.setName(name.getText());
        }

        final Element uri = person.getChild("uri", ns);
        if (uri != null) {
            arxivPerson.setUri(uri.getText());
        }

        final Element email = person.getChild("email", ns);
        if (email != null) {
            arxivPerson.setEmail(email.getText());
        }

        arxivPerson.setForeignMarkup(extractForeignMarkup(person));
        return arxivPerson;
    }

    
    /**
     * Modified from {@link WireFeedInput}.
     * @return
     */
    private SAXBuilder createBuilder() {
        SAXBuilder saxBuilder = new SAXBuilder(XMLReaders.NONVALIDATING);
        
        try {
            final XMLReader parser = saxBuilder.createParser();
            setFeature(saxBuilder, parser, "http://xml.org/sax/features/external-general-entities", false);
            setFeature(saxBuilder, parser, "http://xml.org/sax/features/external-parameter-entities", false);
            setFeature(saxBuilder, parser, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (final JDOMException e) {
            throw new IllegalStateException("JDOM could not create a SAX parser", e);
        }
        saxBuilder.setExpandEntities(false);

        return saxBuilder;
    }

    /**
     * Modified from {@link WireFeedInput}.
     * @return
     */
    private void setFeature(SAXBuilder saxBuilder, XMLReader parser, String feature, boolean value) {
        if (isFeatureSupported(parser, feature, value)) {
            saxBuilder.setFeature(feature, value);
        }
    }

    /**
     * Modified from {@link WireFeedInput}.
     * @return
     */
    private boolean isFeatureSupported(XMLReader parser, String feature, boolean value) {
        try {
            parser.setFeature(feature, value);
            return true;
        } catch (final SAXNotRecognizedException e) {
            return false;
        } catch (final SAXNotSupportedException e) {
            return false;
        }
    }
    
    /**
     * Modified from {@link WireFeedInput}.
     * @return
     */
    protected List<Element> extractForeignMarkup(final Element e) {

        final ArrayList<Element> foreignElements = new ArrayList<Element>();

        for (final Element element : e.getChildren()) {
            if (!ns.equals(element.getNamespace())) {
                // if element not in the RSS namespace and elem was not handled by a module save it
                // as foreign markup but we can't detach it while we're iterating
                foreignElements.add(element.clone());
            }
        }

        // now we can detach the foreign markup elements
        for (final Element foreignElement : foreignElements) {
            foreignElement.detach();
        }

        return foreignElements;

    }
}
