package edu.asu.diging.cord19.explorer.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;

@Controller
public class ShowPublicationController {

    @Autowired
    private PublicationDao pubDao;

    @RequestMapping("/publications")
    public String get(Model model, Pageable pageable) {
        model.addAttribute("pubs", pubDao.getPublicationTitles(pageable.getOffset(), pageable.getPageSize()));
        long pubCount = pubDao.getPublicationCount();
        model.addAttribute("total", pubCount);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount",
                pubCount / pageable.getPageSize() + (pubCount % pageable.getPageSize() > 0 ? 1 : 0));
        return "publications";
    }

}
