package org.palladiosimulator.edp2.visualization.jfreechart.input.cdf;

import java.util.HashMap;
import java.util.Map;

import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.datastream.configurable.reflective.ConfigurationProperty;
import org.palladiosimulator.edp2.visualization.jfreechart.input.xyplot.XYPlotVisualizationInputConfiguration;

/**
 * Options shown in the Properties View for Cumulative Distribution Functions (CDFs).
 *
 * @author Sebastian Lehrig
 */
public class CDFVisualizationInputConfiguration extends XYPlotVisualizationInputConfiguration {

    /**
     * Keys for persistence of properties
     */
    public final static String SHOW_ITEM_VALUES_KEY = "showItemValues";
    public final static String UNIT_KEY = "unit";

    /**
     * Option to show the value for each bar in the histogram, i.e. the absolute or relative number
     * of items contained in each bin.
     */
    @ConfigurationProperty(description = "Show Item Values")
    private boolean showItemValues;

    /**
     * The unit of the horizontal axis as a String.
     */
    @ConfigurationProperty(description = "Domain Unit")
    private Unit<? extends Quantity> unit;

    @SuppressWarnings("unchecked")
    public <Q extends Quantity> Unit<Q> getUnit() {
        return (Unit<Q>) this.unit;
    }

    public boolean isShowItemValues() {
        return this.showItemValues;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.palladiosimulator.edp2.visualization.editors.JFreeChartVisualisationConfiguration#
     * getDefaultConfiguration()
     */
    @Override
    public Map<String, Object> getDefaultConfiguration() {
        final Map<String, Object> result = new HashMap<String, Object>(super.getDefaultConfiguration());
        result.put(SHOW_ITEM_VALUES_KEY, false);
        result.put(UNIT_KEY, Unit.ONE);
        result.put(SHOW_SERIES_LINE_LABEL_KEY, true);
        result.put(SHOW_SERIES_SHAPES_LABEL_KEY, false);
        return result;
    }

}
