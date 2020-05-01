package edu.asu.diging.cord19.explorer.web.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.service.DocumentImportManager;

@Controller
public class ImportController {

    @Autowired
    private DocumentImportManager importManager;

    @RequestMapping(value = "/auth/import", method = RequestMethod.GET)
    public String show() {

        return "auth/import";
    }

    @RequestMapping(value = "/auth/import", method = RequestMethod.POST)
    public String startImport(@RequestParam("rootFolder") String rootFolder) throws IOException {
        importManager.startImport(rootFolder);

        return "auth/importStarted";
    }

}
