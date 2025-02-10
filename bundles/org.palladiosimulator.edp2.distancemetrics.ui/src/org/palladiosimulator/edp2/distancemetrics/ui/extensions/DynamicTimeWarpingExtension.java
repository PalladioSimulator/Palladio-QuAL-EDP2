package org.palladiosimulator.edp2.distancemetrics.ui.extensions;

import jakarta.measure.quantity.Quantity;

import org.eclipse.swt.widgets.Composite;
import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.DistanceMetric;
import org.palladiosimulator.edp2.distancemetrics.DynamicTimeWarping;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.DynamicTimeWarpingProperties;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.PropertiesWidget;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.PropertyAccess;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * Class which is loaded by the extension point org.palladiosimulator.edp2.distancemetrics.ui
 * at startup. It contains all information needed to make the DTW distance metric accessible.
 * 
 * @author Christian Schaf
 */
public class DynamicTimeWarpingExtension implements DistanceMetricExtension, PropertyAccess {
	private final DynamicTimeWarping dm = new DynamicTimeWarping();

	@Override
	public String getDistanceMetricName() {
		return "Dynamic Time Warping";
	}

	@Override
	public String getDistanceMetricDescription() {
		return "Dynamic Time Warping can be used by measurements with different"
				+ " sizes. If one measurement has more elements than the other, it tries to fit"
				+ " multiple elements to one element of the other measurement, that fits best or"
				+ " tries the next element in line, if this is a better fit."
				+ " This is useful, if missing measurements are considered to be equal or similar"
				+ " to the ones which are mapped to the same element.";
	}

	@Override
	public PropertiesWidget getPropertiesWidget(Composite parent, int style) {
		return new DynamicTimeWarpingProperties(parent, style, dm);
	}

	@Override
	public Amount<? extends Quantity> calculateDistance(IDataStream<TupleMeasurement> s1,
			IDataStream<TupleMeasurement> s2) {
		Amount<? extends Quantity> result = dm.calculateDistance(s1, s2);
		
		return result;
	}

	
}
