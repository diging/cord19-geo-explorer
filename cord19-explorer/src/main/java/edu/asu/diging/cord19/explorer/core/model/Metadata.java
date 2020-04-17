package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.BibEntryImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.MetadataImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.RefEntryImpl;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    defaultImpl=MetadataImpl.class)
public interface Metadata {

	String getTitle();

	void setTitle(String title);

	List<PersonImpl> getAuthors();

	void setAuthors(List<PersonImpl> authors);


}