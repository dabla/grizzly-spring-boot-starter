package org.springframework.boot.grizzly.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.boot.grizzly.json.ObjectMapperBuilder.anObjectMapper;

@Provider
@Produces(APPLICATION_JSON)
public class ObjectMapperProvider extends JacksonJaxbJsonProvider {
    private static ObjectMapper restMapper = anObjectMapper().build();
    private static ObjectMapper defaultMapper = anObjectMapper().build();
    private static ObjectMapper formattedMapper = anObjectMapper().enableIndentOuput().build();
    
    public ObjectMapperProvider() {
        super();
        setMapper(restMapper);
    }
    
    public static ObjectMapper getRestMapper() {
    	return restMapper;
    }
    
    public static ObjectMapper getDefaultMapper() {
    	return defaultMapper;
    }

    public static ObjectMapper getFormattedMapper() {
    	return formattedMapper;
    }
}