package es.uniovi.avib.morphing.projections.backend.security.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import es.uniovi.avib.morphing.projections.backend.security.dto.LoginRequest;
import es.uniovi.avib.morphing.projections.backend.security.dto.LoginResponse;
import es.uniovi.avib.morphing.projections.backend.security.dto.RefreshTokenRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {
	private final static String serverUrl = "http://localhost:8088";
    private final RestTemplate restTemplate;
    
    public ResponseEntity<LoginResponse> login(String realm, String clientId, LoginRequest request)  {
    	log.info("Executing login");
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("grant_type", "password");
        map.add("username", request.getUsername());
        map.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token", httpEntity, LoginResponse.class);        
        
        return ResponseEntity.status(200).body(loginResponse.getBody());
    }
    
    public ResponseEntity<LoginResponse> refreshToken(String realm, String clientId,  RefreshTokenRequest request)  {
    	log.info("Executing refreshToken");
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", request.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token", httpEntity, LoginResponse.class);        
        
        return ResponseEntity.status(200).body(loginResponse.getBody());
    }
        
    public ResponseEntity<String> logout(String realm, String clientId, String refreshToken)  {
    	log.info("Executing logout");
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id",clientId);
        map.add("refresh_token",refreshToken);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> logoutResponse = restTemplate.postForEntity(serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout", httpEntity, String.class);
        
        return ResponseEntity.status(200).body(logoutResponse.getBody());
    }
}
