package org.palladiosimulator.edp2.distancemetrics.ui.extensions;

import jakarta.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.DistanceMetric;
import org.palladiosimulator.edp2.distancemetrics.EuclideanDistance;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * Class which is loaded by the extension point org.palladiosimulator.edp2.distancemetrics.ui
 * at startup. It contains all information needed to make the Euclidean Distance distance metric accessible.
 * 
 * @author Christian Schaf
 */
public class EuclideanDistanceExtension implements DistanceMetricExtension {
	private final EuclideanDistance dm = new EuclideanDistance();

	@Override
	public String getDistanceMetricName() {
		return "Euclidean Distance";
	}

	@Override
	public String getDistanceMetricDescription() {
		return "Euclidean Distance uses interpolation. This is helpful if the values of the measured subject "
				+ "change over time linearly but one measurement is not as detailed as the other one."
				+ "Missing data will be approximated by linear interpolation.";
	}

	@Override
	public Amount<? extends Quantity> calculateDistance(IDataStream<TupleMeasurement> s1,
			IDataStream<TupleMeasurement> s2) {
		Amount<? extends Quantity> result = dm.calculateDistance(s1, s2);
		
		return result;
	}
}
