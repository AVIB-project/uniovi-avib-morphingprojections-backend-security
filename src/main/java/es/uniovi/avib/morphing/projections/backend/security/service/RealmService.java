package es.uniovi.avib.morphing.projections.backend.security.service;

import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

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

import es.uniovi.avib.morphing.projections.backend.security.configuration.KeycloakAdminApiConfig;
import es.uniovi.avib.morphing.projections.backend.security.dto.UserRequest;

@Slf4j
@Service
public class RealmService {
	public List<ClientRepresentation> getClients(String realm) throws Exception {
		log.info("Executing getClients from service");
                    	
    	ClientsResource clientsResource = KeycloakAdminApiConfig.getInstance().realm(realm).clients();
    	
    	return clientsResource.findAll();
    }	
	
    public List<UserRepresentation> getUsers(String realm) throws Exception {
    	log.info("Executing getUsers from service");
                
    	UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();    	
    	
    	return usersResource.list();    	    	
    }
    
    public UserRepresentation getUserById(String realm, String userId) throws Exception {
    	log.info("Executing getUserById from service");
                
    	UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();
    	    	    	    
    	return usersResource.get(userId).toRepresentation();       
    } 
    
    public MappingsRepresentation getRoles(String realm, String id) throws Exception {
    	log.info("Executing getRoles from service");
                    	
    	UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();

    	return usersResource.get(id).roles().getAll();
    }   
    
	public String createUser(String realm, UserRequest userRequest) {
		log.info("Executing createUser from service");
		
		RealmResource realmResource = KeycloakAdminApiConfig.getInstance().realm(realm);
		UsersResource usersResource = realmResource.users();
		
	   	UserRepresentation user = new UserRepresentation();
	    user.setUsername(userRequest.getUsername());
	    user.setFirstName(userRequest.getFirstName());
	    user.setLastName(userRequest.getLastName());
	    user.setEmail(userRequest.getEmail());
	    user.setCredentials(new ArrayList<>());
	    user.setAttributes(userRequest.getAttributes());
	    user.setEmailVerified(true);
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
	
	public void updateUser(String realm, String userId, UserRequest userRequest) {
		log.info("Executing updateUser from service");
		
		UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();
			 
		UserResource userResource = usersResource.get(userId);
		
		UserRepresentation user = new UserRepresentation();
	    user.setEnabled(userRequest.isEnabled());
	    user.setUsername(userRequest.getUsername());
	    user.setFirstName(userRequest.getFirstName());
	    user.setLastName(userRequest.getLastName());
	    user.setEmail(userRequest.getEmail());
	    
	    if (userRequest.getAttributes() != null) {
		    for (Entry<String, List<String>> entry : userRequest.getAttributes().entrySet()) {	    	
		    	user.setAttributes(Collections.singletonMap(entry.getKey(), entry.getValue()));
		    }
	    }
	    
	    userResource.update(user);
	}
	
	public int deleteUser(String realm, String id) {
		log.info("Executing deleteUser");
		
		UsersResource usersResource = KeycloakAdminApiConfig.getInstance().realm(realm).users();
			    	         
	    Response response = usersResource.delete(id);
	    	 
	    return response.getStatus();
	}	
}
