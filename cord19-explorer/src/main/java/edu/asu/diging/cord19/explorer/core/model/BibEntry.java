package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.BibEntryImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    defaultImpl=BibEntryImpl.class)
public interface BibEntry {

	String getRefId();

	void setRefId(String refId);

	String getTitle();

	void setTitle(String title);

	List<PersonImpl> getAuthors();

	void setAuthors(List<PersonImpl> authors);

	int getYear();

	void setYear(int year);

	String getVenue();

	void setVenue(String venue);

	String getVolume();

	void setVolume(String volume);

	String getIssn();

	void setIssn(String issn);

	String getPages();

	void setPages(String pages);

	Map<String, List<String>> getOtherIds();

	void setOtherIds(Map<String, List<String>> otherIds);

}