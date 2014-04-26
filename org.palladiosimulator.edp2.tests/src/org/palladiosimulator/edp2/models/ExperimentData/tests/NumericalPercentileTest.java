/**
 */
package org.palladiosimulator.edp2.models.ExperimentData.tests;

import junit.textui.TestRunner;

import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.NumericalPercentile;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Numerical Percentile</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class NumericalPercentileTest extends IdentifiableTest {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(NumericalPercentileTest.class);
    }

    /**
     * Constructs a new Numerical Percentile test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NumericalPercentileTest(String name) {
        super(name);
    }

    /**
     * Returns the fixture for this Numerical Percentile test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected NumericalPercentile getFixture() {
        return (NumericalPercentile)fixture;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see junit.framework.TestCase#setUp()
     * @generated
     */
    @Override
    protected void setUp() throws Exception {
        setFixture(ExperimentDataFactory.eINSTANCE.createNumericalPercentile());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see junit.framework.TestCase#tearDown()
     * @generated
     */
    @Override
    protected void tearDown() throws Exception {
        setFixture(null);
    }

} //NumericalPercentileTest