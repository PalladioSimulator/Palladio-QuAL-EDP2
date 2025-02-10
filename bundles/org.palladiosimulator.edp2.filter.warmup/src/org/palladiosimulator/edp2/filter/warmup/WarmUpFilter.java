package org.palladiosimulator.edp2.filter.warmup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Duration;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataSourceListener;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;
import org.palladiosimulator.edp2.datastream.filter.IFilter;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.edp2.visualization.AbstractInput;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

public class WarmUpFilter extends AbstractFilterInput implements IFilter, IPersistableElement {

    IDataSource dataSource;
    private WarmUpFilterConfiguration configuration;

    @Override
    public IDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(IDataSource iDataSource) {
        this.dataSource = iDataSource;

    }

    @Override
    public boolean canAccept(IDataSource dataSource) {
        final BaseMetricDescription[] subMetricDescriptions = MetricDescriptionUtility
                .toBaseMetricDescriptions(dataSource.getMetricDesciption());

        if (subMetricDescriptions.length != 2) {
            return false; // two-dimensional data needed
        }

        if (!subMetricDescriptions[0].getId().equals(MetricDescriptionConstants.POINT_IN_TIME_METRIC.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public IDataStream<TupleMeasurement> getDataStream() {

        WarmUpFilterConfiguration config = getConfiguration();
        
        final double filterTime = config.getWarmupTime();
        Predicate<? super TupleMeasurement> predicate = new Predicate<TupleMeasurement>() {
            
            @Override
            public boolean test(TupleMeasurement tuple) {
                final Measure<Double, Duration> currentTime = tuple
                        .getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
                if (currentTime.getValue() > filterTime)
                    return true;

                return false;
            }

        };

        IDataStream<TupleMeasurement> stream = dataSource.getDataStream();

        return new PredicateFilterStream(stream, predicate);

    }

    @Override
    public WarmUpFilterConfiguration createConfiguration() {
        return new WarmUpFilterConfiguration();
    }

    @Override
    public MeasuringPoint getMeasuringPoint() {
        return dataSource.getMeasuringPoint();
    }

    @Override
    public MetricDescription getMetricDesciption() {
        return dataSource.getMetricDesciption();
    }

    @Override
    public boolean isCompatibleWith(MetricDescription other) {
        // TODO Auto-generated method stub
        return dataSource.isCompatibleWith(other);
    }

    

    @Override
    protected Set<String> getPropertyKeysTriggeringUpdate() {
        Set<String> updateKeys = new HashSet<String>();
        updateKeys.add(WarmUpFilterConfiguration.WARMUP_TIME_KEY);
        updateKeys.add(WarmUpFilterConfiguration.UNIT_KEY);
        return updateKeys;
    }

    @Override
    public void saveState(IMemento memento) {
        WarmupFilterFactory.saveState(memento, this);
        
    }

    @Override
    public String getFactoryId() {
        return WarmupFilterFactory.FACTORY_ID;
    }

    

}
