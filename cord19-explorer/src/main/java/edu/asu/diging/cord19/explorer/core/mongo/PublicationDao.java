package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

public interface PublicationDao {

	List<String> getCountries();

	List<Integer> getYears();

	List<String> getCountriesInText();

    String getCollection();

}