package es.uniovi.avib.morphing.projections.backend.security.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service("authorizeService")
public class AuthorizeService {
 	private List<String> roles = new ArrayList<String>(
			Arrays.asList("admin","operator", "user"));
	
    public List<String> getRoles() {        
        return roles;
    }
}