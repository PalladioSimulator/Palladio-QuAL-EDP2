package org.palladiosimulator.edp2.repository.local.dao.internal.backgroundlist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.AbstractList;
import java.util.logging.Logger;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.repository.local.dao.internal.backgroundlist.serializer.Serializer;
import org.palladiosimulator.measurementframework.measure.IdentifierMeasure;
import org.palladiosimulator.metricspec.Identifier;

/**
 * A generic list implementation that consumes a constant amount of main memory regardless of the
 * number of elements in the list. It relies on HDD memory as background storage. It uses views on
 * the data stored in form of chunks. A chunk contains a limited number of data elements. Chunks are
 * (de-)serialized to disk depending on the requested data elements.
 * 
 * The list has some restrictions: First, all elements have to be serializable with constant memory
 * footprint. Second, deletion of elements in the list is not implemented. Third, insertion is only
 * possible at the end of the list. Fourth, all list elements are persisted with the same unit, so
 * unit conversion and conversion losses may apply.
 * 
 * @param <V>
 *            Value type of the measurements to be stored. Most often used values are Double or Long
 * @param <Q>
 *            Quantity to be stored, see {@link Quantity}
 * 
 * @author Henning Groenda
 * @author Steffen Becker
 */
public class BackgroundMemoryListImpl<V, Q extends Quantity> extends AbstractList<Measure<V, Q>> implements
        BackgroundMemoryList<V, Q> {

    private static final String ACCESS_MODIFIER_READ_WRITE = "rw";

    /** Logger for this class. */
    transient private static final Logger LOGGER = Logger.getLogger(BackgroundMemoryListImpl.class.getName());

    /** Default Number of data elements per chunk. */
    static final public int DEFAULT_CHUNK_SIZE = 10000;

    /**
     * Describes the representation and storage of the values in binary format.
     * 
     * @author groenda
     */
    public enum BinaryRepresentation {
        /** Binary representation of type long. */
        LONG,
        /** Binary representation of type double. */
        DOUBLE, IDENTIFIER
    };

    /** Access to chunks. */
    transient private ChunkedFile<V> chunks = null;

    /** Name of the file which is used to store the chunks. Absolute path to the file. */
    private String absoluteFilename = null;

    /** The link to the attached background storage in which all chunks are persisted. */
    transient private RandomAccessFile raf = null;

    /**
     * Status of the link to the attached file on background storage. Operations are only allowed in
     * open state.
     */
    transient boolean closed = true;

    /** The serializer for the elements of the list on the background storage. */
    private Serializer<V> serialiser;

    /** Total number of elements in the list. */
    private int listSize;

    /** The size of the chunk in elements */
    private int chunkSize;

    /** Binary format of values stored in this list. */

    private BinaryRepresentation binaryRepresentation;

    /** Unit of the measurements stored in this list. */
    private Unit<Q> unit;

    /**
     * Constructor for background memory lists.
     * 
     * @param absoluteFilename
     *            Path to the file used as background storage.
     * @param serialiser
     *            The (de-)serializer to use upon serialization of data to the background storage.
     * @throws IOException
     *             Thrown if file IO fails.
     */
    public BackgroundMemoryListImpl(final String absoluteFilename, final Serializer<V> serialiser,
            final BinaryRepresentation binaryRepresentation, final Unit<Q> unit) throws IOException {
        this(absoluteFilename, serialiser, DEFAULT_CHUNK_SIZE, binaryRepresentation, unit);
    }

    /**
     * Constructor for background memory lists.
     * 
     * @param absoluteFilename
     *            Path to the file used as background storage.
     * @param serialiser
     *            The (de-)serializer to use upon serialization of data to the background storage.
     * @param chunkSize
     *            The size of the chunk in elements which should be used.
     * @throws IOException
     *             Thrown if file IO fails.
     */
    public BackgroundMemoryListImpl(final String absoluteFilename, final Serializer<V> serialiser, final int chunkSize,
            final BinaryRepresentation binaryRepresentation, final Unit<Q> unit) throws IOException {
        this.absoluteFilename = absoluteFilename;
        this.serialiser = serialiser;
        this.chunkSize = chunkSize;
        this.binaryRepresentation = binaryRepresentation;
        this.unit = unit;
        open();
        listSize = (int) chunks.getElementsInFile();
    }

    @Override
    public synchronized void open() throws IOException {
        if (!closed) {
            final String msg = "Tried to open an already open background list.";
            LOGGER.warning(msg);
            throw new IllegalStateException(msg);
        }
        raf = new RandomAccessFile(absoluteFilename, ACCESS_MODIFIER_READ_WRITE);
        chunks = new ChunkedFile<V>(raf, serialiser, chunkSize);
        closed = false;
        // list size is calculated in open() and readObject() to allow error detection.
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractList#add(int, java.lang.Object)
     */
    @Override
    public synchronized void add(final int index, final Measure<V, Q> element) {
        if (closed) {
            final String msg = "Tried to add data to a closed background list.";
            LOGGER.severe(msg);
            throw new IllegalStateException(msg);
        }
        if (index != listSize) {
            final String msg = "The background list currently only supports adding elements at the end of the list";
            LOGGER.info(msg);
            throw new IllegalArgumentException(msg);
        }
        if (element == null) {
            final String msg = "Null values must not be added to the list.";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            ensureCorrectChunkLoaded(index);
            chunks.add(element.getValue());
            this.listSize++;
            if (chunks.isFull()) {
                chunks.saveChunk();
                chunks.createChunk();
            }
        } catch (final IOException ex) {
            final String msg = "Error during IO of background list for file \"" + absoluteFilename + "\"";
            LOGGER.severe(msg);
            throw new RuntimeException(msg, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractList#get(int)
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized Measure<V, Q> get(final int index) {
        if (closed) {
            final String msg = "Tried to get data from a closed background list.";
            LOGGER.severe(msg);
            throw new IllegalStateException(msg);
        }
        if (index < 0 || index >= size()) {
            String msg;
            if (index >= size()) {
                msg = "Tried to get data element beyond the number of elements in the background list.";
            } else {
                msg = "Tried to get data element with a negative index from the background list.";
            }
            LOGGER.severe(msg);
            throw new ArrayIndexOutOfBoundsException(msg);
        }
        try {
            ensureCorrectChunkLoaded(index);
            // Measure measure;
            if (binaryRepresentation == BinaryRepresentation.LONG) {
                return (Measure<V, Q>) Measure.valueOf(
                        (Long) chunks.get((int) (index - chunks.indexStartingElementForChunk())), unit);
            }
            if (binaryRepresentation == BinaryRepresentation.DOUBLE) {
                return (Measure<V, Q>) Measure.valueOf(
                        (Double) chunks.get((int) (index - chunks.indexStartingElementForChunk())), unit);
            }
            if (binaryRepresentation == BinaryRepresentation.IDENTIFIER) {
                return new IdentifierMeasure((Identifier) chunks.get((int) (index - chunks
                        .indexStartingElementForChunk())), unit);
            }
            throw new UnsupportedOperationException("Unsupported Binary Representation found");
        } catch (final IOException ex) {
            final String msg = "Error during IO of background list for file \"" + absoluteFilename + "\"";
            LOGGER.severe(msg);
            throw new RuntimeException(msg, ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Measure<V, Q> set(final int index, final Measure<V, Q> element) {
        if (closed) {
            final String msg = "Tried to get data to a closed background list.";
            LOGGER.severe(msg);
            throw new IllegalStateException(msg);
        }
        if (index < 0 || index >= size()) {
            String msg;
            if (index >= size()) {
                msg = "Tried to set data element beyond the number of elements in the background list.";
            } else {
                msg = "Tried to set data element with a negative index from the background list.";
            }
            LOGGER.severe(msg);
            throw new ArrayIndexOutOfBoundsException(msg);
        }
        try {
            ensureCorrectChunkLoaded(index);
            final V newValue = element.getValue();
            final V oldValue = chunks.set((int) (index - chunks.indexStartingElementForChunk()), newValue);
            if (binaryRepresentation == BinaryRepresentation.LONG) {
                return (Measure<V, Q>) Measure.valueOf((Long) oldValue, unit);
            }
            if (binaryRepresentation == BinaryRepresentation.DOUBLE) {
                return (Measure<V, Q>) Measure.valueOf((Double) oldValue, unit);
            }
            if (binaryRepresentation == BinaryRepresentation.IDENTIFIER) {
                return new IdentifierMeasure((Identifier) oldValue, unit);
            }
            throw new UnsupportedOperationException("Unsupported Binary Representation found");
        } catch (final IOException ex) {
            final String msg = "Error during IO of background list for file \"" + absoluteFilename + "\"";
            LOGGER.severe(msg);
            throw new RuntimeException(msg, ex);
        }
    };

    @Override
    public Measure<V, Q> remove(final int index) {
        // TODO Add support for removal of elements from background memory list.
        throw new UnsupportedOperationException();
    }

    /**
     * Ensures that the chunk containing the requested element is loaded. Serializes the current
     * chunk and load the requested chunk if necessary.
     * 
     * @param dataIndex
     *            index of the requested element in the list.
     * @throws IOException
     *             Thrown if an error during IO occurs.
     */
    private void ensureCorrectChunkLoaded(final int dataIndex) throws IOException {
        if (chunks.acceptsDataElementIndex(dataIndex)) {
            return;
        } else {
            if (chunks.isChunkLoaded() && chunks.isChanged()) {
                chunks.saveChunk();
            }
            chunks.loadChunkForElement(dataIndex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public synchronized int size() {
        return listSize;
    }

    @Override
    public synchronized void close() throws IOException {
        if (closed) {
            final String msg = "Tried to close a closed list.";
            LOGGER.warning(msg);
            throw new IllegalStateException(msg);
        }
        if (!closed) {
            if (chunks.isChunkLoaded() && chunks.isChanged()) {
                chunks.saveChunk();
            }
            raf.close();
            closed = true;
        }
    }

    /**
     * Used for serialization.
     * 
     * @param in
     *            stream to deserialize.
     * @throws IOException
     *             Error when accessing the stream.
     * @throws ClassNotFoundException
     *             Conversion errors.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        // default behavior
        in.defaultReadObject();

        // customization on load
        open();
        if (chunks.getElementsInFile() != listSize) {
            final String msg = "Number of elements of this list and elements saved to file on background mismatch.";
            LOGGER.severe(msg);
        }
        listSize = (int) chunks.getElementsInFile();
    }

    @Override
    public void flush() throws IOException {
        if (chunks.isChunkLoaded() && chunks.isChanged()) {
            chunks.saveChunk();
        }
    }
}
