/**
 * 
 */
package org.palladiosimulator.edp2.repository.local.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.measure.quantity.Quantity;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.dao.impl.AbstractMeasurementsDaoImpl;

/**
 * Provides the basic functionality and protocol checking for DAOs where the persisted data is
 * managed in files on a local drive
 * 
 * @author groenda
 */
abstract class FileAccessDao<V, Q extends Quantity> extends AbstractMeasurementsDaoImpl<V, Q> {

    /** Error LOGGER for this class. */
    protected static final Logger LOGGER = Logger.getLogger(FileAccessDao.class.getCanonicalName());

    /** Denotes read-only file access. */
    private static final String FILE_ACCESS_READ_ONLY = "r";

    /** Denotes read and write file access. */
    private static final String FILE_ACCESS_READ_WRITE = "rw";

    /** Pointer to the file containing the resource. */
    protected File resourceFile = null;

    /**
     * Sets the resource file from which the ExperimentGroup data is loaded. Can only be set once.
     * 
     * @param resourceFile
     *            File in which the ExperimentGroup is stored.
     */
    public synchronized void setResourceFile(final File resourceFile) {
        if (this.resourceFile == null) {
            this.resourceFile = resourceFile;
        } else {
            LOGGER.log(Level.SEVERE, "Setting the file resource is only allowed if there is no resource loaded.");
            throw new IllegalArgumentException();
        }
    }

    @Override
    public synchronized void delete() throws DataNotAccessibleException {
        super.delete();
        if (!resourceFile.exists() || resourceFile.delete()) {
            setDeleted(true);
        } else {
            final String msg = "Could not delete file.";
            LOGGER.log(Level.WARNING, msg);
            throw new DataNotAccessibleException(msg, null);
        }
    }

    @Override
    public synchronized void deserialize(final ExtendedDataInputStream input) throws DataNotAccessibleException {
        super.deserialize(input);
        final boolean oldOpenState = isOpen();
        if (oldOpenState) {
            close();
        }
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(resourceFile, FILE_ACCESS_READ_WRITE);
        } catch (final FileNotFoundException e) {
            final String msg = "Serialization error: File " + resourceFile.getAbsolutePath()
                    + " on background storage could not be accessed.";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new DataNotAccessibleException(msg, e);
        }
        try {
            raf.seek(0);
            final byte[] b = ExtendedIOUtil.readByteArray(input);
            raf.write(b);
            raf.setLength(b.length);
            raf.close();
        } catch (final IOException ioe) {
            final String msg = "Serialization error: Could not read from file " + resourceFile.getAbsolutePath()
                    + " on background storage and store results in serialized stream.";
            LOGGER.log(Level.SEVERE, msg, ioe);
            throw new DataNotAccessibleException(msg, ioe);
        }
        if (oldOpenState) {
            open();
        }
    }

    @Override
    public synchronized void serialize(final ExtendedDataOutputStream output) throws DataNotAccessibleException {
        super.serialize(output);
        final boolean oldOpenState = isOpen();
        if (oldOpenState) {
            close();
        }
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(resourceFile, FILE_ACCESS_READ_ONLY);
        } catch (final FileNotFoundException e) {
            final String msg = "Serialization error: File " + resourceFile.getAbsolutePath()
                    + " on background storage could not be accessed.";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new DataNotAccessibleException(msg, e);
        }
        // TODO State that only files of size Integer.MAX_INT are supported (also by Serializer).
        final byte[] b = new byte[(int) resourceFile.length()];
        try {
            raf.seek(0);
            raf.read(b);
            ExtendedIOUtil.writeByteArray(output, b);
            raf.close();
        } catch (final IOException ioe) {
            final String msg = "Serialization error: Could not read from file " + resourceFile.getAbsolutePath()
                    + " on background storage and store results in serialized stream.";
            LOGGER.log(Level.SEVERE, msg, ioe);
            throw new DataNotAccessibleException(msg, ioe);
        }
        if (oldOpenState) {
            open();
        }
    }
}
