package org.palladiosimulator.edp2.filter.warmup;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IMemento;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;
import org.palladiosimulator.edp2.datastream.ui.configurable.DataSinkElementFactory;

public class WarmupFilterFactory extends DataSinkElementFactory {

    public static final String FACTORY_ID = WarmupFilterFactory.class.getCanonicalName();

    @Override
    protected IPropertyConfigurable createElementInternal(final IMemento memento) {
        final IAdaptable result = super.createElement(memento);
        return (IPropertyConfigurable) result;
    }

}
