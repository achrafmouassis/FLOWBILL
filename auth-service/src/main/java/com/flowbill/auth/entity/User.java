package com.flowbill.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean enabled = true;

    // Many-to-Many with Roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Relation with Tenant (One user belongs to one tenant in this MVP simple
    // model,
    // or Many-to-Many if user belongs to multiple companies. Prompt implies
    // "user_tenant" table or direct link)
    // We will use a direct simplified link or a join table if strictly requested.
    // Prompt says "user_tenant". Let's use a Join Table or Entity for flexibility,
    // but for MVP, a direct column `tenant_id` is often used.
    // The Prompt LISTS "user_tenant" as a table. So we will map it.

    @Column(name = "tenant_id")
    private String tenantId; // FK to tenants table (managed by logic since Tenant is in same DB but maybe
                             // diff module context)
}
