
package org.palladiosimulator.edp2.models.measuringpoint.impl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>String Measuring Point</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.palladiosimulator.edp2.models.measuringpoint.impl.StringMeasuringPointImpl#getMeasuringPoint
 * <em>Measuring Point</em>}</li>
 * </ul>
 * </p>
 *
 */
public class StringMeasuringPointImpl extends StringMeasuringPointImplGen {

	/**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getStringRepresentation() {
        return this.getMeasuringPoint();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getResourceURIRepresentation() {
        return "Unkown Resource URI -- String Measuring Point only!";
    }

} // StringMeasuringPointImpl
