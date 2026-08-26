package org.palladiosimulator.edp2.visualization.jfreechart.input.boxplot;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.eclipse.ui.IMemento;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.palladiosimulator.edp2.datastream.AbstractDataSource;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable;
import org.palladiosimulator.edp2.visualization.jfreechart.input.AbstractXYVisualizationInput;
import org.palladiosimulator.edp2.visualization.jfreechart.input.JFreeChartVisualizationSingleDatastreamInput;
import org.palladiosimulator.edp2.visualization.jfreechart.input.xyplot.XYPlotVisualizationInputConfiguration;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;

/**
 * A BoxplotEditorInput displays the input data in a boxplot.
 *
 * @author Joerg Henss
 *
 */
public class BoxplotVisualizationInput extends AbstractXYVisualizationInput {

    /**
     * Empty constructor.
     */
    public BoxplotVisualizationInput() {
        this(null);
    }

    /**
     * Constructor, with reference on the source. Automatically initiates an update of the
     * {@link #dataset}.
     *
     * @param source
     */
    public BoxplotVisualizationInput(final AbstractDataSource source) {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistableElement#getFactoryId()
     */
    @Override
    public String getFactoryId() {
        return BoxplotVisualizationInputFactory.FACTORY_ID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState(final IMemento memento) {
        BoxplotVisualizationInputFactory.saveState(memento, this);
    }

    @Override
    protected Plot generatePlot(final PropertyConfigurable config, final AbstractDataset dataset) {
        final BoxplotVisualizationInputConfiguration configuration = (BoxplotVisualizationInputConfiguration) config;
        
        
        final CategoryPlot plot = new CategoryPlot();
        
        //from: https://www.jfree.org/forum/viewtopic.php?t=20267
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer() {
        	public CategoryItemRendererState initialise(Graphics2D g2,
        			Rectangle2D dataArea,
        			CategoryPlot plot,
        			int rendererIndex,
        			PlotRenderingInfo info) {
        			CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        			if (state.getBarWidth() > 20)
        			state.setBarWidth(20); // Keeps the circle and chart from being huge
        			return state;
        			}
        			};
        renderer.setFillBox(configuration.isFillBox());
        renderer.setWhiskerWidth(configuration.getWhiskerWidth());
        renderer.setMedianVisible(configuration.isShowMedian());
        renderer.setMeanVisible(configuration.isShowMean());
        //TODO: not yet supported by our jfreechart
        //renderer.setMinOutlierVisible(configuration.isShowOutlier());
        //renderer.setMaxOutlierVisible(configuration.isShowOutlier());
        renderer.setItemMargin(configuration.getItemMargin());
        renderer.setMaximumBarWidth(configuration.getMaxBarWidth());
        
        
        renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator(
        		"X: {1} Mean: {2} Median: {3} Min: {4} Max: {5} Q1: {6} Q3: {7} , MinOutlier: {8}, MaxOutlier: {9}", 
        		NumberFormat.getInstance()) {
        	@Override
            protected Object[] createItemArray(CategoryDataset dataset, int series,
                                               int item) {
                Object[] result = new Object[10];
                result[0] = dataset.getRowKey(series);
                Number y = dataset.getValue(series, item);
                NumberFormat formatter = getNumberFormat();
                result[1] = formatter.format(y);
                if (dataset instanceof BoxAndWhiskerCategoryDataset) {
                    BoxAndWhiskerCategoryDataset d
                        = (BoxAndWhiskerCategoryDataset) dataset;
                    result[2] = formatter.format(d.getMeanValue(series, item));
                    result[3] = formatter.format(d.getMedianValue(series, item));
                    result[4] = formatter.format(d.getMinRegularValue(series, item));
                    result[5] = formatter.format(d.getMaxRegularValue(series, item));
                    result[6] = formatter.format(d.getQ1Value(series, item));
                    result[7] = formatter.format(d.getQ3Value(series, item));
                    result[8] = formatter.format(d.getMinOutlier(series, item));
                    result[9] = formatter.format(d.getMaxOutlier(series, item));
                }
                return result;
            }
        });
        final CategoryAxis domainAxis = new CategoryAxis(
                configuration.isShowDomainAxisLabel() ? configuration.getDomainAxisLabel() : null);
//        domainAxis.setLowerMargin(0.25);
//        domainAxis.setUpperMargin(0.25);
//        domainAxis.setCategoryMargin(0.50);
        final NumberAxis rangeAxis = new NumberAxis(
                configuration.isShowRangeAxisLabel() ? configuration.getRangeAxisLabel() : null);
        rangeAxis.setAutoRangeIncludesZero(configuration.isIncludeZero());

        plot.setDataset((CategoryDataset ) dataset);
        plot.setRenderer(renderer);
        plot.setRangeAxis(rangeAxis);
        plot.setDomainAxis(domainAxis);


        configureSeriesColors(renderer);

       

        return plot;
    }

    @Override
    protected AbstractDataset generateDataset() {
    	
    	final DefaultBoxAndWhiskerCategoryDataset   result = new DefaultBoxAndWhiskerCategoryDataset();
        // add all inputs anew
        // assume that if the getChart()-Method of this input is called, the
        // remaining inputs have the same type of data
        for (int i = 0; i < getInputs().size(); i++) {
        	var list = generateData(getInputs().get(i).getDataSource());
        	result.add(list, i + ":" + getInputs().get(i).getInputName(), getInputs().get(i).getDataSource().getMetricDesciption().getName());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Double> generateData(final IDataSource dataSource) {
        final IDataStream<TupleMeasurement> inputStream = dataSource.getDataStream();
        final Double[] data = new Double[inputStream.size()];
        final BoxplotVisualizationInputConfiguration configuration = getConfiguration();

        int i = 0;
        for (final TupleMeasurement tuple : inputStream) {
        	tuple.asArray();
            final Measure<? extends Number, Quantity> measure = (Measure<? extends Number, Quantity>) tuple.asArray()[getYPos()];
            data[i] = measure.doubleValue(configuration.getUnit());
            i++;
        }
        return Arrays.asList(data);
    }

    @Override
    protected PropertyConfigurable createConfiguration() {
        return new BoxplotVisualizationInputConfiguration();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.palladiosimulator.edp2.visualization.AbstractVisualizationInput#firstChildInputAdded(
     * org.palladiosimulator.edp2.visualization.AbstractVisualizationSingleDatastreamInput)
     */
    @Override
    protected void firstChildInputAdded(final JFreeChartVisualizationSingleDatastreamInput newChildInput) {
        super.firstChildInputAdded(newChildInput);
        final Map<String, Object> properties = new HashMap<String, Object>(getProperties());
        final MetricSetDescription metricSet = (MetricSetDescription) newChildInput.getDataSource()
                .getMetricDesciption();
        final NumericalBaseMetricDescription baseMetric = (NumericalBaseMetricDescription) metricSet
                .getSubsumedMetrics().get(getYPos());
        properties.put(BoxplotVisualizationInputConfiguration.UNIT_KEY, baseMetric.getDefaultUnit());
        properties.put(XYPlotVisualizationInputConfiguration.DOMAIN_AXIS_LABEL_KEY, getAxisDefaultLabel(getYPos()));
        properties.put(XYPlotVisualizationInputConfiguration.RANGE_AXIS_LABEL_KEY, baseMetric.getName());
        setProperties(properties);
    }

    @Override
    protected Set<String> getPropertyKeysTriggeringUpdate() {
        return new HashSet<String>(Arrays.asList(BoxplotVisualizationInputConfiguration.WHISKER_WIDTH_KEY,
        		BoxplotVisualizationInputConfiguration.MAX_BAR_WIDTH_KEY,
                BoxplotVisualizationInputConfiguration.UNIT_KEY));
    }

	@Override
	public boolean canAccept(IDataSource dataSource) {
		// TODO check what datasources we can accept
		return true;
	}
	
	 
}
