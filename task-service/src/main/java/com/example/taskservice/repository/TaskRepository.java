package com.example.taskservice.repository;

import com.example.taskservice.model.ProjectTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByProjectId(Long projectId);
    List<ProjectTask> findByResponsibleId(Long responsibleId);
}
