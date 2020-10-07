package edu.asu.diging.cord19.explorer.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.asu.diging.cord19.explorer.core.model.export.Export;
import edu.asu.diging.cord19.explorer.core.model.export.impl.ExportImpl;

public interface ExportManager {

    String startExport();

    Page<ExportImpl> listExports(Pageable pageable);

    byte[] getExportFile(Export export);

    Export get(String id);

}