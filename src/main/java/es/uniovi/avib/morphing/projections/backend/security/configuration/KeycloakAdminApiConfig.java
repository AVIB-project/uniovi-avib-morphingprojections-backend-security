package es.uniovi.avib.morphing.projections.backend.security.configuration;

import org.springframework.stereotype.Component;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KeycloakAdminApiConfig {
	private static Keycloak keycloak = null;
		
	private final static String realm = "master";
	private final static String clientId = "admin-cli"; 
    
    public static Keycloak getInstance() {
    	if(keycloak == null){           
            keycloak = KeycloakBuilder.builder()
            			.serverUrl(KeycloakConfig.getServerUrl())
            			.realm(realm)
            			.clientId(clientId)
            			.grantType(OAuth2Constants.PASSWORD)
            			.username(KeycloakConfig.getAdminUsername())
            			.password(KeycloakConfig.getAdminPassword())
                    .build();
        }
        
        return keycloak;
    }
}
