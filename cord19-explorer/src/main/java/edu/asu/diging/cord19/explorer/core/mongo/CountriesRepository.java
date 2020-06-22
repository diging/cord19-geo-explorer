package edu.asu.diging.cord19.explorer.core.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import edu.asu.diging.cord19.explorer.core.model.impl.CountriesImpl;

public interface CountriesRepository extends MongoRepository<CountriesImpl, String> {

}
