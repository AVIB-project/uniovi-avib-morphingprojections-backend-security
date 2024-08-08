package es.uniovi.avib.morphing.projections.backend.security.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class UserRequest {
	private boolean enabled;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String password;
	private List<String> realmRoles;
	private Map<String, List<String>> clientRoles;
	private Map<String, List<String>> attributes;
}
