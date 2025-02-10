package org.palladiosimulator.edp2.distancemetrics.util;

import java.util.Iterator;
import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;

/**
 * This utility class contains functions that are needed by the distance metric algorithms.
 * 
 * @author Christian Schaf
 */
public class Utility {
	
	/**
	 * Checks, if streams contain the same metric and throws an IllegalArgumentException, if it's not
	 * the case.
	 * @param s1 First stream of the metric, that should be compared.
	 * @param s2 Second stream of the metric, that should be compared.
	 */
	public static void legalStreamComparison(final IDataStream<TupleMeasurement> s1, 
	        final IDataStream<TupleMeasurement> s2) {
      	final boolean isLegal = MetricDescriptionUtility
      			.metricDescriptionIdsEqual(s1.getMetricDesciption(), s2.getMetricDesciption());
      	if (!isLegal) {
      		throw new IllegalArgumentException("Streams to compare don't contain same metric.");
      	}
	}
	
	/**
	 * Returns the time unit of a TupleMeasurement stream.
	 * @param stream The stream the unit should be taken from.
	 * @return Returns the unit taken from the metric description of the stream.
	 */
	public static Unit<Quantity> getBaseTimeUnit(final IDataStream<TupleMeasurement> stream) {
		// Get default unit for time value.
		List<MetricDescription> subsumedMetrics = ((MetricSetDescription) stream.getMetricDesciption())
                .getSubsumedMetrics();
		Unit<Quantity> domainUnit = ((NumericalBaseMetricDescription) subsumedMetrics.get(0)).getDefaultUnit();
		return domainUnit;
	}
	
	/**
	 * Returns the value unit of a TupleMeasurement stream.
	 * @param stream The stream the unit should be taken from.
	 * @return Returns the unit taken from the metric description of the stream.
	 */
	public static Unit<Quantity> getBaseValueUnit(IDataStream<TupleMeasurement> stream) {
		// Get default unit for measured value.
		List<MetricDescription> subsumedMetrics = ((MetricSetDescription) stream.getMetricDesciption())
                .getSubsumedMetrics();
		Unit<Quantity> domainUnit = ((NumericalBaseMetricDescription) subsumedMetrics.get(1)).getDefaultUnit();
		return domainUnit;
	}
	
	/** The delta value, that should be used for calculations with numerical values, e.g. comparison of two doubles. */
	public static final double DELTA = 0.0000001;
	
	/**
	 * Compares two values if they are equal in respect of a delta value to avoid
	 * issues with floating point inaccuracy.
	 * @param v1 First value to compare.
	 * @param v2 Second value to compare.
	 * @return True, if absolute difference is smaller or equal the delta value. False otherwise.
	 */
	public static boolean compareEquals(double v1, double v2) {
		return Math.abs(v1 - v2) <=  DELTA;
	}

	/**
	 * Converts Measure unit to an Amount unit.
	 * @param <U> Type of unit (extends Quantity).
	 * @param measure The Measure to be translated into an Amount value.
	 * @return Returns a converted Amount unit.
	 */
	public static <U extends Quantity> Amount<U> quantityToAmount(
	        Measure<?, U> measure) {
	    return Amount.valueOf(measure.doubleValue(measure.getUnit()), measure.getUnit());
	}
	
	/**
	 * Calculates the euclidean distance of two values.
	 * @return The euclidean distance of the two given values.
	 * @param v1 First value to calculate the euclidean distance of.
	 * @param v2 Second value to calculate the euclidean distance of.
	 */
	public static double euclid(double v1, double v2) {
		return Math.abs(v1 - v2);
	}
	
	/**
	 * Calculates the euclidean distance of two values.
	 * @return The euclidean distance of the two given values.
	 * @param amount First value to calculate the euclidean distance of.
	 * @param amount2 Second value to calculate the euclidean distance of.
	 */
	public static Amount<? extends Quantity> euclid(Amount<?> amount, Amount<?> amount2) {
        return amount.minus(amount2).abs();
	}
	
	/**
	 * Converts the given stream to an array of type Amount<?>.
	 * @return Returns the elements of the stream as an array.
	 * @param stream The stream to be translated into an array.
	 */
	public static Amount<?>[] streamToArray(IDataStream<TupleMeasurement> stream) {
		Amount<?>[] array = new Amount<?>[stream.size()];
        Iterator<TupleMeasurement> it = stream.iterator();
		for (int i = 0; i < stream.size(); i++) {
			// Get value of (time, value) tuple
			@SuppressWarnings("unchecked")
			Measure<Double, Quantity> measure = (Measure<Double, Quantity>) it.next().asArray()[1];
			Unit<?> unit = Utility.getBaseValueUnit(stream);
			double measureDouble = measure.doubleValue(Utility.getBaseValueUnit(stream));
			
			array[i] = Amount.valueOf(measureDouble, unit);
		}
		return array;
	}
	
	/**
	 * Converts the given stream to an array of type TupleWrapper.
	 * @return Returns the elements of the stream as an array.
	 * @param stream The stream to be translated into an array.
	 */
	public static TupleWrapper[] streamToTupleWrapperArray(IDataStream<TupleMeasurement> stream) {
		TupleWrapper[] array = new TupleWrapper[stream.size()];
        Iterator<TupleMeasurement> it = stream.iterator();
		for (int i = 0; i < stream.size(); i++) {
			array[i] = new TupleWrapper(it.next());
		}
		return array;
	}
	
	/**
	 * Converts the given stream to a double array and converts
	 * the values with the base unit of the stream.
	 * @return Returns the measured values of the stream as an array.
	 * Time values will be dismissed.
	 * @param stream The stream to be translated into an array.
	 */
	public static double[] streamToDoubleArray(IDataStream<TupleMeasurement> stream) {
		double[] array = new double[stream.size()];
        Iterator<TupleMeasurement> it = stream.iterator();
		for (int i = 0; i < stream.size(); i++) {
			// Get value of (time, value) tuple
			@SuppressWarnings("unchecked")
			Measure<Double, Quantity> measure = (Measure<Double, Quantity>) it.next().asArray()[1];
			//array[i] = measure.doubleValue(measure.getUnit());
			array[i] = measure.doubleValue(Utility.getBaseValueUnit(stream));
		}
		return array;
	}
	
	/**
	 * Converts the given stream to an array of type TupleMeasurement.
	 * @return Returns the elements of the stream as an array of TupleMeasurement elements.
	 * @param stream The stream to be translated into an array.
	 */
	public static TupleMeasurement[] streamToTupleArray(IDataStream<TupleMeasurement> stream) {
		TupleMeasurement[] array = new TupleMeasurement[stream.size()];
        Iterator<TupleMeasurement> it = stream.iterator();
		for (int i = 0; i < stream.size(); i++) {
			array[i] = it.next();
		}
		return array;
	}
	
    /**
     * This implementation uses a Java 2D array for computation which can only fit Integer.MAX_VALUE
	 * which also limits the number of elements to Integer.MAX_VALUE for each measurement.
	 * @param numOfElements1 Number of elements of the first measurement.
	 * @param numOfElements2 Number of elements of the second measurement.
     */
	public static void parametersAllowed(int numOfElements1, int numOfElements2) {
		
	    if (numOfElements1 > Integer.MAX_VALUE || numOfElements2 > Integer.MAX_VALUE) {
	    	throw new IllegalArgumentException("Stream is bigger than Integer.MAX_VALUE.");
	    }
	    if (numOfElements1 < 0 || numOfElements2 < 0) {
	    	throw new IllegalArgumentException("Number of elements is negative.");
	    }
	}
	
	/**
	 * x usually contains the time values and y usually contains the measured values
	 * which were measured at the time which is stored in x.
	 * e.g. at time x[0] the value y[0] was measured.
	 * @param x Values on the x-axis.
	 * @param y The corresponding f(x)=y values.
	 * @param xi Usually the time value of the current OTHER measurement, at which the interpolation
	 * should be applied.
	 * @return Returns the interpolated value at the position xi.
	 */
	public static double linearInterpolation(double[] x, double[] y, double xi) {
		// Return linear interpolation of (x, y) at position xi
		LinearInterpolator li = new LinearInterpolator();
		// Calculate a linear function f(x) (slope and y-axis value at 0) based on the values x and y values.
		PolynomialSplineFunction psf = li.interpolate(x, y);
		// Interpolation at the time value xi (f(xi))
		double yi = psf.value(xi);
		return yi;
	}	
}
