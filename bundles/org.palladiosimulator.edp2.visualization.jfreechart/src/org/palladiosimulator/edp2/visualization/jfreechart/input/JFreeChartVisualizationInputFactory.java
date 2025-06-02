package org.palladiosimulator.edp2.visualization.jfreechart.input;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.palladiosimulator.edp2.datastream.ui.configurable.PropertyConfigurableElementFactory;

/**
 * Factory class for {@link JFreeChartVisualizationInput}. Invokes persistence of all child elements
 * managed by this input
 * 
 * @author Steffen Becker
 * 
 */
public abstract class JFreeChartVisualizationInputFactory extends PropertyConfigurableElementFactory implements
        IElementFactory {

    @Override
    public IAdaptable createElement(final IMemento memento) {
        final JFreeChartVisualizationInput result = (JFreeChartVisualizationInput) super.createElement(memento)
                .getAdapter(JFreeChartVisualizationInput.class);
        final List<IPersistableElement> children = PropertyConfigurableElementFactory.loadChildren(memento);
        for (final IPersistableElement child : children) {
            result.addInput((JFreeChartVisualizationSingleDatastreamInput) child);
        }
        return result;
    }

    public static void saveState(final IMemento memento, final JFreeChartVisualizationInput input) {
        PropertyConfigurableElementFactory.saveConfigurationState(memento, input);
        PropertyConfigurableElementFactory.persistChildren(memento, input.getInputs());
    }

}
