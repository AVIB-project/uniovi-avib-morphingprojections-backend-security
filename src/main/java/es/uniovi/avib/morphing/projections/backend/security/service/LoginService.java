package es.uniovi.avib.morphing.projections.backend.security.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.security.configuration.KeycloakAdminApiConfig;
import es.uniovi.avib.morphing.projections.backend.security.configuration.KeycloakConfig;
import es.uniovi.avib.morphing.projections.backend.security.dto.LoginRequest;
import es.uniovi.avib.morphing.projections.backend.security.dto.LoginResponse;
import es.uniovi.avib.morphing.projections.backend.security.dto.RefreshTokenRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {
    private final RestTemplate restTemplate;
    
    public ResponseEntity<LoginResponse> login(String realm, String clientId, LoginRequest request)  {
    	log.info("Executing login from service");
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("grant_type", "password");
        map.add("username", request.getUsername());
        map.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(KeycloakConfig.getServerUrl() + "/realms/" + realm + "/protocol/openid-connect/token", httpEntity, LoginResponse.class);        
        
        return ResponseEntity.status(200).body(loginResponse.getBody());
    }
    
    public ResponseEntity<LoginResponse> refreshToken(String realm, String clientId,  RefreshTokenRequest request)  {
    	log.info("Executing refreshToken from service");
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", request.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(KeycloakConfig.getServerUrl() + "/realms/" + realm + "/protocol/openid-connect/token", httpEntity, LoginResponse.class);        
        
        return ResponseEntity.status(200).body(loginResponse.getBody());
    }
        
    public ResponseEntity<String> logout(String realm, String clientId, String refreshToken)  {
    	log.info("Executing logout from service");
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id",clientId);
        map.add("refresh_token",refreshToken);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> logoutResponse = restTemplate.postForEntity(KeycloakConfig.getServerUrl() + "/realms/" + realm + "/protocol/openid-connect/logout", httpEntity, String.class);
        
        return ResponseEntity.status(200).body(logoutResponse.getBody());
    }
    
    public void resetPassword(String realm, String userId, String resetPasswordRequest) throws Exception {
        log.info("Executing resetPassword from service");
                        
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
}
