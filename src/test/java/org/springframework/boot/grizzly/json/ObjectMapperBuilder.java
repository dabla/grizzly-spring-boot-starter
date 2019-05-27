package org.springframework.boot.grizzly.json;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.IS_GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.SETTER;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperBuilder  {
	private boolean enableDefaultTyping = true;
	private boolean enableIndentOuput = false;

	private ObjectMapperBuilder() {}
	
	public static ObjectMapperBuilder anObjectMapper() {
		return new ObjectMapperBuilder();
	}
	
	public ObjectMapperBuilder disableDefaultTyping() {
		enableDefaultTyping = false;
		return this;
	}
	
	public ObjectMapperBuilder enableIndentOuput() {
		enableIndentOuput = true;
		return this;
	}
	
	public ObjectMapper build() {
		ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(ALWAYS);
        
        if (enableDefaultTyping) {
        	mapper.enableDefaultTyping(NON_FINAL, WRAPPER_OBJECT);
        }
        else {
        	mapper.enableDefaultTyping(NON_CONCRETE_AND_ARRAYS);
        }
        
        if (enableIndentOuput) {
        	mapper.enable(INDENT_OUTPUT);
        }
        
        mapper.setVisibility(FIELD, ANY);
        mapper.setVisibility(GETTER, NONE);
        mapper.setVisibility(IS_GETTER, NONE);
        mapper.setVisibility(SETTER, NONE);
        mapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        
        return mapper;
	}
}
