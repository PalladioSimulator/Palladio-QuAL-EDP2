package org.palladiosimulator.edp2.visualization.jfreechart.input.histogram;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;

import org.eclipse.ui.IMemento;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;
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
 * A HistogramEditorInput displays the input data in a histogram either in absolute or relative
 * frequency. Options include the number of bins into which the data is split and whether axis and
 * values are labeled.
 *
 * @author Steffen Becker, Dominik Ernst
 *
 */
public class HistogramVisualizationInput extends AbstractXYVisualizationInput {

    /**
     * Empty constructor.
     */
    public HistogramVisualizationInput() {
        this(null);
    }

    /**
     * Constructor, with reference on the source. Automatically initiates an update of the
     * {@link #dataset}.
     *
     * @param source
     */
    public HistogramVisualizationInput(final AbstractDataSource source) {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistableElement#getFactoryId()
     */
    @Override
    public String getFactoryId() {
        return HistogramVisualizationInputFactory.FACTORY_ID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState(final IMemento memento) {
        HistogramVisualizationInputFactory.saveState(memento, this);
    }

    @Override
    protected Plot generatePlot(final PropertyConfigurable config, final AbstractDataset dataset) {
        final HistogramVisualizationInputConfiguration configuration = (HistogramVisualizationInputConfiguration) config;
        final XYPlot plot = new XYPlot();
        final XYBarRenderer renderer = new XYBarRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardXYBarPainter());

        final NumberAxis domainAxis = new NumberAxis(
                configuration.isShowDomainAxisLabel() ? configuration.getDomainAxisLabel() : null);
        domainAxis.setAutoRangeIncludesZero(configuration.isIncludeZero());
        final NumberAxis rangeAxis = new NumberAxis(
                configuration.isShowRangeAxisLabel() ? configuration.getRangeAxisLabel() : null);

        plot.setDataset((XYDataset) dataset);

        plot.setRenderer(renderer);
        plot.setRangeAxis(rangeAxis);
        plot.setDomainAxis(domainAxis);

        configureSeriesColors(renderer);

        ((HistogramDataset) dataset).setType(configuration.isAbsoluteFrequency() ? HistogramType.FREQUENCY
                : HistogramType.RELATIVE_FREQUENCY);
        renderer.setMargin(configuration.getBarMargin() / 100);

        // show values on each bar in the histogram if the property is set
        renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        renderer.setBaseItemLabelPaint(Color.BLACK);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition());
        renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.TOP_CENTER));
        renderer.setBaseItemLabelsVisible(configuration.isShowItemValues());

        return plot;
    }

    @Override
    protected AbstractDataset generateDataset() {
        final HistogramDataset result = new HistogramDataset();
        // add all inputs anew
        // assume that if the getChart()-Method of this input is called, the
        // remaining inputs have the same type of data
        for (int i = 0; i < getInputs().size(); i++) {
            result.addSeries(getInputs().get(i).getInputName(), generateData(getInputs().get(i).getDataSource()),
                    ((HistogramVisualizationInputConfiguration) getConfiguration()).getNumberOfBins());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private double[] generateData(final IDataSource dataSource) {
        final IDataStream<TupleMeasurement> inputStream = dataSource.getDataStream();
        final double[] data = new double[inputStream.size()];
        final HistogramVisualizationInputConfiguration configuration = getConfiguration();

        int i = 0;
        for (final TupleMeasurement tuple : inputStream) {
            final Measure<? extends Number, Quantity> measure = (Measure<? extends Number, Quantity>) tuple.asArray()[getYPos()];
            data[i] = measure.doubleValue(configuration.getUnit());
            i++;
        }
        return data;
    }

    @Override
    protected PropertyConfigurable createConfiguration() {
        return new HistogramVisualizationInputConfiguration();
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
        properties.put(HistogramVisualizationInputConfiguration.UNIT_KEY, baseMetric.getDefaultUnit());
        properties.put(XYPlotVisualizationInputConfiguration.DOMAIN_AXIS_LABEL_KEY, getAxisDefaultLabel(getYPos()));
        properties.put(XYPlotVisualizationInputConfiguration.RANGE_AXIS_LABEL_KEY, "Frequency [%]");
        setProperties(properties);
    }

    @Override
    protected Set<String> getPropertyKeysTriggeringUpdate() {
        return new HashSet<String>(Arrays.asList(HistogramVisualizationInputConfiguration.NUMBER_BINS_KEY,
                HistogramVisualizationInputConfiguration.UNIT_KEY));
    }

}
