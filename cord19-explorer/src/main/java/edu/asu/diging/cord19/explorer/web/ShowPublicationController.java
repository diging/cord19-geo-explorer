package edu.asu.diging.cord19.explorer.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;

@Controller
public class ShowPublicationController {

    @Autowired
    private PublicationDao pubDao;

    @RequestMapping("/pubs")
    public String get(Model model, @PageableDefault(size = 20)Pageable pageable, @RequestParam String last, @RequestParam String lastId) {
        List<PublicationImpl> page = pubDao.getPublications(pageable, last, lastId);
        long count = pubDao.getPublicationCount();
        model.addAttribute("pubs", page);
        model.addAttribute("total", count);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount", count / pageable.getPageSize() + (count % pageable.getPageSize() > 0 ? 1 : 0));
        model.addAttribute("sort", pageable.getSort().toString().replace(": ", ","));
        model.addAttribute("order", pageable.getSort().toString().split(": ")[1]);
        return "publications";
    }

}
