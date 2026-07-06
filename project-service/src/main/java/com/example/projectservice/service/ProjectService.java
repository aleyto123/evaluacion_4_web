package com.example.projectservice.service;

import com.example.projectservice.dto.ProjectRequest;
import com.example.projectservice.dto.ProjectResponse;
import com.example.projectservice.dto.UserSummary;
import com.example.projectservice.model.Project;
import com.example.projectservice.repository.ProjectRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository repository;
    private final RemoteUserService remoteUserService;
    private final String fallbackMessage;

    public ProjectService(ProjectRepository repository, RemoteUserService remoteUserService,
                          @Value("${app.fallback-message}") String fallbackMessage) {
        this.repository = repository;
        this.remoteUserService = remoteUserService;
        this.fallbackMessage = fallbackMessage;
    }

    public List<ProjectResponse> findAll() {
        log.info("Listando proyectos");
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ProjectResponse findById(Long id) {
        log.info("Consultando proyecto {}", id);
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
    }

    public List<ProjectResponse> findByResponsible(Long responsibleId) {
        log.info("Listando proyectos del responsable {}", responsibleId);
        return repository.findByResponsibleId(responsibleId).stream().map(this::toResponse).toList();
    }

    public ProjectResponse create(ProjectRequest request) {
        log.info("Creando proyecto {}", request.name());
        validateResponsible(request.responsibleId());
        Project project = new Project();
        apply(project, request);
        return toResponse(repository.save(project));
    }

    public ProjectResponse update(Long id, ProjectRequest request) {
        log.info("Actualizando proyecto {}", id);
        Project project = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        validateResponsible(request.responsibleId());
        apply(project, request);
        return toResponse(repository.save(project));
    }

    public void delete(Long id) {
        log.info("Eliminando proyecto {}", id);
        repository.deleteById(id);
    }

    private void validateResponsible(Long responsibleId) {
        if (responsibleId != null && remoteUserService.getResponsible(responsibleId).id() == null) {
            throw new IllegalArgumentException(fallbackMessage);
        }
    }

    private void apply(Project project, ProjectRequest request) {
        project.setName(request.name());
        project.setDescription(request.description());
        project.setResponsibleId(request.responsibleId());
        project.setStatus(request.status() == null ? "PLANNED" : request.status());
        project.setProgress(request.progress() == null ? 0 : Math.max(0, Math.min(100, request.progress())));
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getResponsibleId(),
                remoteUserService.getResponsible(project.getResponsibleId()),
                project.getStatus(),
                project.getProgress(),
                project.getStartDate(),
                project.getEndDate()
        );
    }
}
