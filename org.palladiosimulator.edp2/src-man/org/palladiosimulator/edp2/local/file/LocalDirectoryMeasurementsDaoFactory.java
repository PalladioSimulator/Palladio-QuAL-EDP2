package org.palladiosimulator.edp2.local.file;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.edp2.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.MeasurementsDaoRegistry;
import org.palladiosimulator.edp2.NominalMeasurementsDao;
import org.palladiosimulator.edp2.impl.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.impl.DataNotAccessibleException;
import org.palladiosimulator.edp2.impl.JScienceXmlMeasurementsDao;
import org.palladiosimulator.edp2.impl.MeasurementsDaoRegistryImpl;
import org.palladiosimulator.edp2.local.file.BackgroundMemoryListImpl.BinaryRepresentation;
import org.palladiosimulator.edp2.models.impl.EmfModelXMIResourceFactoryImpl;

/**This {@link MeasurementsDaoFactory} implementation stores data in file on background storage.
 * 
 * @author groenda
 */
public class LocalDirectoryMeasurementsDaoFactory extends org.palladiosimulator.edp2.impl.MeasurementsDaoFactory {
    /** Logger for this class. */
    private static final Logger logger = Logger
            .getLogger(LocalDirectoryMeasurementsDaoFactory.class.getCanonicalName());

    /** File suffix for uuid-referenced data files. */
    private static final String FILE_SUFFIX = "edp2bin";

    /**
     * Resource set for all handled EMF models. Must be the same to allow
     * correct linking.
     */
    private static final ResourceSet emfResourceSet = new ResourceSetImpl();
    /** Determines where newly created DAOs are registered. */
    private final MeasurementsDaoRegistry daoRegistry;

    /** Map containing the existing FileDaoFactories. */
    private static ConcurrentMap<String, MeasurementsDaoFactory> existingFileDaoFactories = new ConcurrentHashMap<String, MeasurementsDaoFactory>();

    /** Directory which is handled by this instance of the file dao factory. */
    private File storageDirectory = null;

    /**
     * Creates a new instance of a DaoFactory for files.
     * 
     * @param storageDirectory
     *            The directory for which this instance is responsible.
     */
    public LocalDirectoryMeasurementsDaoFactory(final File storageDirectory) {
        if (existingFileDaoFactories
                .containsKey(fileToMapKey(storageDirectory))) {
            logger.log(Level.SEVERE,
                    "There is already an existing FileDaoFactory instance for "
                            + fileToMapKey(storageDirectory) + ".");
            throw new IllegalArgumentException();
        } else {
            existingFileDaoFactories.put(fileToMapKey(storageDirectory), this);
        }
        this.storageDirectory = storageDirectory;
        this.daoRegistry = new MeasurementsDaoRegistryImpl();
    }

    /**
     * Converts a file/directory name to a valid key of the
     * existingFileDaoFactories map.
     * 
     * @param directory
     *            The file to convert.
     * @return The key for the map.
     */
    private static String fileToMapKey(final File directory) {
        String result = null;
        try {
            result = directory.getCanonicalPath();
        } catch (final IOException e) {
            logger.log(Level.WARNING, "Could not resolve file name to String.",
                    e);
        }
        return result;
    }

    /**
     * returns the absolute path to a uuid-referenced data file.
     * 
     * @param uuid
     *            Identifier of the data (file).
     * @return aboslute path to the file.
     */
    private String getAbsolutePathToUuidFile(final String uuid, final String suffix) {
        return new File(storageDirectory.getAbsolutePath() + File.separator
                + uuid).getAbsolutePath()
                + "." + suffix;
    }

    @Override
    public JScienceXmlMeasurementsDao<?,Quantity> createJScienceXmlMeasurementsDao(final String uuid) {
        super.createJScienceXmlMeasurementsDao(uuid);

        // TODO Implement JScienceXmlMeasurements
        logger.log(Level.SEVERE,"Unsupported Operation: JScience Measurements.");
        throw new UnsupportedOperationException();
    }

    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Double,Q> createDoubleMeasurementsDao(final String uuid) {
        super.createDoubleMeasurementsDao(uuid);
        final FileBinaryMeasurementsDaoImpl<Double,Q> fbmDao = new FileBinaryMeasurementsDaoImpl<Double,Q>();
        fbmDao.setBinaryRepresentation(BinaryRepresentation.DOUBLE);
        fbmDao.setResourceFile(new File(getAbsolutePathToUuidFile(uuid,
                FILE_SUFFIX)));
        fbmDao.setSerializer(new DoubleSerializer());
        daoRegistry.register(fbmDao, uuid);
        return fbmDao;
    }

    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Long,Q> createLongMeasurementsDao(final String uuid) {
        super.createLongMeasurementsDao(uuid);
        final FileBinaryMeasurementsDaoImpl<Long,Q> fbmDao = new FileBinaryMeasurementsDaoImpl<Long,Q>();
        fbmDao.setBinaryRepresentation(BinaryRepresentation.LONG);
        fbmDao.setResourceFile(new File(getAbsolutePathToUuidFile(uuid,
                FILE_SUFFIX)));
        fbmDao.setSerializer(new LongSerializer());
        daoRegistry.register(fbmDao, uuid);
        return fbmDao;
    }

    @Override
    public NominalMeasurementsDao createNominalMeasurementsDao(final String uuid) {
        super.createNominalMeasurementsDao(uuid);
        final FileNominalMeasurementsDaoImpl fnmDao = new FileNominalMeasurementsDaoImpl();
        fnmDao.setResourceFile(new File(getAbsolutePathToUuidFile(uuid,
                EmfModelXMIResourceFactoryImpl.EDP2_NOMINALMEASUREMENTS_EXTENSION)));
        fnmDao.setResourceSet(emfResourceSet);
        daoRegistry.register(fnmDao, uuid);
        return fnmDao;
    }

    @Override
    public MeasurementsDaoRegistry getDaoRegistry() {
        return daoRegistry;
    }

    @Override
    public void setActive(final boolean newValue) {
        super.setActive(newValue);
        final Set<String> registeredUuids = daoRegistry.getRegisteredUuids();
        for (final String uuid : registeredUuids) {
            try {
                if (!isActive()) {
                    if (!daoRegistry.getMeasurementsDao(uuid).isDeleted()) {
                        if (daoRegistry.getMeasurementsDao(uuid).isOpen()) {
                            daoRegistry.getMeasurementsDao(uuid).close();
                        }
                    }
                    daoRegistry.deregister(uuid);
                } else {
                    daoRegistry.getMeasurementsDao(uuid).open();
                }
            } catch (final DataNotAccessibleException e) {
                logger.log(Level.SEVERE, "Could not close DAO.", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**Returns a registered factory for a given location.
     * @param directory Local directory for which the factory is requested.
     * @return <code>null</code> if there is no factory registered. The registered factory otherwise.
     */
    public static MeasurementsDaoFactory getRegisteredFactory(final File directory) {
        return existingFileDaoFactories.get(fileToMapKey(directory));
    }

    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Double,Q> createDoubleMeasurementsDao(
            final String uuid, final Unit<Q> storageUnit) {
        final BinaryMeasurementsDao<Double,Q> bmd = createDoubleMeasurementsDao(uuid);
        bmd.setUnit(storageUnit);
        return bmd;
    }

    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Long,Q> createLongMeasurementsDao(
            final String uuid, final Unit<Q> storageUnit) {
        final BinaryMeasurementsDao<Long,Q> bmd = createLongMeasurementsDao(uuid);
        bmd.setUnit(storageUnit);
        return bmd;
    }

}