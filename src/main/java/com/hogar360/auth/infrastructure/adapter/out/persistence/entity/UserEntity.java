package com.hogar360.auth.infrastructure.adapter.out.persistence.entity;

import com.hogar360.auth.domain.model.AuthProvider;
import com.hogar360.auth.domain.model.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA. Vive solo en infraestructura: el dominio jamás la ve.
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 20)
    private AuthProvider authProvider;

    protected UserEntity() {
        // requerido por JPA
    }

    public UserEntity(Long id, String name, String email, String password, Role role, LocalDateTime createdAt) {
        this(id, name, email, password, role, createdAt, AuthProvider.LOCAL);
    }

    public UserEntity(Long id, String name, String email, String password, Role role,
                      LocalDateTime createdAt, AuthProvider authProvider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.authProvider = authProvider;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public AuthProvider getAuthProvider() { return authProvider; }
}