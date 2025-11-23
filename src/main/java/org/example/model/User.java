package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    private String fiscalCode;
    private String firstName;
    private String lastName;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @Enumerated(EnumType.STRING)
//    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
//    @Column(name = "role")
//    private Set<RolesType> roles;

    public User() {
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String codiceFiscale) {
        this.fiscalCode = codiceFiscale;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String nome) {
        this.firstName = nome;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String cognome) {
        this.lastName = cognome;
    }

//    public Set<RolesType> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Set<RolesType> roles) {
//        this.roles = roles;
//    }
}

