package edu.asu.diging.cord19.explorer.web.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.model.export.impl.ExportImpl;
import edu.asu.diging.cord19.explorer.core.service.ExportManager;

@Controller
public class ListExportController {
    
    @Autowired
    private ExportManager manager;

    @RequestMapping("/auth/export/list")
    public String get(Model model, Pageable pageable) {
        Page<ExportImpl> page = manager.listExports(pageable);
        model.addAttribute("exports", page.toList());
        model.addAttribute("page", page.getNumber());
        model.addAttribute("pageCount", page.getTotalPages());
        model.addAttribute("total", page.getTotalElements());
        return "auth/exportList";
    }
}
