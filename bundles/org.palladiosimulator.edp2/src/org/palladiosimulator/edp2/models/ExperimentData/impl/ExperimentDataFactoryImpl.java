package org.palladiosimulator.edp2.models.ExperimentData.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.MeasureFormat;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.emf.ecore.EDataType;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 */
public class ExperimentDataFactoryImpl extends ExperimentDataFactoryImplGen {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ExperimentDataFactoryImpl.class.getCanonicalName());

    @Override
    public javax.measure.Measure<?, ?> createEJSMeasureFromString(final EDataType eDataType, final String initialValue) {
        try {
            return (javax.measure.Measure<?, ?>) MeasureFormat.getInstance(NumberFormat.getInstance(Locale.ENGLISH),
                    UnitFormat.getInstance()).parseObject(initialValue);
        } catch (final ParseException e) {
            LOGGER.log(Level.SEVERE,
                    "Could not restore Measure object. " + "Object: " + initialValue + "Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String convertEJSMeasureToString(final EDataType eDataType, final Object instanceValue) {
        String result = "";
        if (instanceValue != null) {
            final MeasureFormat formatter = MeasureFormat.getInstance(NumberFormat.getInstance(Locale.ENGLISH),
                    UnitFormat.getInstance());
            result = formatter.format(instanceValue);
        }
        return result;
    }

    private javax.measure.Measure readEJSMeasureObject(final String initialValue) {
        if (initialValue == null || initialValue.isEmpty()) {
            return null;
        }
        final String[] items = initialValue.split(" ");
        Double value = 0.0d;
        Unit<? extends Quantity> unit = null;
        try {
            value = Double.valueOf(items[0]);

            // in case the measure has no dimension
            if (items.length < 2) {
                return javax.measure.Measure.valueOf(value, Dimensionless.UNIT);
            }

            final UnitFormat formatter = UnitFormat.getInstance();
            unit = formatter.parseObject(items[1], new ParsePosition(0));
        } catch (final NumberFormatException nfe) {
            LOGGER.log(Level.SEVERE, "Could not restore Measure object. It has to start with a number." + "Object: "
                    + initialValue + "Error: " + nfe.getMessage());
            return null;
        } catch (final IllegalArgumentException iae) {
            LOGGER.log(Level.SEVERE,
                    "Could not restore Measure object. It has wrong dimension. It receives no dimension." + "Object: "
                            + initialValue + "Error: " + iae.getMessage());
        }
        if (unit == null) {
            unit = Dimensionless.UNIT;
        }
        return javax.measure.Measure.valueOf(value, unit);
    }

    @Override
    public javax.measure.Measure createEJSDurationMeasureFromString(final EDataType eDataType, final String initialValue) {
        return (javax.measure.Measure) this.readObject(initialValue);
    }

    @Override
    public String convertEJSDurationMeasureToString(final EDataType eDataType, final Object instanceValue) {
        return this.writeObject(instanceValue);
    }

     /**
     * Serializes an object to a string.
     *
     * @param o
     *            The object to store.
     * @return String containing the serialized object.
     * @throws IOException
     *             Error occurred during serialization.
     */
    private String writeObject(final Object o) {
        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bas);
            oos.writeObject(o);
            oos.close();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not serialize object to string. Data corruption is likely. " + "Object: "
                    + o + "Error: " + e.getMessage());
        }
        return Base64.encodeBase64String(bas.toByteArray());
    }

    /**
     * Deserialized an object from a string.
     *
     * @param bis
     *            The input string containing the object.
     * @return The deserialized object.
     * @throws IOException
     *             Error occurred during serialization.
     * @throws ClassNotFoundException
     *             Error occurred during serialization.
     */
    private Object readObject(final String input) {
        Object o = null;
        try {
            ObjectInputStream ois;
            ois = new ObjectInputStream(new ByteArrayInputStream(Base64.decodeBase64(input)));
            o = ois.readObject();
            ois.close();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not deserialize object from string. Data corruption is likely. "
                    + "Error: " + e.getMessage());
        } catch (final ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not deserialize object from string. No class for object could be found. "
                    + "Data corruption is likely. " + "Error: " + e.getMessage());
        }
        return o;
    }

}
