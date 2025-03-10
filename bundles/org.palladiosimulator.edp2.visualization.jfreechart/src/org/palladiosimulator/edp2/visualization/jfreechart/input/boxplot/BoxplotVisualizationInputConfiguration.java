package org.palladiosimulator.edp2.visualization.jfreechart.input.boxplot;

import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.palladiosimulator.edp2.datastream.configurable.reflective.ConfigurationProperty;
import org.palladiosimulator.edp2.visualization.jfreechart.input.xyplot.XYPlotVisualizationInputConfiguration;

public class BoxplotVisualizationInputConfiguration extends XYPlotVisualizationInputConfiguration {

    /**
     * Keys for persistence of properties
     */
    public static final String INCLUDE_ZERO_KEY = "includeZero";
    public final static String FILL_BOX_KEY = "fillBox";
    public final static String SHOW_MEAN_KEY = "showMean";
    public final static String SHOW_MEDIAN_KEY = "showMedian";
    public final static String SHOW_OUTLIER_KEY = "showOutlier";
    public final static String ITEM_MARGIN_KEY = "itemMargin";
    public final static String MAX_BAR_WIDTH_KEY = "maxBarWidth";
    public final static String WHISKER_WIDTH_KEY = "whiskerWidth";
    public final static String UNIT_KEY = "unit";

    /**
     * The number of bins, i.e. the number of intervals of equal length in which the measurements
     * are counted.
     */
    @ConfigurationProperty(description = "Fill Box")
    private boolean fillBox;

    /**
     * Option to show the mean value line.
     */
    @ConfigurationProperty(description = "Show Mean Value Line")
    private boolean showMean;

    /**
     * Whether to use absolute frequency or relative frequency for the chart. NOTE: this does affect
     * other input items as well, but is specific for histograms, thus it is located here.
     */
    @ConfigurationProperty(description = "Show Median Dot")
    private boolean showMedian;
    
    /**
     * Option to show the outliers.
     */
    @ConfigurationProperty(description = "Show Outlier")
    private boolean showOutlier;

    /**
     * The width of the whitespace between the items in percentage.
     */
    @ConfigurationProperty(description = "Item Margin")
    private double itemMargin;
    
    /**
     * The maximum bar width as percentage.
     */
    @ConfigurationProperty(description = "Max Bar Width")
    private double maxBarWidth;
    

    /**
     * The whisker width as fraction of bar width.
     */
    @ConfigurationProperty(description = "Whisker Width")
    private double whiskerWidth;

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

   

    public boolean isFillBox() {
		return fillBox;
	}



	public boolean isShowMean() {
		return showMean;
	}



	public boolean isShowMedian() {
		return showMedian;
	}



	public boolean isShowOutlier() {
		return showOutlier;
	}



	public double getItemMargin() {
		return itemMargin;
	}



	public double getMaxBarWidth() {
		return maxBarWidth;
	}



	public double getWhiskerWidth() {
		return whiskerWidth;
	}



	@SuppressWarnings("unchecked")
    public <Q extends Quantity> Unit<Q> getUnit() {
        return (Unit<Q>) unit;
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
        result.put(FILL_BOX_KEY, true);
        result.put(SHOW_MEDIAN_KEY, true);
        result.put(SHOW_MEAN_KEY, true);
        result.put(SHOW_OUTLIER_KEY, true);
        result.put(MAX_BAR_WIDTH_KEY, 1.0d);
        result.put(WHISKER_WIDTH_KEY, 1.0d);
        result.put(ITEM_MARGIN_KEY, 0.2d);
        result.put(UNIT_KEY, Unit.ONE);
        return result;
    }

}
