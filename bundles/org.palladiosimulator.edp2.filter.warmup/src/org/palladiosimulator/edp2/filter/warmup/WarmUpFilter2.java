package org.palladiosimulator.edp2.filter.warmup;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Duration;

import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.filter.AbstractFilter;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

public class WarmUpFilter2 extends AbstractFilter {

	public WarmUpFilter2(MetricDescription metricDescription) {
		super(metricDescription);
	}
	
	public WarmUpFilter2(IDataSource datasource, MetricDescription metricDescription) {
		super(datasource, metricDescription);
	}

	

	@Override
	protected boolean shouldSkip(MeasuringValue next) {
		WarmUpFilterConfiguration configuration =  this.getConfiguration();
		double time = configuration.getWarmupTime();
		final Measure<Double, Duration> currentTime = next
				.getMeasureForMetric(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
		
		return currentTime.doubleValue(configuration.getUnit()) < time;
	}
	
	

	
}
