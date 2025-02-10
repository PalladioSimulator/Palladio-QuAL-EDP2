/**
 * 
 */
package org.palladiosimulator.edp2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.unit.Unit;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.measurementframework.measure.IdentifierMeasure;
import org.palladiosimulator.metricspec.DataType;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.Scale;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;
import org.palladiosimulator.metricspec.util.builder.IdentifierBuilder;
import org.palladiosimulator.metricspec.util.builder.TextualBaseMetricDescriptionBuilder;

/**
 * JUnit test for classes with NominalMeasurementsDao interface. Subclass and test for all different
 * types of NominalMeasurementsDaos.
 * 
 * @author groenda
 */
public abstract class NominalMeasurementsDaoTest extends Edp2DaoTest {
    /** The ExperimentGroupDao under test. */
    @SuppressWarnings("unchecked")
    protected BinaryMeasurementsDao<Identifier, Dimensionless> onmDao = (BinaryMeasurementsDao<Identifier, Dimensionless>) dao;
    protected TextualBaseMetricDescription metric;
    protected Identifier identifier;

    @Before
    public void setup() {
        metric = TextualBaseMetricDescriptionBuilder.newTextualBaseMetricDescriptionBuilder().name("Test Enum")
                .textualDescription("Test Enum").scale(Scale.NOMINAL).dataType(DataType.QUALITATIVE).build();
        identifier = IdentifierBuilder.newIdentifierBuilder().literal("TEST ID").textualBaseMetricDescription(metric)
                .build();
        identifier.setTextualBaseMetricDescription(metric);
        metric.getIdentifiers().add(identifier);
    }

    /**
     * Test method for
     * {@link org.palladiosimulator.edp2.file.impl.FileExperimentGroupDaoImpl#getExperimentGroup()}.
     * 
     * @throws DataNotAccessibleException
     *             Error while accessing the file.
     */
    @Test
    public void testGetNominalMeasurements() throws DataNotAccessibleException {
        assertFalse("NominalMeasurementsDao must have initial state of not-open.", onmDao.isOpen());
        assertFalse("NominalMeasurementsDao must have initial state of not-deleted.", onmDao.isDeleted());

        onmDao.open();
        assertTrue("NominalMeasurementsDao must be open after open().", onmDao.isOpen());
        assertFalse("NominalMeasurementsDao.open() must not effect status of deletion.", onmDao.isDeleted());

        onmDao.close();
        assertFalse("NominalMeasurementsDao must be closed after close().", onmDao.isOpen());
        assertFalse("NominalMeasurementsDao.open() must not effect status of deletion.", onmDao.isDeleted());

        onmDao.delete();
        assertFalse("NominalMeasurementsDao.delete() must have no effect on status of open/closed.", onmDao.isOpen());
        assertTrue("NominalMeasurementsDao must be deleted adter delete().", onmDao.isDeleted());
    }

    /**
     * Abstract due to the fact that other EMF models are referenced and the test should be kept
     * simple.
     * 
     * @throws DataNotAccessibleException
     * @throws IOException
     */
    @Test
    public void testDataRetainedIfReopened() throws DataNotAccessibleException {
        onmDao.open();
        List<Measure<Identifier, Dimensionless>> bmd = onmDao.getMeasurements();

        final IdentifierMeasure<Dimensionless> testValue = IdentifierMeasure.valueOf(identifier, Unit.ONE);
        bmd.add(testValue);
        bmd = null;
        onmDao.close();
        onmDao.open();
        bmd = onmDao.getMeasurements();
        assertEquals("Test data must be retained if DAO is reopened.", identifier, bmd.get(0).getValue());
    }

}
