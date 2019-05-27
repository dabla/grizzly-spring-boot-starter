package org.springframework.boot.grizzly.json;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

public class LongSequenceGenerator extends ObjectIdGenerator<Long> {
    private static final long serialVersionUID = 1L;

    private final Class<?> scope;
    private transient long nextValue;
    
	private LongSequenceGenerator() {
    	this(Object.class, -1);
    }

    private LongSequenceGenerator(Class<?> scope, long fv) {
        this.scope = scope;
        this.nextValue = fv;
    }

    protected int initialValue() { return 1; }
    
    @Override
    public ObjectIdGenerator<Long> forScope(Class<?> scope) {
        return (this.scope == scope) ? this : new LongSequenceGenerator(scope, nextValue);
    }
    
    @Override
    public ObjectIdGenerator<Long> newForSerialization(Object context) {
        return new LongSequenceGenerator(scope, initialValue());
    }

    @Override
    public IdKey key(Object key) {
        if (key == null) {
            return null;
        }
        return new IdKey(getClass(), scope, key);
    }
    
    @Override
    public Long generateId(Object forPojo) {
        if (forPojo == null)  {
            return null;
        }
        long id = nextValue;
        ++nextValue;
        return id;
    }
    
	@Override
	public Class<?> getScope() {
		return scope;
	}
	
	@Override
	public boolean canUseFor(ObjectIdGenerator<?> gen) {
		return (gen.getClass() == getClass()) && (gen.getScope() == scope);
	}
}