package org.palladiosimulator.edp2.filter.exponentialsmoothing;

import java.util.ArrayList;
import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Duration;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.filter.AbstractFilter;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

public class ExponentialDecayingFilter extends AbstractFilter {

    private BaseMetricDescription valueMetric;
    
    Measure<Double, Quantity> average = null;
    MeasuringValue lastTime = null;

    // TODO make this parametrizable
    private static double ALPHA = 0.1;
    private static double TAU;
    
    static {
        TAU = -1d/Math.log(1-ALPHA);
    }
    
    public ExponentialDecayingFilter(IDataSource datasource, MetricDescription metricDescription) {
        super(datasource, metricDescription);
        MetricSetDescription set = (MetricSetDescription) this.getMetricDesciption();
        this.valueMetric = (BaseMetricDescription) set.getSubsumedMetrics().stream()
                .filter(m -> !m.getId().equals(MetricDescriptionConstants.POINT_IN_TIME_METRIC.getId())).findAny().get();
    }

    @Override
    protected MeasuringValue computeOutputFromInput(MeasuringValue next) {
        MeasuringValue curTime = next.getMeasuringValueForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
        Measure measuringValue = next.getMeasureForMetric(valueMetric);
        double curTimeVal = curTime.<Double, Duration>getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC).getValue();
        if(average == null || curTimeVal < lastTime.<Double, Duration>getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC).getValue()) {
            average = measuringValue;
            this.lastTime = curTime;
            return next;
        } else {
            Unit unit = measuringValue.getUnit();
            double curValue = measuringValue.doubleValue(unit);            
            double oldValue = average.doubleValue(unit);
            double lastTimeVal = lastTime.<Double, Duration>getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC).getValue();
            
            double newEstimateVal = oldValue + (curValue-oldValue)*(1-Math.exp(-(curTimeVal-lastTimeVal)/TAU));
            average = Measure.valueOf(newEstimateVal, unit);  
            this.lastTime = curTime;
            final MeasuringValue averageMeasuringValue = new BasicMeasurement<Double, Quantity>(average, this.valueMetric);
            final List<MeasuringValue> result = new ArrayList<MeasuringValue>(2);
            result.add(curTime);
            result.add(averageMeasuringValue);
            return new TupleMeasurement(result, (MetricSetDescription) this.getMetricDesciption());
        }
    }

}
