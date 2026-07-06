package com.example.projectservice.repository;

import com.example.projectservice.model.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByResponsibleId(Long responsibleId);
}
