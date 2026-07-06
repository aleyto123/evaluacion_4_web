package com.example.taskservice.service;

import com.example.taskservice.dto.ProgressRequest;
import com.example.taskservice.dto.TaskRequest;
import com.example.taskservice.dto.TaskResponse;
import com.example.taskservice.model.ProjectTask;
import com.example.taskservice.repository.TaskRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final RemoteLookupService remoteLookupService;
    private final String fallbackMessage;

    public TaskService(TaskRepository repository, RemoteLookupService remoteLookupService,
                       @Value("${app.fallback-message}") String fallbackMessage) {
        this.repository = repository;
        this.remoteLookupService = remoteLookupService;
        this.fallbackMessage = fallbackMessage;
    }

    public List<TaskResponse> findAll(Long projectId, Long responsibleId) {
        log.info("Listando tareas projectId={} responsibleId={}", projectId, responsibleId);
        if (projectId != null) {
            return repository.findByProjectId(projectId).stream().map(this::toResponse).toList();
        }
        if (responsibleId != null) {
            return repository.findByResponsibleId(responsibleId).stream().map(this::toResponse).toList();
        }
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public TaskResponse findById(Long id) {
        log.info("Consultando tarea {}", id);
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
    }

    public TaskResponse create(TaskRequest request) {
        log.info("Creando tarea {}", request.title());
        validateProject(request.projectId());
        validateResponsible(request.responsibleId());
        ProjectTask task = new ProjectTask();
        apply(task, request);
        return toResponse(repository.save(task));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        log.info("Actualizando tarea {}", id);
        ProjectTask task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        validateProject(request.projectId());
        validateResponsible(request.responsibleId());
        apply(task, request);
        return toResponse(repository.save(task));
    }

    public TaskResponse updateProgress(Long id, ProgressRequest request) {
        log.info("Actualizando avance de tarea {}", id);
        ProjectTask task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.progress() != null) {
            task.setProgress(Math.max(0, Math.min(100, request.progress())));
        }
        return toResponse(repository.save(task));
    }

    public void delete(Long id) {
        log.info("Eliminando tarea {}", id);
        repository.deleteById(id);
    }

    private void validateProject(Long projectId) {
        if (projectId != null && remoteLookupService.getProject(projectId).id() == null) {
            throw new IllegalArgumentException(fallbackMessage);
        }
    }

    private void validateResponsible(Long responsibleId) {
        if (responsibleId != null && remoteLookupService.getResponsible(responsibleId).id() == null) {
            throw new IllegalArgumentException(fallbackMessage);
        }
    }

    private void apply(ProjectTask task, TaskRequest request) {
        task.setProjectId(request.projectId());
        task.setResponsibleId(request.responsibleId());
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() == null ? "PENDING" : request.status());
        task.setProgress(request.progress() == null ? 0 : Math.max(0, Math.min(100, request.progress())));
        task.setDueDate(request.dueDate());
    }

    private TaskResponse toResponse(ProjectTask task) {
        return new TaskResponse(
                task.getId(),
                task.getProjectId(),
                remoteLookupService.getProject(task.getProjectId()),
                task.getResponsibleId(),
                remoteLookupService.getResponsible(task.getResponsibleId()),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getProgress(),
                task.getDueDate()
        );
    }
}
