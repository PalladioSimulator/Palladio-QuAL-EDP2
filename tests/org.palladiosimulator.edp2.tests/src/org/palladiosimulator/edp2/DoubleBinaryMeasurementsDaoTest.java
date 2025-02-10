/**
 * 
 */
package org.palladiosimulator.edp2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Duration;
import jakarta.measure.unit.SI;
import jakarta.measure.unit.Unit;

import org.junit.Test;
import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;

/**
 * JUnit test for classes with DoubleBinaryMeasurementsDao interface. Subclass and test for all
 * different types of DoubleBinaryMeasurementsDao.
 * 
 * @author groenda
 */
@SuppressWarnings("unchecked")
public abstract class DoubleBinaryMeasurementsDaoTest extends Edp2DaoTest {
    /** Binary measurement DAO to test. */
    protected BinaryMeasurementsDao<Double, Duration> bmDao = (BinaryMeasurementsDao<Double, Duration>) dao;
    protected Unit<Duration> unit = SI.SECOND;

    @Test(expected = IllegalStateException.class)
    public void testGetBinaryMeasurmentsOnlyIfOpen() {
        bmDao.getMeasurements();
    }

    @Test
    public void testGetBinaryMeasurements() throws DataNotAccessibleException {
        assertFalse("BinaryMeasurementsDao must have initial state of not-open.", bmDao.isOpen());
        assertFalse("BinaryMeasurementsDao must have initial state of not-deleted.", bmDao.isDeleted());

        bmDao.open();
        assertTrue("BinaryMeasurementsDao must be open after open().", bmDao.isOpen());
        assertFalse("BinaryMeasurementsDao.open() must not effect status of deletion.", bmDao.isDeleted());
        assertNotNull("BinaryMeasurementsDao must be not null if open.", bmDao.getMeasurements());

        bmDao.close();
        assertFalse("BinaryMeasurementsDao must be closed after close().", bmDao.isOpen());
        assertFalse("BinaryMeasurementsDao.open() must not effect status of deletion.", bmDao.isDeleted());

        bmDao.delete();
        assertFalse("BinaryMeasurementsDao.delete() must have no effect on status of open/closed.", bmDao.isOpen());
        assertTrue("BinaryMeasurementsDao must be deleted adter delete().", bmDao.isDeleted());
    }

    @Test
    public void testDataRetainedIfReopened() throws DataNotAccessibleException {
        bmDao.open();
        List<Measure<Double, Duration>> bmd = bmDao.getMeasurements();
        double testValue = 5.0132;
        bmd.add(Measure.valueOf(testValue, unit));
        bmd = null;
        bmDao.close();
        bmDao.open();
        bmd = bmDao.getMeasurements();
        assertEquals("Test data must be retained if DAO is reopened.", testValue, bmd.get(0).doubleValue(unit), 0.1);
    }
}
