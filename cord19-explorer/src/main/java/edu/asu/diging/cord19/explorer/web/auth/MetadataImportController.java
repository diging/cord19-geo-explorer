package edu.asu.diging.cord19.explorer.web.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.service.DocumentImportManager;

@Controller
public class MetadataImportController {

    @Autowired
    private DocumentImportManager importManager;
    
    @RequestMapping(value = "/auth/metadata/import", method = RequestMethod.GET)
    public String show() {

        return "auth/metadataImport";
    }

    @RequestMapping(value = "/auth/metadata/import", method = RequestMethod.POST)
    public String startImport(@RequestParam("filepath") String filepath, @RequestParam(value="type", defaultValue="cord19") String type) throws IOException {
        
        if (type.equals("dimensions")) {
            importManager.startDimensionsMetadataImport(filepath);
        } else {
            importManager.startMetadataImport(filepath);
        }

        return "auth/metadataImportStarted";
    }

}
