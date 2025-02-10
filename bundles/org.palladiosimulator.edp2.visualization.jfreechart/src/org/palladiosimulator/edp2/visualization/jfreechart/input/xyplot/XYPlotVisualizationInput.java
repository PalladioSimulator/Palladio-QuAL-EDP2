package org.palladiosimulator.edp2.visualization.jfreechart.input.xyplot;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable;
import org.palladiosimulator.edp2.visualization.jfreechart.editor.JFreeChartEditor;
import org.palladiosimulator.edp2.visualization.jfreechart.input.AbstractXYVisualizationInput;
import org.palladiosimulator.edp2.visualization.jfreechart.input.JFreeChartVisualizationSingleDatastreamInput;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;

/**
 * Input for {@link JFreeChartEditor} .
 *
 * @author Steffen Becker, Dominik Ernst, Roland Richter, Florian Rosenthal
 */
public class XYPlotVisualizationInput extends AbstractXYVisualizationInput {

    public XYPlotVisualizationInput() {
        super();
    }

    @Override
    public String getFactoryId() {
        return XYPlotVisualizationInputFactory.FACTORY_ID;
    }

    @Override
    protected PropertyConfigurable createConfiguration() {
        return new XYPlotVisualizationInputConfiguration();
    }

    /**
     * @return
     */
    @Override
    protected AbstractDataset generateDataset() {
        final DefaultXYDataset dataset = new DefaultXYDataset();

        int i = 1;
        for (final JFreeChartVisualizationSingleDatastreamInput childInput : getInputs()) {
            dataset.addSeries(i + ": " + childInput.getInputName(), getXYData(childInput.getDataSource()));
            i++;
        }
        return dataset;
    }

    /**
     * @param configuration
     * @param dataset
     * @return
     */
    @Override
    protected Plot generatePlot(final PropertyConfigurable config, final AbstractDataset dataset) {
        final XYPlotVisualizationInputConfiguration configuration = (XYPlotVisualizationInputConfiguration) config;
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

    private double[][] getXYData(final IDataSource dataSource) {
        if (!canAccept(dataSource)) {
            throw new IllegalArgumentException("XYData has to be a two-dimensional metric set description.");
        }

        final IDataStream<TupleMeasurement> inputStream = dataSource.getDataStream();
        final List<MetricDescription> subsumedMetrics = ((MetricSetDescription) inputStream.getMetricDesciption())
                .getSubsumedMetrics();
        final Unit<Quantity> domainUnit = ((NumericalBaseMetricDescription) subsumedMetrics.get(getXPos())).getDefaultUnit();
        final Unit<Quantity> rangeUnit = ((NumericalBaseMetricDescription) subsumedMetrics.get(getYPos())).getDefaultUnit();

        try {
            final double[][] result = new double[2][inputStream.size()];
            int i = 0;
            for (final TupleMeasurement tuple : inputStream) {

                @SuppressWarnings("unchecked")
                final
                Measure<?, Quantity>[] measurement = (Measure<?, Quantity>[]) tuple.asArray();

                result[0][i] = measurement[getXPos()].doubleValue(domainUnit); // x
                // (domain)
                result[1][i] = measurement[getYPos()].doubleValue(rangeUnit); // y
                // (range)

                i++;
            }
            return result;
        } finally {
            inputStream.close();
        }
    }

    @Override
    protected Set<String> getPropertyKeysTriggeringUpdate() {
        return Collections.emptySet();
    }
}
