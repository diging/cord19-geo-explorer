package edu.asu.diging.cord19.explorer.web.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.service.ExportManager;

@Controller
public class DownloadExportController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ExportManager exManager;

    @RequestMapping(value = "/auth/export/{id}/download")
    public void download(@PathVariable("id") String id, HttpServletResponse response) {
        Export export = exManager.get(id);
        
        if (export == null) {
            return;
        }
         
        byte[] file = exManager.getExportFile(export);
        
        try {
            InputStream is = new ByteArrayInputStream(file);
            response.setHeader("Content-disposition", "attachment; filename=" + export.getFilename());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            return;
        } catch (IOException ex) {
            logger.info("Error writing file to output stream. Filename was '{}'", export.getFilename(), ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}
