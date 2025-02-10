package org.palladiosimulator.edp2.distancemetrics.r;

import java.util.Vector;

import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.apache.log4j.BasicConfigurator;
import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.DistanceMetric;
import org.palladiosimulator.edp2.distancemetrics.util.Utility;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

/**
 * Wrapper class for accessing R implementation of eucldidean distance implementation.
 * Please note that this implementation is only able to compare two measurements containing
 * the same number of elements.
 * 
 * @author Christian Schaf
 *
 */
public class EuclideanDistanceR implements DistanceMetric {
	

	@Override
	public Amount<?> calculateDistance(final IDataStream<TupleMeasurement> s1, 
	        final IDataStream<TupleMeasurement> s2) {
   	 	// Configures the log4j Logger, so no warning are given and no external file is needed.
		BasicConfigurator.configure();
		// Error handling
		RRegressionConnection rcon = DistanceMetricsConnectionImpl.getRRegressionConnection();
		if (rcon == null) {
			ErrorState.setState(ErrorState.CONNECTION_ERROR);
			return null;
		} else if (s1.size() != s2.size()) {
			ErrorState.setState(ErrorState.ELEMENTS_NUMBER_ERROR);
			return null;
		}
		
   	 	double[] s1Array = Utility.streamToDoubleArray(s1);
   	 	double[] s2Array = Utility.streamToDoubleArray(s2);
   	 	Unit<Quantity> baseValueUnit = Utility.getBaseValueUnit(s1);
        String xName = "x";
        String yName = "y";
        rcon.assign(xName, s1Array);
        rcon.assign(yName, s2Array);
        Vector<REXP> ed = rcon.execute("EuclideanDistance(" + xName + ", " + yName + ")");
        Amount<?> amnt = Amount.valueOf(0, baseValueUnit);
 		try {
			amnt = Amount.valueOf(ed.firstElement().asDouble(), baseValueUnit);
		} catch (REXPMismatchException e) {
			rcon.close();
			e.printStackTrace();
		}
 		rcon.close();
 		return amnt;
	}
}
