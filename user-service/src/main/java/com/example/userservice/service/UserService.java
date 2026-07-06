package com.example.userservice.service;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserResponse> findAll() {
        log.info("Listando usuarios");
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse findById(Long id) {
        log.info("Consultando usuario {}", id);
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public UserResponse register(UserRequest request) {
        log.info("Registrando usuario {}", request.email());
        repository.findByEmail(request.email()).ifPresent(user -> {
            throw new IllegalArgumentException("El correo ya existe");
        });
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(request.role() == null || request.role().isBlank() ? "MEMBER" : request.role());
        return toResponse(repository.save(user));
    }

    public UserResponse login(LoginRequest request) {
        log.info("Intento de inicio de sesion {}", request.email());
        User user = repository.findByEmail(request.email())
                .filter(found -> found.getPassword().equals(request.password()))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));
        return toResponse(user);
    }

    public UserResponse update(Long id, UserRequest request) {
        log.info("Actualizando usuario {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(request.password());
        }
        user.setRole(request.role());
        return toResponse(repository.save(user));
    }

    public void delete(Long id) {
        log.info("Eliminando usuario {}", id);
        repository.deleteById(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.isActive());
    }
}
