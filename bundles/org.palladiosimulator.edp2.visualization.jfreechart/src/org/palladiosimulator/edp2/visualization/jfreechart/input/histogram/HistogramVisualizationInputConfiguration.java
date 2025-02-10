package org.palladiosimulator.edp2.visualization.jfreechart.input.histogram;

import java.util.HashMap;
import java.util.Map;

import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.datastream.configurable.reflective.ConfigurationProperty;
import org.palladiosimulator.edp2.visualization.jfreechart.input.xyplot.XYPlotVisualizationInputConfiguration;

public class HistogramVisualizationInputConfiguration extends XYPlotVisualizationInputConfiguration {

    /**
     * Keys for persistence of properties
     */
    public static final String INCLUDE_ZERO_KEY = "includeZero";
    public final static String NUMBER_BINS_KEY = "numberOfBins";
    public final static String SHOW_ITEM_VALUES_KEY = "showItemValues";
    public final static String BAR_MARGIN_KEY = "barMargin";
    public final static String ABSOLUTE_FREQUENCY_KEY = "absoluteFrequency";
    public final static String UNIT_KEY = "unit";

    /**
     * The number of bins, i.e. the number of intervals of equal length in which the measurements
     * are counted.
     */
    @ConfigurationProperty(description = "Number of Bins")
    private int numberOfBins;

    /**
     * Option to show the value for each bar in the histogram, i.e. the absolute or relative number
     * of items contained in each bin.
     */
    @ConfigurationProperty(description = "Show Item Values")
    private boolean showItemValues;

    /**
     * Whether to use absolute frequency or relative frequency for the chart. NOTE: this does affect
     * other input items as well, but is specific for histograms, thus it is located here.
     */
    @ConfigurationProperty(description = "Use Absolute Frequency")
    private boolean absoluteFrequency;

    /**
     * The width of the whitespace between the bars in percentage of each bar's width.
     */
    @ConfigurationProperty(description = "Bar Margin")
    private double barMargin;

    /**
     * The unit of the horizontal axis as a String.
     */
    @ConfigurationProperty(description = "Domain Unit")
    private Unit<? extends Quantity> unit;

    /**
     * Always include zero in the diagram?
     */
    @ConfigurationProperty(description = "Include Zeros")
    private boolean includeZero;

    public boolean isIncludeZero() {
        return includeZero;
    }

    public int getNumberOfBins() {
        return numberOfBins;
    }

    @SuppressWarnings("unchecked")
    public <Q extends Quantity> Unit<Q> getUnit() {
        return (Unit<Q>) unit;
    }

    public boolean isAbsoluteFrequency() {
        return absoluteFrequency;
    }

    public double getBarMargin() {
        return barMargin;
    }

    public boolean isShowItemValues() {
        return showItemValues;
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
        result.put(INCLUDE_ZERO_KEY, false);
        result.put(NUMBER_BINS_KEY, 5);
        result.put(SHOW_ITEM_VALUES_KEY, false);
        result.put(BAR_MARGIN_KEY, 0.0d);
        result.put(ABSOLUTE_FREQUENCY_KEY, false);
        result.put(UNIT_KEY, Unit.ONE);
        return result;
    }

}
