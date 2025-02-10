package org.palladiosimulator.edp2.dao.memory.impl;

import java.util.LinkedList;
import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.dao.impl.AbstractMeasurementsDaoImpl;

public class MemoryBinaryMeasurementsDaoImpl<V, Q extends Quantity> extends AbstractMeasurementsDaoImpl<V, Q> implements
        BinaryMeasurementsDao<V, Q> {

    private final List<Measure<V, Q>> list = new LinkedList<Measure<V, Q>>();
    private Unit<Q> unit;

    public MemoryBinaryMeasurementsDaoImpl(final Unit<Q> storageUnit) {
        super();
        this.unit = storageUnit;
    }

    @Override
    public List<Measure<V, Q>> getMeasurements() {
        return list;
    }

    @Override
    public void flush() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.palladiosimulator.edp2.dao.impl.Edp2DaoImpl#open()
     */
    @Override
    public synchronized void open() throws DataNotAccessibleException {
        super.open();
        setOpen();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.palladiosimulator.edp2.dao.impl.Edp2DaoImpl#close()
     */
    @Override
    public synchronized void close() throws DataNotAccessibleException {
        super.close();
        setClosed();
    }

    @Override
    public Unit<Q> getUnit() {
        return unit;
    }

    @Override
    public void setUnit(final Unit<Q> unit) {
        this.unit = unit;
    }

}
