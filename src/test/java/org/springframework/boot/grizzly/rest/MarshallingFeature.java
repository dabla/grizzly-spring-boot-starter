package org.springframework.boot.grizzly.rest;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import org.springframework.boot.grizzly.json.ObjectMapperProvider;

public class MarshallingFeature implements Feature {
	@Deprecated
	public static final String JSON_FEATURE = "jersey.config.jsonFeature"; // should be removed once we upgraded jersey with JDK 8
	
    @Override
    public boolean configure(FeatureContext context) {
    	context.register(JsonParseExceptionMapper.class);
        context.register(JsonMappingExceptionMapper.class);
        context.register(ObjectMapperProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
        
        Configuration config = context.getConfiguration();
        // Disables discoverability of org.glassfish.jersey.jackson.JacksonFeature
        context.property(getPropertyNameForRuntime(JSON_FEATURE, config.getRuntimeType()), MarshallingFeature.class.getSimpleName());
        
        return true;
    }
    
    @Deprecated
    // should be removed once we upgraded jersey with JDK 8
    private static String getPropertyNameForRuntime(String key, RuntimeType runtimeType) {
        if (runtimeType != null && key.startsWith("jersey.config")) {
            RuntimeType[] types = RuntimeType.values();
            for (RuntimeType type : types) {
                if (key.startsWith("jersey.config." + type.name().toLowerCase())) {
                    return key;
                }
            }
            return key.replace("jersey.config", "jersey.config." + runtimeType.name().toLowerCase());
        }
        return key;
    }
}