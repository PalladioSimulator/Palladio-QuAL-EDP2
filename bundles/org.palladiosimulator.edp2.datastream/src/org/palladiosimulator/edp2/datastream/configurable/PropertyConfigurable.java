package org.palladiosimulator.edp2.datastream.configurable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;

public abstract class PropertyConfigurable extends AbstractObservable<IPropertyListener>
        implements IPropertyConfigurable {

    public static final Object NOT_SET = new Object();
    private final Map<String, Object> properties = new HashMap<>();
    protected final Set<String> keys;

    public PropertyConfigurable() {
        super();
        this.keys = getKeys();
    }

    public PropertyConfigurable(final Map<String, Object> newProperties) {
        this();
        setProperties(newProperties);
    }

    @Override
    public abstract Map<String, Object> getDefaultConfiguration();

    @Override
    public abstract Set<String> getKeys();

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public void setProperties(final Map<String, Object> newProperties) {
        for (final String key : keys) {
            if (!newProperties.containsKey(key)) {
                throw new IllegalArgumentException("New properties miss expected key " + key);
            }
            if (newProperties.get(key) == null) {
                throw new IllegalArgumentException("New properties has null value for key " + key);
            }
            if (newProperties.get(key) != NOT_SET) {
                final Class<?> fromClass = newProperties.get(key)
                    .getClass();
                final Class<?> propertyType = getPropertyType(key);
                if (!ClassUtils.isAssignable(fromClass, propertyType, true)) {
                    throw new IllegalArgumentException("New properties have wrong type for " + key);
                }
            }
        }
        for (final String key : keys) {
            final Object oldValue = properties.get(key);
            final Object newValue = newProperties.get(key);
            properties.put(key, newProperties.get(key));
            if ((newValue == null && oldValue != null) || (newValue != null && !newValue.equals(oldValue))) {
                this.getEventDispatcher()
                    .propertyChanged(key, oldValue, newValue);
            }
        }
        this.getEventDispatcher()
            .propertyChangeCompleted();
    }

    @Override
    public boolean isPropertyNotSet(final String key) {
        return properties.get(key) == NOT_SET;
    }

    @Override
    public void unsetProperty(final String key) {
        properties.put(key, NOT_SET);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        return null;
    }

    protected Object getNotSetConstant() {
        return NOT_SET;
    }
}
