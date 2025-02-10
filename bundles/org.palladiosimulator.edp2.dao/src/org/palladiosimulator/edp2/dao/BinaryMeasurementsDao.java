package org.palladiosimulator.edp2.dao;

import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

/**
 * Interface used to access a list of measurements of a certain type. An example is a list of double
 * values measured for a sensor in an experiment run.
 * 
 * @author groenda
 * @param <T>
 *            Data type of measurements.
 */
public interface BinaryMeasurementsDao<V, Q extends Quantity> extends MeasurementsDao<V, Q> {

    /**
     * Returns the unit in which all measurements are stored.
     * 
     * @return The unit.
     */
    public Unit<Q> getUnit();

    /**
     * Sets the unit in which all measurements are stored.
     * 
     * @param unit
     *            The unit.
     */
    public void setUnit(Unit<Q> unit);
}
