package edu.asu.diging.cord19.explorer.core.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.cord19.explorer.core.model.export.impl.ExportImpl;

public interface ExportRepository extends PagingAndSortingRepository<ExportImpl, String> {

}
