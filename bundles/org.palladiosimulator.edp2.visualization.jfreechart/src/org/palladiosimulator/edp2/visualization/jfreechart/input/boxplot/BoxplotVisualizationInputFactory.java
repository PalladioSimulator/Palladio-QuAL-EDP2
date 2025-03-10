package org.palladiosimulator.edp2.visualization.jfreechart.input.boxplot;

import org.eclipse.ui.IMemento;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;
import org.palladiosimulator.edp2.visualization.jfreechart.input.JFreeChartVisualizationInputFactory;

public class BoxplotVisualizationInputFactory extends JFreeChartVisualizationInputFactory {

    public static final String FACTORY_ID = BoxplotVisualizationInputFactory.class.getCanonicalName();

    @Override
    protected IPropertyConfigurable createElementInternal(final IMemento memento) {
        return new BoxplotVisualizationInput();
    }

}
