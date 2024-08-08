package es.uniovi.avib.morphing.projections.backend.security.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class KeycloakConfig {
    private static String serverUrl;
	private static String adminUsername;
	private static String adminPassword;
	
    @Value("${keycloak.server-url}")
    public void setServerUrl(String value) {
    	serverUrl = value;
    }
    
    public static String getServerUrl() {
        return serverUrl;
    }
    
    @Value("${keycloak.admin-username}")
    public void setAdminUsername(String value) {
    	adminUsername = value;
    }
    
    public static String getAdminUsername() {
        return adminUsername;
    }
    
    @Value("${keycloak.admin-password}")
    public void setAdminPassword(String value) {
    	adminPassword = value;
    }
    
    public static String getAdminPassword() {
        return adminPassword;
    }
}
