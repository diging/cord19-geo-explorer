package edu.asu.diging.cord19.explorer.web.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.service.ArxivImportManager;

@Controller
public class ArxivImportController {

    @Autowired
    private ArxivImportManager importManager;

    @RequestMapping(value = "/auth/import/arxiv", method = RequestMethod.GET)
    public String show() {

        return "auth/importArxiv";
    }

    @RequestMapping(value = "/auth/import/arxiv", method = RequestMethod.POST)
    public String startImport(@RequestParam("searchTerm") String searchTerm) throws IOException {
        importManager.startImport(searchTerm);
        return "auth/importStarted";
    }

}
