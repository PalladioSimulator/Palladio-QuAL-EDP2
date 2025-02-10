package org.palladiosimulator.edp2.distancemetrics.ui.extensions;

import jakarta.measure.quantity.Quantity;

import org.eclipse.swt.widgets.Composite;
import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.DistanceMetric;
import org.palladiosimulator.edp2.distancemetrics.EditDistanceOnRealSequence;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.EditDistanceOnRealSequenceProperties;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.PropertiesWidget;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.PropertyAccess;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * Class which is loaded by the extension point org.palladiosimulator.edp2.distancemetrics.ui
 * at startup. It contains all information needed to make the EDR distance metric accessible.
 * 
 * @author Christian Schaf
 */
public class EditDistanceOnRealSequenceExtension implements DistanceMetricExtension, PropertyAccess {

	private final EditDistanceOnRealSequence dm = new EditDistanceOnRealSequence();

	@Override
	public PropertiesWidget getPropertiesWidget(Composite parent, int style) {
		return new EditDistanceOnRealSequenceProperties(parent, style, dm);
	}

	@Override
	public String getDistanceMetricName() {
		return "Edit Distance on Real Sequence";
	}

	@Override
	public String getDistanceMetricDescription() {
		return " Edit Distance on Real Sequence uses dynamic programming"
				+ " to fit different sized measurements best. If elements are not"
				+ " being mapped to one element, there will be penalty costs of"
				+ "the number of elements that weren't mapped to one element.";
	}

	@Override
	public Amount<? extends Quantity> calculateDistance(IDataStream<TupleMeasurement> s1,
			IDataStream<TupleMeasurement> s2) {
		Amount<? extends Quantity> result = dm.calculateDistance(s1, s2);
		
		return result;
	}
}
