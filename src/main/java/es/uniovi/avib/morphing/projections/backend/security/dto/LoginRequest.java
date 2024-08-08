package es.uniovi.avib.morphing.projections.backend.security.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
