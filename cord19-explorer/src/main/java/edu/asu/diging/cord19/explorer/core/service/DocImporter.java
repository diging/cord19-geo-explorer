package edu.asu.diging.cord19.explorer.core.service;

import java.io.IOException;

public interface DocImporter {

	void run(String rootFolder, String taskId) throws IOException;

	void extractYears(String taskId);

	void extractLocations(String taskId) throws ClassCastException, ClassNotFoundException, IOException;

	void removeUnvalid(String taskId);

}