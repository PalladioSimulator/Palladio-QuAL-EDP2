package org.palladiosimulator.edp2.datastream.ui.configurable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;
import org.palladiosimulator.commons.emfutils.EMFLoadHelper;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable;

/**
 * A factory for persistence of {@link IDataFlow} elements.
 *
 * @author Dominik Ernst
 *
 */
public abstract class PropertyConfigurableElementFactory implements IElementFactory {

    private static final String CHILD_INPUTS_MEMENTO_TAG = PropertyConfigurableElementFactory.class.getCanonicalName()
            + "childInputs";

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
     */
    @Override
    public IAdaptable createElement(final IMemento memento) {
        final IPropertyConfigurable result = createElementInternal(memento);
        final Map<String, Object> newProperties = getPropertiesFromMemento(memento, result);
        result.setProperties(newProperties);

        return result;
    }

    public static void saveConfigurationState(final IMemento memento, final IPropertyConfigurable configurable) {
        final Map<String, Object> props = configurable.getProperties();

        for (final String key : props.keySet()) {
            if (!configurable.isPropertyNotSet(key)) {
                memento.putString(key, serialize(props.get(key)));
            } else {
                memento.putString(key, null);
            }
        }
    }

    private static String serialize(final Object object) {
        if (ClassUtils.isAssignable(object.getClass(), Color.class, true)) {
            final Color col = (Color) object;
            return String.format("%08x", col.getRGB());
        } else if (ClassUtils.isAssignable(object.getClass(), EObject.class, true)) {
            return EMFLoadHelper.getResourceURI((EObject) object);
        }
        return object.toString();
    }

    /**
     * @param memento
     * @param input
     */
    @SuppressWarnings("rawtypes")
    public static void persistChildren(final IMemento memento, final Collection children) {
        for (final Object childObject : children) {
            final IPersistableElement child = (IPersistableElement) childObject;
            final IMemento subMemento = memento.createChild(CHILD_INPUTS_MEMENTO_TAG, child.getFactoryId());
            child.saveState(subMemento);
        }
    }

    public static List<IPersistableElement> loadChildren(final IMemento memento) {
        final IMemento[] inputMementos = memento.getChildren(CHILD_INPUTS_MEMENTO_TAG);
        final List<IPersistableElement> result = new ArrayList<>(inputMementos.length);
        for (final IMemento subMemento : inputMementos) {
            final String id = subMemento.getID();
            final IElementFactory inputFactory = PlatformUI.getWorkbench()
                .getElementFactory(id);
            final IPersistableElement createdInput = (IPersistableElement) inputFactory.createElement(subMemento);
            result.add(createdInput);
        }
        return result;
    }

    /**
     * Method used during restoration of persisted elements. It is the same for all implementations
     * of {@link IDataFlow}.
     *
     * @param memento
     *            the {@link IMemento} from which the properties are read
     * @param propertiesToOverride
     *            the properties in which the values are to be replaced
     */
    private Map<String, Object> getPropertiesFromMemento(final IMemento memento,
            final IPropertyConfigurable configurable) {
        final Map<String, Object> result = new HashMap<>();
        for (final String key : configurable.getKeys()) {
            if (memento.getString(key) == null) {
                result.put(key, PropertyConfigurable.NOT_SET);
            } else {
                result.put(key, deserialize(memento.getString(key), configurable.getPropertyType(key)));
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private Object deserialize(final String string, final Class<?> propertyType) {
        if (ClassUtils.isAssignable(propertyType, String.class, true)) {
            return string;
        } else if (ClassUtils.isAssignable(propertyType, Boolean.class, true)) {
            return "true".equals(string);
        } else if (ClassUtils.isAssignable(propertyType, Integer.class, true)) {
            return Integer.parseInt(string);
        } else if (ClassUtils.isAssignable(propertyType, Float.class, true)) {
            return Float.parseFloat(string);
        } else if (ClassUtils.isAssignable(propertyType, Double.class, true)) {
            return Double.parseDouble(string);
        } else if (ClassUtils.isAssignable(propertyType, Color.class, true)) {
            final Color col = Color.decode(string.substring(2));
            return new Color(col.getRed(), col.getBlue(), col.getBlue(), Integer.parseInt(string.substring(0, 1), 16));
        } else if (ClassUtils.isAssignable(propertyType, EObject.class, true)) {
            return EMFLoadHelper.loadAndResolveEObject(string);
        }
        throw new UnsupportedOperationException("Deserialize of unsupported type found: " + propertyType);
    }

    protected abstract IPropertyConfigurable createElementInternal(IMemento memento);
}
