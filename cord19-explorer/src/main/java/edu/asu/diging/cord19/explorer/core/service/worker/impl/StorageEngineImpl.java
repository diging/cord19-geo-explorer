package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.exception.FileStorageException;
import edu.asu.diging.cord19.explorer.core.service.worker.StorageEngine;

@Component
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class StorageEngineImpl implements StorageEngine {

    @Value("${file.upload.path}")
    private String path;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.vspace.core.file.impl.IStorageEngine#storeFile(byte[],
     * java.lang.String, java.lang.String)
     */
    @Override
    public String storeFile(byte[] fileContent, String filename, String directory) throws FileStorageException {
        File parent = new File(path + File.separator + directory);
        if (!parent.exists()) {
            parent.mkdir();
        }
        File file = new File(parent.getAbsolutePath() + File.separator + filename);
        BufferedOutputStream stream;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new FileStorageException("Could not store file.", e);
        }
        try {
            stream.write(fileContent);
            stream.close();
        } catch (IOException e) {
            throw new FileStorageException("Could not store file.", e);
        }

        return directory;
    }

}