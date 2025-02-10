package org.palladiosimulator.edp2.distancemetrics.tests;

import static org.junit.Assert.assertEquals;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Duration;
import jakarta.measure.unit.SI;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.distancemetrics.EuclideanDistance;
import org.jscience.physics.amount.Amount;
import org.junit.Test;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * This class contains test cases for the Euclidean Distance distance metric.
 * 
 * @author Christian Schaf
 */
public class TestEuclidean {
	private final double delta = 0.000001;
	private final EuclideanDistance ed = new EuclideanDistance();
	
	/**
	 * Tests if the result is identical to values calculated by hand.
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
		
		Amount<?> result = ed.calculateDistance(tds1.getDataStream(), tds2.getDataStream());
		assertEquals(result.getEstimatedValue(), 4.0, delta);
	}
	
	/**
	 * Tests if the algorithm can interpolate values, i.e. if time values aren't the same, interpolation between values
	 * needs to be applied.
	 */
	@Test
	public void sampleDataInterpolationTest() {
		final TestDataSource tds1 = new TestDataSource();
		final TestDataSource tds2 = new TestDataSource();
		
		Measure<Double, Duration> t11 = Measure.valueOf(0.0, SI.SECOND);
		Measure<Double, Duration> t12 = Measure.valueOf(1.0, SI.SECOND);
		Measure<Double, Duration> t13 = Measure.valueOf(2.0, SI.SECOND);
		Measure<Double, Duration> t14 = Measure.valueOf(3.0, SI.SECOND);
        
		Measure<Double, Dimensionless> m11 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m12 = Measure.valueOf(5.0, Unit.ONE);
		Measure<Double, Dimensionless> m13 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m14 = Measure.valueOf(2.0, Unit.ONE);
        
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t11, m11));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t12, m12));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t13, m13));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t14, m14));
		
		Measure<Double, Duration> t21 = Measure.valueOf(0.0, SI.SECOND);
		// The value t22 is different from the others.
		Measure<Double, Duration> t22 = Measure.valueOf(1.5, SI.SECOND);
		Measure<Double, Duration> t23 = Measure.valueOf(2.0, SI.SECOND);
		Measure<Double, Duration> t24 = Measure.valueOf(3.0, SI.SECOND);
		
		Measure<Double, Dimensionless> m21 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m22 = Measure.valueOf(2.0, Unit.ONE);
		Measure<Double, Dimensionless> m23 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m24 = Measure.valueOf(1.0, Unit.ONE);
		
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t21, m21));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t22, m22));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t23, m23));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t24, m24));
		
		Amount<?> result = ed.calculateDistance(tds1.getDataStream(), tds2.getDataStream());
		assertEquals(result.getEstimatedValue(), 3.5, delta);
	}
}


