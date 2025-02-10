package org.palladiosimulator.edp2.distancemetrics.tests;

import org.junit.Test;
import org.palladiosimulator.edp2.distancemetrics.DynamicTimeWarping;
import org.palladiosimulator.edp2.distancemetrics.util.Utility;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

import static org.junit.Assert.assertEquals;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Duration;
import jakarta.measure.unit.SI;
import jakarta.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

/**
 * This class contains test cases for the DTW distance metric.
 * 
 * @author Christian Schaf
 */
public class TestDTW {	
	private final DynamicTimeWarping dtw = new DynamicTimeWarping();
	
	/**
	 * Tests if the result is identical to values calculated by hand.
	 * The following sample values were taken from
	 * https://de.wikipedia.org/wiki/Dynamic-Time-Warping#/media/File:Animation_Dynamic_Time_Warping.gif
	 */
	@Test
	public void sampleDataTest() {
		final TestDataSource tds1 = new TestDataSource();
		final TestDataSource tds2 = new TestDataSource();
		
		Measure<Double, Duration> t1 = Measure.valueOf(0.0, SI.SECOND);
		Measure<Double, Duration> t2 = Measure.valueOf(1.0, SI.SECOND);
		Measure<Double, Duration> t3 = Measure.valueOf(2.0, SI.SECOND);
		Measure<Double, Duration> t4 = Measure.valueOf(3.0, SI.SECOND);
        
		Measure<Double, Dimensionless> m11 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m12 = Measure.valueOf(5.0, Unit.ONE);
		Measure<Double, Dimensionless> m13 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m14 = Measure.valueOf(2.0, Unit.ONE);
        
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t1, m11));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t2, m12));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t3, m13));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t4, m14));
		
		Measure<Double, Dimensionless> m21 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m22 = Measure.valueOf(2.0, Unit.ONE);
		Measure<Double, Dimensionless> m23 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m24 = Measure.valueOf(1.0, Unit.ONE);
		
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t1, m21));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t2, m22));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t3, m23));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t4, m24));
		
		Amount<?> result = dtw.calculateDistance(tds1.getDataStream(), tds2.getDataStream());
		assertEquals(3.0, result.getEstimatedValue(), Utility.DELTA);
	}
}
