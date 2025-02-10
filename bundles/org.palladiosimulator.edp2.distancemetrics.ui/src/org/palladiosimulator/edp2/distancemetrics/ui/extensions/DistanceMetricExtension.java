package org.palladiosimulator.edp2.distancemetrics.ui.extensions;

import jakarta.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.DistanceMetric;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * Your class will be called by createExecutableExtension, once it has implemented this interface and is listed
 * by the extension point org.palladiosimulator.edp2.distancemetrics.algorithms
 * createExecutableExtension does not allow the use of any other constructor than the standard constructor.
 * 
 * @author Christian Schaf
 */
public interface DistanceMetricExtension {
	
	/**
	 * Returns the name of the distance metric.
	 * @return Returns the string representation of the name of the distance metric.
	 */
	public String getDistanceMetricName();
	/**
	 * Returns the description of the distance metric.
	 * @return Returns the string representation of the description of the distance metric.
	 */
	public String getDistanceMetricDescription();

	/**
	 * This class forwards calculation parameters and is responsible for individual UI
	 * error handling of the extension.
	 * 
	 * @param s1 First stream of measurement to compare.
	 * @param s2 Second stream of measurement to compare.
	 * @return Return a generic Amount which can contain any Quantity. Returns null, if calcuation couldn't be executed.
	 */
	public Amount<? extends Quantity> calculateDistance(IDataStream<TupleMeasurement> s1, IDataStream<TupleMeasurement> s2);
}
