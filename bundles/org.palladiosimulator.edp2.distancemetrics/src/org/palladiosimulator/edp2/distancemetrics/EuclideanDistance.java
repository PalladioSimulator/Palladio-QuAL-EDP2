package org.palladiosimulator.edp2.distancemetrics;

import java.util.Iterator;

import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.util.TupleWrapper;
import org.palladiosimulator.edp2.distancemetrics.util.Utility;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * This algorithm calculates the distance with the Euclidean Distance and
 * interpolates the values if the time values of the two current measurement points
 * doesn't match.
 * 
 * @author Christian Schaf
 */
public class EuclideanDistance implements DistanceMetric {
    
	/**
	 * Inner class represents state of all required variables.
	 * @author Christian Schaf
	 *
	 */
	private static class DistanceState {
		private static Iterator<TupleMeasurement> it1;
	    private static Iterator<TupleMeasurement> it2;
		private static Unit<Quantity> valueBaseUnit;
		private static Unit<Quantity> timeBaseUnit;
		private static double sumEucDist;
		private static boolean finished;
		private static TupleWrapper c1;
		private static TupleWrapper c2;
	    private static long numOfElements1;
	    private static long numOfElements2;
	}

	@Override
	public Amount<?> calculateDistance(IDataStream<TupleMeasurement> s1, IDataStream<TupleMeasurement> s2) {
		Utility.legalStreamComparison(s1, s2);
		
		// TODO replace with distance state constructor call
		init(s1, s2);
        
        if (DistanceState.numOfElements1 == 0 || DistanceState.numOfElements2 == 0) {
        	DistanceState.finished = true;
        }
        
        while (!DistanceState.finished) {
        	
        	if (Utility.compareEquals(DistanceState.c1.getTime(DistanceState.timeBaseUnit),
        			DistanceState.c2.getTime(DistanceState.timeBaseUnit))) {
        		DistanceState.sumEucDist += Utility.euclid(DistanceState.c1.getValue(DistanceState.valueBaseUnit),
        				DistanceState.c2.getValue(DistanceState.valueBaseUnit));
        		skipWhenEqual();
        	} else if (DistanceState.c1.getTime(DistanceState.timeBaseUnit)
        			< DistanceState.c2.getTime(DistanceState.timeBaseUnit)) {
        		// Skip elements of measurement m1, until bigger time value is found than the current element of m2
        		TupleWrapper temp = skipStream1();
        		if (!DistanceState.finished) {
        			if (Utility.compareEquals(DistanceState.c1.getTime(DistanceState.timeBaseUnit),
        					DistanceState.c2.getTime(DistanceState.timeBaseUnit))) {
        				DistanceState.sumEucDist += Utility.euclid(DistanceState.c1.getValue(DistanceState.valueBaseUnit),
        						DistanceState.c2.getValue(DistanceState.valueBaseUnit));
        				skipWhenEqual();
        			} else {
        				double interpolatedValue = interpolation(temp, DistanceState.c1, DistanceState.c2);
        				DistanceState.sumEucDist +=
        						Utility.euclid(interpolatedValue, DistanceState.c2.getValue(DistanceState.valueBaseUnit));
        				DistanceState.c2 = getNext(DistanceState.it2);
        			}
        		}
        	} else if (DistanceState.c2.getTime(DistanceState.timeBaseUnit)
        			< DistanceState.c1.getTime(DistanceState.timeBaseUnit)) {
        		// Skip elements of measurement m2, until bigger time value is found than the current element of m1
        		TupleWrapper temp = skipStream2();
        		if (!DistanceState.finished) {
        			if (Utility.compareEquals(DistanceState.c1.getTime(DistanceState.timeBaseUnit),
        					DistanceState.c2.getTime(DistanceState.timeBaseUnit))) {
        				DistanceState.sumEucDist += Utility.euclid(DistanceState.c1.getValue(DistanceState.valueBaseUnit),
        						DistanceState.c2.getValue(DistanceState.valueBaseUnit));
        				skipWhenEqual();
        			} else {
        				double interpolatedValue = interpolation(temp, DistanceState.c2, DistanceState.c1);
        				DistanceState.sumEucDist +=
        						Utility.euclid(interpolatedValue, DistanceState.c1.getValue(DistanceState.valueBaseUnit));
        				DistanceState.c1 = getNext(DistanceState.it1);
        			}
        		}
        	}
        }
        return Amount.valueOf(DistanceState.sumEucDist, DistanceState.valueBaseUnit);     
	}
	
	/**
	 * Skips both streams if current elements have identical time value and
	 * declares calculations as finished, if one stream has no elements anymore.
	 */
	private void skipWhenEqual() {
    	// Get next elements of m1 and m2
    	if (DistanceState.it1.hasNext()) {
    		DistanceState.c1 = new TupleWrapper(DistanceState.it1.next());
    	} else {
    		DistanceState.finished = true;
    	}
    	if (DistanceState.it2.hasNext()) {
    		DistanceState.c2 = new TupleWrapper(DistanceState.it2.next());
    	} else {
    		DistanceState.finished = true;
    	}
	}
	
	/**
	 * Skips elements of first stream, until time value of current element
	 * of stream 1 is bigger or equal than the current element of stream 2
	 * or end of stream 1 is reached.
	 * @return Returns null, if calculations are already finished. Otherwise
	 * returns next element. If end of stream 1 is reached, current element will
	 * be returned.
	 */
	private TupleWrapper skipStream1() {
		TupleWrapper temp = null;
		while (DistanceState.c1.getTime(DistanceState.timeBaseUnit)
				< DistanceState.c2.getTime(DistanceState.timeBaseUnit) && !DistanceState.finished) {
			temp = DistanceState.c1;
			if (DistanceState.it1.hasNext()) {
				DistanceState.c1 = new TupleWrapper(DistanceState.it1.next());
			} else {
				DistanceState.finished = true;
			}
		}
		return temp;
	}
	
	/**
	 * Skips elements of first stream, until time value of current element
	 * of stream 2 is bigger or equal than the current element of stream 1
	 * or end of stream 2 is reached.
	 * @return Returns null, if calculations are already finished. Otherwise
	 * returns next element. If end of stream 2 is reached, current element will
	 * be returned.
	 */
	private TupleWrapper skipStream2() {
		TupleWrapper temp = null;
		while (DistanceState.c2.getTime(DistanceState.timeBaseUnit)
				< DistanceState.c1.getTime(DistanceState.timeBaseUnit) && !DistanceState.finished) {
			temp = DistanceState.c2;
			if (DistanceState.it2.hasNext()) {
				DistanceState.c2 = new TupleWrapper(DistanceState.it2.next());
			} else {
				DistanceState.finished = true;
			}
		}
		return temp;
	}
	
	/**
	 * Does interpolation between a1 and a2 (measured value) at time position given by b.
	 * @param a1 First element to be interpolated.
	 * @param a2 Second element to be interpolated.
	 * @param b Contains time position of interpolation.
	 * @return Returns value of interpolation.
	 */
	private double interpolation(TupleWrapper a1, TupleWrapper a2, TupleWrapper b) {
		// Calculate interpolation value between two points of m2 at the time of the current point in m1
		// x[] contains times, y[] contains measured values
		double[] x = {a1.getTime(DistanceState.timeBaseUnit), a2.getTime(DistanceState.timeBaseUnit)};
		double[] y = {a1.getValue(DistanceState.valueBaseUnit), a2.getValue(DistanceState.valueBaseUnit)};
		return Utility.linearInterpolation(x, y, b.getTime(DistanceState.timeBaseUnit));
	}
	
	/**
	 * 
	 * @param it Iterator to take the next element from.
	 * @return Null, if Iterator is already at last position of stream.
	 * Otherwise returns next element of stream.
	 */
	private TupleWrapper getNext(Iterator<TupleMeasurement> it) {
		// Get next element of stream
		TupleWrapper ret = null;
    	if (it.hasNext()) {
    		ret = new TupleWrapper(it.next());
    	} else {
    		DistanceState.finished = true;
    	}
    	return ret;
	}
	
	/**
	 * Initialization of variables.
	 * @param s1 First stream for calculation.
	 * @param s2 Second stream for calculation.
	 */
	private void init(IDataStream<TupleMeasurement> s1, IDataStream<TupleMeasurement> s2) {
		// Initialization
		DistanceState.numOfElements1 = s1.size();
		DistanceState.numOfElements2 = s2.size();
		DistanceState.it1 = s1.iterator();
		DistanceState.it2 = s2.iterator();
		DistanceState.valueBaseUnit = Utility.getBaseValueUnit(s1);
		DistanceState.timeBaseUnit = Utility.getBaseTimeUnit(s1);
		DistanceState.c1 = new TupleWrapper(DistanceState.it1.next());
		DistanceState.c2 = new TupleWrapper(DistanceState.it2.next());
		DistanceState.sumEucDist = 0.0;
		DistanceState.finished = false;
	}
}
