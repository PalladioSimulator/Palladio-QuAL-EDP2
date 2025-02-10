package org.palladiosimulator.edp2.example;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Duration;
import jakarta.measure.unit.SI;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.edp2source.Edp2DataTupleDataSource;
import org.palladiosimulator.edp2.datastream.filter.AbstractFilter;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.models.Repository.LocalMemoryRepository;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.Repository.RepositoryFactory;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.measurementframework.measureprovider.IMeasureProvider;
import org.palladiosimulator.metricspec.MetricSetDescription;

/**
 * Contains an example how data can be stored with EDP2.
 *
 * @author groenda
 */
public class StoreLoadExample {

    /** (Relative) name of the directory in which the data of the example will be stored. */
    public static final String DEFAULT_DIRECTORY = "LocalRepository";

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(StoreLoadExample.class.getCanonicalName());

    /** Repository which is used to store the data. */
    private final Repository ldRepo;

    /** Helper class used to process data for the example. */
    private final ExampleData exampleData;

    /**
     * Initializes an instance of this class with the default directory as target.
     */
    public StoreLoadExample() {
        this(DEFAULT_DIRECTORY);
    }

    /**
     * Initializes an instance of this class.
     *
     * @param directory
     *            Directory to be used to store measurements.
     */
    public StoreLoadExample(final String directory) {
        super();
        initPathmaps();
        ldRepo = initializeRepository(directory);
        exampleData = new ExampleData(ldRepo.getDescriptions());
    }

    private void initPathmaps() {
        final String metricSpecModel = "commonMetrics.metricspec";
        final URL url = getClass().getClassLoader().getResource(metricSpecModel);
        if (url == null) {
            throw new RuntimeException("Error getting common metric definitions");
        }
        String urlString = url.toString();
        if (!urlString.endsWith(metricSpecModel)) {
            throw new RuntimeException("Error getting common metric definitions. Got: " + urlString);
        }
        urlString = urlString.substring(0, urlString.length() - metricSpecModel.length() - 1);
        final URI uri = URI.createURI(urlString);
        final URI target = uri.appendSegment("");
        URIConverter.URI_MAP.put(URI.createURI("pathmap://METRIC_SPEC_MODELS/"), target);

        final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        final Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("metricspec", new XMIResourceFactoryImpl());
    }

    /**
     * Initializes the repository in which the data will be stored.
     *
     * @param directory
     *            Path to directory in which the data should be stored.
     * @return the initialized repository.
     */
    private Repository initializeRepository(final String directory) {
        // final LocalDirectoryRepository repo =
        // RepositoryManager.initializeLocalDirectoryRepository(new File(directory));
        final LocalMemoryRepository repo = RepositoryFactory.eINSTANCE.createLocalMemoryRepository();
        /*
         * Add repository to a (optional) central directory of repositories. This can be useful to
         * manage more than one repository or have links between different existing repositories. A
         * repository must be connected to an instance of Repositories in order to be opened.
         */
        RepositoryManager.addRepository(RepositoryManager.getCentralRepository(), repo);
        return repo;
    }

    private void createExample() {
        exampleData.createExampleExperimentMetadata();
        ldRepo.getExperimentGroups().add(exampleData.getExampleExperimentGroup());
        // create experiment data
        exampleData.simulateExperimentRun();
    }

    /**
     * Method body which executes all necessary steps to create and store an example.
     */
    public void run() {
        try {
            final String storedData = storeExperimentRun();
            final String readData = loadExperimentRun(storedData);
            if (readData != null && !readData.equals(storedData)) {
                throw new IllegalStateException("Stored and loaded data is not equal. Stored: " + storedData
                        + "\nLoaded: " + readData);
            }
            streamResultData();
        } catch (final DataNotAccessibleException e) {
            LOGGER.log(Level.SEVERE, "Error while accessing the datastore.", e);
        }
    }

    /**
     * @throws DataNotAccessibleException
     */
    private void streamResultData() throws DataNotAccessibleException {
        MeasurementsUtility.ensureOpenRepository(ldRepo);
        final IDataSource dataSource = new Edp2DataTupleDataSource(ldRepo.getExperimentGroups().get(0)
                .getExperimentSettings().get(0).getExperimentRuns().get(0).getMeasurement().get(0)
                .getMeasurementRanges().get(0).getRawMeasurements());
        final AbstractFilter adapter = new AbstractFilter(dataSource, ldRepo.getExperimentGroups().get(0)
                .getExperimentSettings().get(0).getMeasuringTypes().get(0).getMetric()) {

            @Override
            protected MeasuringValue computeOutputFromInput(final MeasuringValue data) {
                final List<Measure<?, ?>> next = new ArrayList<Measure<?, ?>>(2);
                for (final Measure m : data.asList()) {
                    final Measure<Double, Duration> newM = Measure
                            .valueOf(m.doubleValue(SI.SECOND) + 1.0d, m.getUnit());
                    next.add(newM);
                }
                return new TupleMeasurement((MetricSetDescription) data.getMetricDesciption(), next);
            }

        };
        final IDataStream<MeasuringValue> dataStream = adapter.getDataStream();
        for (final IMeasureProvider tuple : dataStream) {
            System.out.println(tuple);
        }
        dataStream.close();
        MeasurementsUtility.ensureClosedRepository(ldRepo);
    }

    /**
     * @param storedData
     * @throws DataNotAccessibleException
     */
    private String loadExperimentRun(final String storedData) throws DataNotAccessibleException {
        // load
        MeasurementsUtility.ensureOpenRepository(ldRepo);
        final String readData = exampleData.printStoredMeasurements(ldRepo);
        System.out.println(readData);
        MeasurementsUtility.ensureClosedRepository(ldRepo);

        return readData;
    }

    /**
     * @return
     * @throws DataNotAccessibleException
     */
    private String storeExperimentRun() throws DataNotAccessibleException {
        MeasurementsUtility.ensureOpenRepository(ldRepo);
        createExample();
        final String storedData = exampleData.printStoredMeasurements(ldRepo);
        System.out.println(storedData);
        MeasurementsUtility.ensureClosedRepository(ldRepo);
        return storedData;
    }

    /**
     * Main method to run the example.
     *
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) {
        final StoreLoadExample example = new StoreLoadExample();
        example.run();
    }
}
