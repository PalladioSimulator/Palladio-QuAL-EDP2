package org.palladiosimulator.edp2.visualization.jfreechart.input;

import org.eclipse.ui.IMemento;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;
import org.palladiosimulator.edp2.datastream.ui.configurable.DataSinkElementFactory;

public class JFreeChartVisualizationSingleDatastreamInputFactory extends DataSinkElementFactory {

    public static final String FACTORY_ID = JFreeChartVisualizationSingleDatastreamInputFactory.class
            .getCanonicalName();

    @Override
    protected IPropertyConfigurable createElementInternal(final IMemento memento) {
        return new JFreeChartVisualizationSingleDatastreamInput();
    }
}
