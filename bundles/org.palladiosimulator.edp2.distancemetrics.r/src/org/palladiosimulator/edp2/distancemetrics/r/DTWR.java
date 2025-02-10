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
 * This is the R implementation of Dynamic Time Warping using the TSdist package.
 * 
 * @author Christian Schaf
 *
 */
public class DTWR implements DistanceMetric {

	@Override
	public Amount<?> calculateDistance(final IDataStream<TupleMeasurement> s1, final IDataStream<TupleMeasurement> s2) {
		BasicConfigurator.configure();
		RRegressionConnection rcon = DistanceMetricsConnectionImpl.getRRegressionConnection();
		if (rcon == null) {
			ErrorState.setState(ErrorState.CONNECTION_ERROR);
			return null;
		} 
   	 	double[] s1Array = Utility.streamToDoubleArray(s1);
   	 	double[] s2Array = Utility.streamToDoubleArray(s2);
   	 	Unit<Quantity> baseValueUnit = Utility.getBaseValueUnit(s1);
        String xName = "x";
        String yName = "y";
        rcon.assign(xName, s1Array);
        rcon.assign(yName, s2Array);
        Vector<REXP> dtw = rcon.execute("DTWDistance(" + xName + ", " + yName + ")");
        Amount<?> amnt = Amount.valueOf(0, baseValueUnit);
 		try {
			amnt = Amount.valueOf(dtw.firstElement().asDouble(), baseValueUnit);
		} catch (REXPMismatchException e) {
			rcon.close();
			e.printStackTrace();
		}
 		rcon.close();
 		return amnt;
	}

}
