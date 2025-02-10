/**
 * 
 */
package org.palladiosimulator.edp2.dao.jscience;

import java.util.List;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;

import org.palladiosimulator.edp2.dao.MeasurementsDao;

/**
 * Access for JScienceXmlMeasurements.
 * 
 * @author groenda
 *
 */
public interface JScienceXmlMeasurementsDao<V, Q extends Quantity> extends MeasurementsDao<V, Q> {

    @Override
    public List<Measure<V, Q>> getMeasurements();
}
