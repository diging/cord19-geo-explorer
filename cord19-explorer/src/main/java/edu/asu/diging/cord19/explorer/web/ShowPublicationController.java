package edu.asu.diging.cord19.explorer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;

@Controller
public class ShowPublicationController {

    @Autowired
    private PublicationDao pubDao;

    @RequestMapping("/pubs")
    public String get(Model model, Pageable pageable) {
        Page<PublicationImpl> page = pubDao.getPublications(pageable);
        model.addAttribute("pubs", page);
        long pubCount = page.getTotalElements();
        model.addAttribute("total", pubCount);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount", page.getTotalPages());
        model.addAttribute("sort", pageable.getSort().toString().replace(": ", ","));
        model.addAttribute("order", pageable.getSort().toString().split(": ")[1]);
        return "publications";
    }

}
