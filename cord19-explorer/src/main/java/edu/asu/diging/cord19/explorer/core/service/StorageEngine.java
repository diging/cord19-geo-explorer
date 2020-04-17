package edu.asu.diging.cord19.explorer.core.service;

import edu.asu.diging.cord19.explorer.core.exception.FileStorageException;

public interface StorageEngine {

	String storeFile(byte[] fileContent, String filename, String directory) throws FileStorageException;

}