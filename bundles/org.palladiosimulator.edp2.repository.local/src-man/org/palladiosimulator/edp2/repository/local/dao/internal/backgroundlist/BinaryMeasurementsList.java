package org.palladiosimulator.edp2.repository.local.dao.internal.backgroundlist;

import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;

/**
 * Interface to abstract lists of measurements which are persisted in some kind of binary format.
 * 
 * @author groenda
 * @author S. Becker
 * @param <V>
 *            Value type of the measurements to be stored. Most often used values are Double or Long
 * @param <Q>
 *            Quantity to be stored, see {@link Quantity}
 */
public interface BinaryMeasurementsList<V, Q extends Quantity> extends List<Measure<V, Q>> {
}
