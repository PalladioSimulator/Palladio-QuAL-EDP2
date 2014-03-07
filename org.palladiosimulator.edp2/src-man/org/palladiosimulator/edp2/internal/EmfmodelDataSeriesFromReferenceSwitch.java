package org.palladiosimulator.edp2.internal;

import javax.measure.quantity.Quantity;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.edp2.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.impl.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.DoubleBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.IdentifierBasedMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.JSXmlMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.LongBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.util.ExperimentDataSwitch;

public class EmfmodelDataSeriesFromReferenceSwitch<Q extends Quantity> 
    extends ExperimentDataSwitch<DataSeries> 
{
	
    /** Factory for Emfmodel. */
	private static final ExperimentDataFactory experimentDatafactory = ExperimentDataFactory.eINSTANCE;
	
	/** Factory which is used to create the DAOs to access data of the DataSeries. */
	private final MeasurementsDaoFactory daoFactory;
	
	/** String which contains the values uuid for the data series. */
	private final String valuesId = EcoreUtil.generateUUID();
	
	public EmfmodelDataSeriesFromReferenceSwitch(MeasurementsDaoFactory daoFactory) {
	    super();
	    
		this.daoFactory = daoFactory;
	}
	
	@Override
	public DataSeries caseIdentifierBasedMeasurements(IdentifierBasedMeasurements object) {
		IdentifierBasedMeasurements ibm = experimentDatafactory.createIdentifierBasedMeasurements();
		daoFactory.createNominalMeasurementsDao(valuesId);
		ibm.setValuesUuid(valuesId);
		return ibm;
	}
	
	@Override
	public DataSeries caseJSXmlMeasurements(JSXmlMeasurements object) {
		JSXmlMeasurements jsxml = experimentDatafactory.createJSXmlMeasurements();
		daoFactory.createJScienceXmlMeasurementsDao(valuesId);
		jsxml.setValuesUuid(valuesId);
		return jsxml;
	}

	@SuppressWarnings("unchecked")
    @Override
	public DataSeries caseDoubleBinaryMeasurements(DoubleBinaryMeasurements object) {
		DoubleBinaryMeasurements dbm = experimentDatafactory.createDoubleBinaryMeasurements();
		dbm.setValuesUuid(valuesId);
		dbm.setStorageUnit(object.getStorageUnit());
		BinaryMeasurementsDao<Double,Q> bmdao = daoFactory.<Q>createDoubleMeasurementsDao(valuesId);
		bmdao.setUnit(dbm.getStorageUnit());
		return dbm;
	}

	@SuppressWarnings("unchecked")
    @Override
	public DataSeries caseLongBinaryMeasurements(LongBinaryMeasurements object) {
		LongBinaryMeasurements lbm = experimentDatafactory.createLongBinaryMeasurements();
		lbm.setValuesUuid(valuesId);
		lbm.setStorageUnit(object.getStorageUnit());
		BinaryMeasurementsDao<Long,Q> bmdao = daoFactory.<Q>createLongMeasurementsDao(valuesId);
		bmdao.setUnit(lbm.getStorageUnit());
		return lbm;
	}

}