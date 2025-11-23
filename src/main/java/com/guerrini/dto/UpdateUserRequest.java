package com.guerrini.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import com.guerrini.model.RolesType;

import java.util.Set;

public class UpdateUserRequest {

    @NotBlank
    private String username;

    private String codiceFiscale;
    private String nome;
    private String cognome;

    @NotEmpty
    private Set<RolesType> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Set<RolesType> getRoles() {
        return roles;
    }

    public void setRoles(Set<RolesType> roles) {
        this.roles = roles;
    }
}

