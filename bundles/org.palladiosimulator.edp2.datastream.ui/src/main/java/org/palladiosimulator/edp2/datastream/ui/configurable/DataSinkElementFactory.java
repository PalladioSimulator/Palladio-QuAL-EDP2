package org.palladiosimulator.edp2.datastream.ui.configurable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.palladiosimulator.edp2.datastream.IDataSink;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;

public abstract class DataSinkElementFactory extends PropertyConfigurableElementFactory {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.palladiosimulator.edp2.visualization.elementfactories.PropertyConfigurableElementFactory
     * #createElement(org.eclipse.ui.IMemento)
     */
    @Override
    public IAdaptable createElement(final IMemento memento) {
        final IAdaptable result = super.createElement(memento);
        final IDataSink input = (IDataSink) result.getAdapter(IDataSink.class);
        final List<IPersistableElement> children = PropertyConfigurableElementFactory.loadChildren(memento);
        input.setDataSource((IDataSource) children.get(0));
        return result;
    }

    public static void saveState(final IMemento memento, final IDataSink childInput) {
        PropertyConfigurableElementFactory.saveConfigurationState(memento, (IPropertyConfigurable) childInput);
        PropertyConfigurableElementFactory.persistChildren(memento, Arrays.asList(childInput.getDataSource()));
    }

}
