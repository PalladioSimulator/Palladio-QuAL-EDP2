package org.palladiosimulator.edp2.batchexport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Duration;

import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * Utility class for batch exporting EDP2 repositories.
 * 
 * @author stier
 *
 */
public class BatchExporter {

    private static final char COMMA = ',';
    private static final String NEW_LINE = "\n";

    /**
     * Exports an EDP2 repository to a target directory. Directory must be an absolute file system
     * path.
     * 
     * @param repo
     *            The exported repository.
     * @param targetDirectory
     *            The target directory.
     */
    public static void batchExport(final Repository repo, final String targetDirectory) {
        for (ExperimentGroup group : repo.getExperimentGroups()) {
            String groupDirectory = targetDirectory + File.separator
                    + removeIllegalChars(group.getPurpose() + " " + group.getId()) + File.separator;
            for (ExperimentSetting setting : group.getExperimentSettings()) {
                final String settingDirectory = groupDirectory
                        + removeIllegalChars(setting.getDescription() + setting.getId()) + File.separator;
                for (ExperimentRun run : setting.getExperimentRuns()) {
                    final String runDirectory = settingDirectory
                            + removeIllegalChars(run.getStartTime().toString() + " " + run.getId()) + File.separator;
                    for (Measurement measurement : run.getMeasurement()) {
                        measurementToCsv(runDirectory, measurement);
                    }
                }
            }
        }
    }

    /**
     * Writes a measurement to a CSV file.
     * 
     * @param directory
     *            Parent directory where file is created.
     * @param measurement
     *            The measurement written to CSV.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void measurementToCsv(final String directory, Measurement measurement) {
        final BaseMetricDescription[] mds = MetricDescriptionUtility
                .toBaseMetricDescriptions(measurement.getMeasuringType().getMetric());
        int timeIdx = 0;
        boolean timeFound = false;
        while (!timeFound && timeIdx < mds.length) {
            if (mds[timeIdx].getId().equals(MetricDescriptionConstants.POINT_IN_TIME_METRIC.getId())) {
                timeFound = true;
            } else {
                timeIdx++;
            }
        }
        BaseMetricDescription tmp = mds[mds.length - 1];
        mds[mds.length - 1] = MetricDescriptionConstants.POINT_IN_TIME_METRIC;
        mds[timeIdx] = tmp;
        List<DataSeries> dataSeriesList = measurement.getMeasurementRanges().get(0).getRawMeasurements()
                .getDataSeries();
        dataSeriesList = new ArrayList<DataSeries>(dataSeriesList);
        dataSeriesList.add(dataSeriesList.remove(timeIdx));
        try {
            File file = new File(directory
                    + removeIllegalChars(measurement.getMeasuringType().getMeasuringPoint().getStringRepresentation()
                            + " " + measurement.getMeasuringType().getMetric().getName())
                    + ".csv");
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            String cols = "";
            List<BaseMetricDescription> metricsList = Arrays.asList(mds);
            Iterator<BaseMetricDescription> metricsIt = metricsList.iterator();

            List<MeasurementsDao> daos = new ArrayList<MeasurementsDao>();
            for (DataSeries series : dataSeriesList) {
                daos.add(MeasurementsUtility.getMeasurementsDao(series));
            }

            for (MeasurementsDao<?, ?> curDao : daos) {
                BaseMetricDescription curMetricDesc = metricsIt.next();
                cols = curMetricDesc.getName() + "[" + ((BinaryMeasurementsDao<?, ?>) curDao).getUnit().toString() + "]"
                        + COMMA + cols;
            }
            cols = cols.substring(0, cols.length() - 1) + NEW_LINE;
            writer.append(cols);
            final long numberOfMsmnts = MeasurementsUtility.<Duration> getMeasurementsDao(dataSeriesList.get(0))
                    .getMeasurements().size();

            List<Iterator<Measure>> measureIts = new ArrayList<Iterator<Measure>>();
            for (MeasurementsDao dao : daos) {
                measureIts.add(dao.getMeasurements().iterator());
            }

            for (int pos = 0; pos < numberOfMsmnts; pos++) {
                String line = "";
                for (Iterator<Measure> measureIt : measureIts) {
                    Measure<?, ?> cur = measureIt.next();
                    line = cur.getValue().toString() + COMMA + line;
                }
                line = line.substring(0, line.length() - 1);
                writer.append(line + NEW_LINE);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Removes all illegal chars from String.
     * @param input The input string.
     * @return Cleaned up string.
     */
    private static String removeIllegalChars(String input) {
        return input.replaceAll("[^a-zA-Z0-9\\._]+", "_");
    }
}
