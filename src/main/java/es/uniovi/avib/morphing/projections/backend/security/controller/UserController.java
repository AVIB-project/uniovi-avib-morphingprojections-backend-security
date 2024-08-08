package es.uniovi.avib.morphing.projections.backend.security.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.uniovi.avib.morphing.projections.backend.security.configuration.KeycloakAdminApiConfig;
import es.uniovi.avib.morphing.projections.backend.security.dto.LoginRequest;
import es.uniovi.avib.morphing.projections.backend.security.dto.LoginResponse;
import es.uniovi.avib.morphing.projections.backend.security.dto.RefreshTokenRequest;
import es.uniovi.avib.morphing.projections.backend.security.dto.UserRequest;
import es.uniovi.avib.morphing.projections.backend.security.service.LoginService;
import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@RestController
@RequestMapping("security")
public class UserController {
	final String CLIENT_NAME = "poc";	
	final Logger log = LoggerFactory.getLogger(UserController.class);
    
    LoginService loginService;
    
    public UserController(LoginService loginService) {
    	this.loginService = loginService;
    }
    
    @RequestMapping(value = "/realms/{realm}/clients/{clientId}/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@PathVariable("realm") String realm, @PathVariable("clientId") String clientId, @RequestBody LoginRequest loginRequest) throws Exception {
        log.info("Executing Login");
                        
        ResponseEntity<LoginResponse> response = loginService.login(realm, clientId, loginRequest);

        return response;
    }
    
    @RequestMapping(value = "/realms/{realm}/clients/{clientId}/refresh", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> refreshToken(@PathVariable("realm") String realm, @PathVariable("clientId") String clientId, @RequestBody RefreshTokenRequest refreshTokenRequest) throws Exception {
        log.info("Executing Refresh Token");
                        
        ResponseEntity<LoginResponse> response = loginService.refreshToken(realm, clientId, refreshTokenRequest);

        return response;
    }
    
    @RequestMapping(value = "/realms/{realm}/users/{userId}/resetPassword", method = RequestMethod.POST)
    public void resetPassword(@PathVariable("realm") String realm, @PathVariable("userId") String userId, @RequestBody String resetPasswordRequest) throws Exception {
        log.info("Executing Reset Password");
                        
        RealmResource realmResource = KeycloakAdminApiConfig.getInstance().realm(realm);
		UsersResource usersResource = realmResource.users();

		UserResource userResource = usersResource.get(userId);
		
	    // set user password
	    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
	    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
	    credentialRepresentation.setTemporary(false);
	    credentialRepresentation.setValue(resetPasswordRequest);
	    	    
	    userResource.resetPassword(credentialRepresentation);	    
    }
    
    @RequestMapping(value = "/realms/{realm}/clients/{clientId}/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(@PathVariable("realm") String realm, @PathVariable("clientId") String clientId, @RequestBody RefreshTokenRequest refreshTokenRequest) throws Exception {
        log.info("Executing Logout");
                        
        ResponseEntity<String> response = loginService.logout(realm, clientId, refreshTokenRequest.getRefreshToken());

        return response;
    }
    
	@RequestMapping(value = "/realms/{realm}/clients", method = RequestMethod.GET)
    public List<ClientRepresentation> getClients(@PathVariable("realm") String realm) throws Exception {
        log.info("Executing Get Clients");
                    	
    	ClientsResource clientsResource = KeycloakAdminApiConfig.getInstance().realm(realm).clients();
    	
    	return clientsResource.findAll();
    }
		 
	@RequestMapping(value = "/realms/{realm}/users", method = RequestMethod.GET)
    public List<UserRepresentation> getUsers(@PathVariable("realm") String realm) throws Exception {
        log.info("Executing Get Users");
                
    	UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();    	
    	
    	return usersResource.list();    	    	
    }
	
	@RequestMapping(value = "/realms/{realm}/users/{userId}", method = RequestMethod.GET)
    public UserRepresentation getUserById(@PathVariable("realm") String realm, @PathVariable("userId") String userId) throws Exception {
        log.info("Executing get User by Id");
                
    	UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();
    	    	    	    
    	return usersResource.get(userId).toRepresentation();       
    }
	
	@RequestMapping(value = "/realms/{realm}/users/{id}/roles", method = RequestMethod.GET)
    public MappingsRepresentation getRoles(@PathVariable("realm") String realm, @PathVariable("id") String id) throws Exception {
        log.info("Executing Get Roles");
                    	
    	UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();

    	return usersResource.get(id).roles().getAll();
    }
	
	@RequestMapping(value = "/realms/{realm}/users", method = RequestMethod.POST)
	public String createUser(@PathVariable("realm") String realm, @RequestBody UserRequest userRequest) {
		log.info("Executing Create User");
		
		RealmResource realmResource = KeycloakAdminApiConfig.getInstance().realm(realm);
		UsersResource usersResource = realmResource.users();
		
	   	UserRepresentation user = new UserRepresentation();
	    user.setUsername(userRequest.getUsername());
	    user.setFirstName(userRequest.getFirstName());
	    user.setLastName(userRequest.getLastName());
	    user.setEmail(userRequest.getEmail());
	    user.setCredentials(new ArrayList<>());
	    user.setAttributes(userRequest.getAttributes());
	    user.setEnabled(userRequest.isEnabled());
	    	           
	    // create user
	    Response response = usersResource.create(user);
	    		    
	    // get user resource
	    String userId = CreatedResponseUtil.getCreatedId(response);
	    UserResource userResource = usersResource.get(userId);
	    	    	    	    
	    // assign Realm Roles to user
	    if (userRequest.getRealmRoles() != null) {
		    List<RoleRepresentation> roleRealmRepresentationAvailable = userResource.roles().realmLevel().listAvailable();
		    List<RoleRepresentation> roleRealmRepresentationRequest = new ArrayList<RoleRepresentation>();
		    
		    // set realm roles available to user
		    for (String userRole : userRequest.getRealmRoles()) {
			    for (RoleRepresentation roleRepresentation : roleRealmRepresentationAvailable) {
			      if (roleRepresentation.getName().equals(userRole)) {
			    	  roleRealmRepresentationRequest.add(roleRepresentation);		          		         
			       }
			    }
		    }
		    
		    // add realm roles to user
		    userResource.roles().realmLevel().add(roleRealmRepresentationRequest);
	    }
	    
	    // assign Client Roles to user
	    if (userRequest.getClientRoles() != null) {	    	    
		    List<RoleRepresentation> roleClientRepresentationRequest = new ArrayList<RoleRepresentation>();
		    
		    for (Entry<String, List<String>> entry : userRequest.getClientRoles().entrySet()) {
			    // get client Id by name
		    	List<ClientRepresentation> clientRepresentations =  realmResource.clients().findByClientId(entry.getKey());
		    			
		    	if (clientRepresentations.size() > 0) {
			    	ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(entry.getKey()).get(0);
				    String clientId = clientRepresentation.getId();
			    	
				    // get client available roles
			    	List<RoleRepresentation> roleClientRepresentationAvailable = userResource.roles().clientLevel(clientId).listAvailable();
			    	
			    	// set client roles to user
			    	for (RoleRepresentation roleRepresentation : roleClientRepresentationAvailable) {
			    		for (String clientRole : entry.getValue()) {	
			    			if (roleRepresentation.getName().equals(clientRole)) {				    	  
						    	 roleClientRepresentationRequest.add(roleRepresentation);
						    }
			    		}
			    	}
		    			    
			    	// add client roles to user
			    	userResource.roles().clientLevel(clientId).add(roleClientRepresentationRequest);
		    	}
		    }		    
	    }
	    	    
	    // set user password
	    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
	    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
	    credentialRepresentation.setTemporary(false);
	    credentialRepresentation.setValue(userRequest.getPassword());
	    	    
	    userResource.resetPassword(credentialRepresentation);
	    
	    return userId;	    
	}

	@RequestMapping(value = "/{realm}/users/{userId}", method = RequestMethod.PUT)
	public void updateUser(@PathVariable("realm") String realm, @PathVariable("userId") String userId, @RequestBody UserRequest userRequest) {
		log.info("Executing Update User");
		
		UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();
			 
		UserResource userResource = usersResource.get(userId);
		
		UserRepresentation user = new UserRepresentation();
	    user.setEnabled(userRequest.isEnabled());
	    user.setUsername(userRequest.getUsername());
	    user.setFirstName(userRequest.getFirstName());
	    user.setLastName(userRequest.getLastName());
	    user.setEmail(userRequest.getEmail());
	    
	    for (Entry<String, List<String>> entry : userRequest.getAttributes().entrySet()) {	    	
	    	user.setAttributes(Collections.singletonMap(entry.getKey(), entry.getValue()));
	    }
	    
	    userResource.update(user);
	}
	
	@RequestMapping(value = "/{realm}/users/{id}", method = RequestMethod.DELETE)
	public int deleteUser(@PathVariable("realm") String realm, @PathVariable("id") String id) {
		log.info("Executing dDelete User");
		
		UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();
			    	         
	    Response response = usersResource.delete(id);
	    	 
	    return response.getStatus();
	}
}
