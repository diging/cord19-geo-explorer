package edu.asu.diging.cord19.explorer.core.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.asu.diging.cord19.explorer.core.model.task.impl.TaskImpl;

public interface TaskRepository extends PagingAndSortingRepository<TaskImpl, String> {

}
