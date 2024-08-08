package es.uniovi.avib.morphing.projections.backend.security.configuration;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeycloakAdminApiConfig {
	static Keycloak keycloak = null;
	
    final static String serverUrl = "http://localhost:8088";	
    final static String realm = "master";
    final static String clientId = "admin-cli";
    final static String adminUserName = "user";
    final static String adminPassword = "vnBSHFD0lT";   
    
    public static Keycloak getInstance() {
    	if(keycloak == null){           
            keycloak = KeycloakBuilder.builder()
            			.serverUrl(serverUrl)
            			.realm(realm)
            			.clientId(clientId)
            			.grantType(OAuth2Constants.PASSWORD)
            			.username(adminUserName)
            			.password(adminPassword)
                    .build();
        }
        
        return keycloak;
    }
}
