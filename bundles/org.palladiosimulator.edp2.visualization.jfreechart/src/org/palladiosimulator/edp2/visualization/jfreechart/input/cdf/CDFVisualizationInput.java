package org.palladiosimulator.edp2.visualization.jfreechart.input.cdf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;

import org.eclipse.ui.IMemento;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
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
 * A CDFVisualizationInput displays the input data in a Cumulative Distribution Functions (CDFs).
 * Options include the number of bins into which the data is split and whether axis and values are
 * labeled.
 *
 * @author Sebastian Lehrig
 */
public class CDFVisualizationInput extends AbstractXYVisualizationInput {

    /**
     * Empty constructor.
     */
    public CDFVisualizationInput() {
        this(null);
    }

    /**
     * Constructor, with reference on the source. Automatically initiates an update of the
     * {@link #dataset}.
     *
     * @param source
     */
    public CDFVisualizationInput(final AbstractDataSource source) {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistableElement#getFactoryId()
     */
    @Override
    public String getFactoryId() {
        return CDFVisualizationInputFactory.FACTORY_ID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState(final IMemento memento) {
        CDFVisualizationInputFactory.saveState(memento, this);
    }

    @Override
    protected Plot generatePlot(final PropertyConfigurable config, final AbstractDataset dataset) {
        final CDFVisualizationInputConfiguration configuration = (CDFVisualizationInputConfiguration) config;
        final XYPlot plot = new XYPlot();
        final XYDataset xyDataset = (XYDataset) dataset;

        final ValueAxis domainAxis = new NumberAxis(
                configuration.isShowDomainAxisLabel() ? configuration.getDomainAxisLabel() : null);
        final ValueAxis rangeAxis = new NumberAxis(configuration.isShowRangeAxisLabel() ? configuration.getRangeAxisLabel()
                : null);

        plot.setRangeAxis(rangeAxis);
        plot.setDomainAxis(domainAxis);

        plot.setDataset(xyDataset);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        final boolean isShowSeriesLine = configuration.isShowSeriesLine();
        final boolean isShowSeriesShape = configuration.isShowSeriesShapes();

        for (int i = 0; i < xyDataset.getSeriesCount(); ++i) {
            renderer.setSeriesLinesVisible(i, isShowSeriesLine);
            renderer.setSeriesShapesVisible(i, isShowSeriesShape);
        }

        plot.setRenderer(renderer);

        configureSeriesColors(renderer);
        return plot;
    }

    @Override
    protected AbstractDataset generateDataset() {
        final DefaultXYDataset dataset = new DefaultXYDataset();

        int i = 1;
        for (final JFreeChartVisualizationSingleDatastreamInput childInput : getInputs()) {
            dataset.addSeries(i + ": " + childInput.getInputName(), computeCDFData(childInput));
            i++;
        }
        return dataset;
    }

    @Override
    protected PropertyConfigurable createConfiguration() {
        return new CDFVisualizationInputConfiguration();
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
        properties.put(CDFVisualizationInputConfiguration.UNIT_KEY, baseMetric.getDefaultUnit());
        properties.put(XYPlotVisualizationInputConfiguration.DOMAIN_AXIS_LABEL_KEY, getAxisDefaultLabel(getYPos()));
        properties.put(XYPlotVisualizationInputConfiguration.RANGE_AXIS_LABEL_KEY, "Summed Probability [%]");
        setProperties(properties);
    }

    @Override
    protected Set<String> getPropertyKeysTriggeringUpdate() {
        return Collections.emptySet();
    }

    /**
     * TODO: These methods need to be refactored in case the value array does not fit into
     * a double array, i.e., if the measurement count is larger than the maximum allowed index of
     * java arrays.
     * @param childInput The child input whose values should be displayed as CDF
     * @return A two-valued double array. Each entry represents a x,y point. X is the values,
     * Y the summed probability until this value. Based on a sorting of the data in the childInput.
     */
    private double[][] computeCDFData(final JFreeChartVisualizationSingleDatastreamInput childInput) {
        final double[] rawValues = generateData(childInput.getDataSource());
        Arrays.sort(rawValues);
        final double[][] plotData = new double[2][rawValues.length];
        double sum = 0.0d;
        for (int j = 0; j < rawValues.length; j++) {
            plotData[0][j] = rawValues[j];
            plotData[1][j] = sum;
            sum += 1.0d / rawValues.length;
        }
        return plotData;
    }

    @SuppressWarnings("unchecked")
    private double[] generateData(final IDataSource dataSource) {
        final IDataStream<TupleMeasurement> inputStream = dataSource.getDataStream();
        final double[] data = new double[inputStream.size()];
        final CDFVisualizationInputConfiguration configuration = getConfiguration();

        int i = 0;
        for (final TupleMeasurement tuple : inputStream) {
            final Measure<? extends Number, Quantity> measure = (Measure<? extends Number, Quantity>) tuple
                    .asArray()[getYPos()];
            data[i] = measure.doubleValue(configuration.getUnit());
            i++;
        }
        return data;
    }

}
