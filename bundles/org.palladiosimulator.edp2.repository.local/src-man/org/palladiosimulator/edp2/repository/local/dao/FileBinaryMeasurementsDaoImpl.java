/**
 * 
 */
package org.palladiosimulator.edp2.repository.local.dao;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.repository.local.dao.internal.backgroundlist.BackgroundMemoryList;
import org.palladiosimulator.edp2.repository.local.dao.internal.backgroundlist.BackgroundMemoryListImpl;
import org.palladiosimulator.edp2.repository.local.dao.internal.backgroundlist.BackgroundMemoryListImpl.BinaryRepresentation;
import org.palladiosimulator.edp2.repository.local.dao.internal.backgroundlist.serializer.Serializer;

/**
 * File-backed implementation of {@link BinaryMeasurementsDao}.
 * 
 * @author groenda
 * @author S. Becker
 */
public class FileBinaryMeasurementsDaoImpl<V, Q extends Quantity> extends FileAccessDao<V, Q> implements
        BinaryMeasurementsDao<V, Q> {
    /** Error logger for this class. */
    protected static final Logger LOGGER = Logger.getLogger(FileBinaryMeasurementsDaoImpl.class.getCanonicalName());

    /** Serializer to use for the background list. */
    private Serializer<V> serializer = null;
    /** Background memory list used to actually handle the list. */
    private BackgroundMemoryList<V, Q> backgroundMemoryList;
    /** Binary format of stored Measures. */
    private BinaryRepresentation binaryRepresentation = null;
    /** Unit in which all measurements are stored. */
    private Unit<Q> unit;

    @Override
    public void open() throws DataNotAccessibleException {
        super.open();
        if (unit == null) {
            final String msg = "A unit must be set before a call to open() is made.";
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg, null);
        }
        if (binaryRepresentation == null) {
            final String msg = "A binary representation must be set before a call to open() is made.";
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg, null);
        }
        if (serializer == null) {
            final String msg = "Initialization must have failed. Serializer is null.";
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg, null);
        }
        try {
            this.backgroundMemoryList = new BackgroundMemoryListImpl<V, Q>(resourceFile.getAbsolutePath(), serializer,
                    binaryRepresentation, unit);
            setOpen();
        } catch (final IOException ioe) {
            final String msg = "Error accessing file on background storage.";
            LOGGER.log(Level.SEVERE, msg, ioe);
            throw new DataNotAccessibleException(msg, ioe);
        }
    }

    @Override
    public void close() throws DataNotAccessibleException {
        super.close();
        try {
            backgroundMemoryList.close();
            setClosed();
        } catch (final IOException ioe) {
            final String msg = "Error accessing file on background storage.";
            LOGGER.log(Level.SEVERE, msg, ioe);
            throw new DataNotAccessibleException(msg, ioe);
        }
    }

    public Serializer<V> getSerializer() {
        return serializer;
    }

    public void setSerializer(final Serializer<V> serializer) {
        this.serializer = serializer;
    }

    @Override
    public List<Measure<V, Q>> getMeasurements() {
        if (!isOpen()) {
            final String msg = "Binary measurements can only be requested if state is open.";
            LOGGER.log(Level.WARNING, msg);
            throw new IllegalStateException(msg);
        }
        return backgroundMemoryList;
    }

    /**
     * @return the binaryRepresentation
     */
    public BinaryRepresentation getBinaryRepresentation() {
        return binaryRepresentation;
    }

    /**
     * @param binaryRepresentation
     *            the binaryRepresentation to set
     */
    public void setBinaryRepresentation(final BinaryRepresentation binaryRepresentation) {
        if (this.binaryRepresentation != null) {
            final String msg = "It is not allowed to set the binary representation more than once.";
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg);
        }
        this.binaryRepresentation = binaryRepresentation;
    }

    /**
     * @return the unit
     */
    @Override
    public Unit<Q> getUnit() {
        return unit;
    }

    /**
     * @param unit
     *            the unit to set
     */
    @Override
    public void setUnit(final Unit<Q> unit) {
        if (this.unit != null) {
            final String msg = "It is not allowed to set the unit more than once.";
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalStateException(msg);
        }
        this.unit = unit;
    }

    @Override
    public synchronized void serialize(final ExtendedDataOutputStream output) throws DataNotAccessibleException {
        try {
            super.serialize(output);
            ExtendedIOUtil.writeString(output, getUnit().toString());
        } catch (final IOException e) {
            final String msg = "Could not put unit name on stream.";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new DataNotAccessibleException(msg, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void deserialize(final ExtendedDataInputStream input) throws DataNotAccessibleException {
        try {
            super.deserialize(input);
            unit = (Unit<Q>) Unit.valueOf(ExtendedIOUtil.readString(input));
        } catch (final IOException e) {
            final String msg = "Could not put unit name on stream.";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new DataNotAccessibleException(msg, e);
        }
    }

    @Override
    public void flush() {
        if (isOpen()) {
            try {
                backgroundMemoryList.flush();
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Flush failed. Data may not be persited", e);
                throw new RuntimeException(e);
            }
        }
    }
}
