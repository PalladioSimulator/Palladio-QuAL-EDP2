package org.palladiosimulator.edp2.dao.memory.impl;

import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoRegistry;
import org.palladiosimulator.edp2.dao.impl.MeasurementsDaoFactoryImpl;
import org.palladiosimulator.edp2.dao.impl.MeasurementsDaoRegistryImpl;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;

public class MemoryMeasurementsDaoFactory extends MeasurementsDaoFactoryImpl {

    private final MeasurementsDaoRegistry registry = new MeasurementsDaoRegistryImpl();

    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Double, Q> createDoubleMeasurementsDao(final String uuid,
            final Unit<Q> storageUnit) {
        final MemoryBinaryMeasurementsDaoImpl<Double, Q> result = new MemoryBinaryMeasurementsDaoImpl<Double, Q>(
                storageUnit);
        registry.register(result, uuid);
        return result;
    }

    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Long, Q> createLongMeasurementsDao(final String uuid,
            final Unit<Q> storageUnit) {
        final MemoryBinaryMeasurementsDaoImpl<Long, Q> result = new MemoryBinaryMeasurementsDaoImpl<Long, Q>(
                storageUnit);
        registry.register(result, uuid);
        return result;
    }

    @Override
    public BinaryMeasurementsDao<Identifier, Dimensionless> createNominalMeasurementsDao(final String uuid,
            final TextualBaseMetricDescription metric, final Unit<Dimensionless> one) {
        final MemoryBinaryMeasurementsDaoImpl<Identifier, Dimensionless> result = new MemoryBinaryMeasurementsDaoImpl<Identifier, Dimensionless>(
                one);
        registry.register(result, uuid);
        return result;
    }

    @Override
    public MeasurementsDaoRegistry getDaoRegistry() {
        return registry;
    }

}
