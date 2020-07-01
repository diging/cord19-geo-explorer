package edu.asu.diging.cord19.explorer.web.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.service.worker.ElasticsearchConnector;

@Controller
public class CorrectMatchController {
    
    @Autowired
    private ElasticsearchConnector elasticConnector;

    @RequestMapping(value="/auth/affiliation/wiki/find")
    public ResponseEntity<List<Wikientry>> findWikientries(@RequestParam("title") String title) {
        List<Wikientry> entries = elasticConnector.searchInTitle(title);
        return new ResponseEntity<List<Wikientry>>(entries, HttpStatus.OK);
    }
}
