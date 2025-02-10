package org.palladiosimulator.edp2.visualization.jfreechart.input.pie;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Duration;
import jakarta.measure.unit.SI;

import org.apache.commons.lang.mutable.MutableDouble;
import org.eclipse.ui.IMemento;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.palladiosimulator.edp2.datastream.AbstractDataSource;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.edp2.visualization.jfreechart.input.JFreeChartVisualizationInput;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.Scale;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

public class PieChartVisualizationInput extends JFreeChartVisualizationInput {

    public PieChartVisualizationInput() {
        this(null);
    }

    public PieChartVisualizationInput(final AbstractDataSource source) {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState(final IMemento memento) {
        PieChartVisualizationInputFactory.saveState(memento, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IPersistableElement#getFactoryId()
     */
    @Override
    public String getFactoryId() {
        return PieChartVisualizationInputFactory.FACTORY_ID;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.palladiosimulator.edp2.visualization.editors.JFreeChartVisualizationInput#generatePlot
     * (org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable,
     * org.jfree.data.general.AbstractDataset)
     */
    @Override
    protected Plot generatePlot(final PropertyConfigurable config, final AbstractDataset dataset) {
        final PieChartVisualizationConfiguration configuration = (PieChartVisualizationConfiguration) config;
        final PiePlot3D plot = new PiePlot3D((PieDataset) dataset);
        plot.setNoDataMessage("No data available.");
        plot.setIgnoreNullValues(true);
        plot.setLabelGenerator(
                new StandardPieSectionLabelGenerator(configuration.isShowRelativeAmount() ? "{0} ({2})" : "{0} ({1})"));

        return plot;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.palladiosimulator.edp2.visualization.editors.JFreeChartVisualizationInput#generateDataset
     * ()
     */
    @Override
    protected AbstractDataset generateDataset() {
        final DefaultPieDataset dataset = new DefaultPieDataset();
        final Map<Comparable<?>, MutableDouble> bins = new HashMap<Comparable<?>, MutableDouble>();
        final IDataSource datasource = getInputs().get(0).getDataSource();
        final IDataStream<TupleMeasurement> datastream = datasource.getDataStream();

        TupleMeasurement last = null;
        for (final TupleMeasurement tuple : datastream) {
            if (last != null) {
                final Measure<Double, Duration> currentTime = tuple
                        .getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
                final Measure<Double, Duration> lastTime = last
                        .getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
                final Comparable<?> state = (Comparable<?>) last.asArray()[1].getValue();
                if (!bins.containsKey(state)) {
                    bins.put(state, new MutableDouble(0.0d));
                }
                bins.get(state).add(currentTime.doubleValue(SI.SECOND) - lastTime.doubleValue(SI.SECOND));
            }
            last = tuple;
        }

        for (final Comparable<?> o : bins.keySet()) {
            dataset.setValue(o, bins.get(o).doubleValue());
        }

        return dataset;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.palladiosimulator.edp2.visualization.AbstractInput#getPropertyKeysTriggeringUpdate()
     */
    @Override
    protected Set<String> getPropertyKeysTriggeringUpdate() {
        return Collections.emptySet();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.palladiosimulator.edp2.visualization.editors.JFreeChartVisualizationInput#
     * createConfiguration ()
     */
    @Override
    protected PropertyConfigurable createConfiguration() {
        return new PieChartVisualizationConfiguration();
    }

    @Override
    public boolean canAccept(final IDataSource dataSource) {
        final BaseMetricDescription[] subMetricDescriptions = MetricDescriptionUtility
                .toBaseMetricDescriptions(dataSource.getMetricDesciption());
        if (subMetricDescriptions.length != 2) {
            return false; // two-dimensional data needed
        }

        if (!subMetricDescriptions[0].getId().equals(MetricDescriptionConstants.POINT_IN_TIME_METRIC.getId())) {
            return false;
        }

        return subMetricDescriptions[1].getScale().compareTo(Scale.ORDINAL) <= 0;
    }

}
