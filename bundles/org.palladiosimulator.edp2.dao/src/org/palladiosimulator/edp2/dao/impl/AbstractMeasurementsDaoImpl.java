/**
 * 
 */
package org.palladiosimulator.edp2.dao.impl;

import jakarta.measure.quantity.Quantity;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;

/**
 * Abstract base class for all DAO implementations which manage persisted measurements.
 * 
 * @author groenda
 *
 */
public abstract class AbstractMeasurementsDaoImpl<V, Q extends Quantity> extends Edp2DaoImpl implements
        MeasurementsDao<V, Q> {

    /*
     * (non-Javadoc)
     * 
     * @see org.palladiosimulator.edp2.MeasurementsDao#deserialize(org.eclipse.net4j.util.io.
     * ExtendedDataInputStream)
     */
    @Override
    public void deserialize(final ExtendedDataInputStream input) throws DataNotAccessibleException {
        if (isDeleted()) {
            throw new IllegalStateException("Deserialization not possible on deleted data.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.palladiosimulator.edp2.MeasurementsDao#serialize(org.eclipse.net4j.util.io.
     * ExtendedDataOutputStream)
     */
    @Override
    public void serialize(final ExtendedDataOutputStream output) throws DataNotAccessibleException {
        if (isDeleted()) {
            throw new IllegalStateException("Serialization not possible on deleted data.");
        }
    }
}
