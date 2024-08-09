package es.uniovi.avib.morphing.projections.backend.security.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import es.uniovi.avib.morphing.projections.backend.security.dto.LoginRequest;
import es.uniovi.avib.morphing.projections.backend.security.dto.LoginResponse;
import es.uniovi.avib.morphing.projections.backend.security.dto.RefreshTokenRequest;
import es.uniovi.avib.morphing.projections.backend.security.dto.UserRequest;
import es.uniovi.avib.morphing.projections.backend.security.service.LoginService;
import es.uniovi.avib.morphing.projections.backend.security.service.RealmService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("security")
public class UserController {    
	private final LoginService loginService;
	private final RealmService realmService;

    @RequestMapping(value = "/realms/{realm}/clients/{clientId}/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@PathVariable("realm") String realm, @PathVariable("clientId") String clientId, @RequestBody LoginRequest loginRequest) throws Exception {
        log.info("Executing login from controller");
                        
        ResponseEntity<LoginResponse> response = loginService.login(realm, clientId, loginRequest);

        return response;
    }
    
    @RequestMapping(value = "/realms/{realm}/clients/{clientId}/refresh", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> refreshToken(@PathVariable("realm") String realm, @PathVariable("clientId") String clientId, @RequestBody RefreshTokenRequest refreshTokenRequest) throws Exception {
        log.info("Executing refreshToken from controller");
                        
        ResponseEntity<LoginResponse> response = loginService.refreshToken(realm, clientId, refreshTokenRequest);

        return response;
    }
    
    @RequestMapping(value = "/realms/{realm}/users/{userId}/resetPassword", method = RequestMethod.POST)
    public void resetPassword(@PathVariable("realm") String realm, @PathVariable("userId") String userId, @RequestBody String resetPasswordRequest) throws Exception {
        log.info("Executing resetPassword from controller");
                   
        loginService.resetPassword(realm, userId, resetPasswordRequest);            
    }
    
    @RequestMapping(value = "/realms/{realm}/clients/{clientId}/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(@PathVariable("realm") String realm, @PathVariable("clientId") String clientId, @RequestBody RefreshTokenRequest refreshTokenRequest) throws Exception {
        log.info("Executing Logout");
                        
        ResponseEntity<String> response = loginService.logout(realm, clientId, refreshTokenRequest.getRefreshToken());

        return response;
    }
    
	@RequestMapping(value = "/realms/{realm}/clients", method = RequestMethod.GET)
    public List<ClientRepresentation> getClients(@PathVariable("realm") String realm) throws Exception {
        log.info("Executing getClients from controller");
            
        return realmService.getClients(realm);
    }
		 
	@RequestMapping(value = "/realms/{realm}/users", method = RequestMethod.GET)
    public List<UserRepresentation> getUsers(@PathVariable("realm") String realm) throws Exception {
        log.info("Executing getUsers from controller");
              
        return realmService.getUsers(realm);   	    	
    }
	
	@RequestMapping(value = "/realms/{realm}/users/{userId}", method = RequestMethod.GET)
    public UserRepresentation getUserById(@PathVariable("realm") String realm, @PathVariable("userId") String userId) throws Exception {
        log.info("Executing getUserById from controller");
                
        return realmService.getUserById(realm, userId);    
    }
	
	@RequestMapping(value = "/realms/{realm}/users/{userId}/roles", method = RequestMethod.GET)
    public MappingsRepresentation getRoles(@PathVariable("realm") String realm, @PathVariable("userId") String userId) throws Exception {
        log.info("Executing getRoles from controller");
                 
        return realmService.getRoles(realm, userId);
    }
	
	@RequestMapping(value = "/realms/{realm}/users", method = RequestMethod.POST)
	public String createUser(@PathVariable("realm") String realm, @RequestBody UserRequest userRequest) {
		log.info("Executing createUser from controller");
		
		return realmService.createUser(realm, userRequest);	    
	}

	@RequestMapping(value = "/realms/{realm}/users/{userId}", method = RequestMethod.PUT)
	public void updateUser(@PathVariable("realm") String realm, @PathVariable("userId") String userId, @RequestBody UserRequest userRequest) {
		log.info("Executing updateUser from controller");
		
		realmService.updateUser(realm, userId, userRequest);	  		
	}
	
	@RequestMapping(value = "/realms/{realm}/users/{userId}", method = RequestMethod.DELETE)
	public int deleteUser(@PathVariable("realm") String realm, @PathVariable("userId") String userId) {
		log.info("Executing deleteUser from controller");
		
		return realmService.deleteUser(realm, userId);
	}
}
