package org.palladiosimulator.edp2.filter.warmup;

import java.util.HashMap;
import java.util.Map;

import jakarta.measure.quantity.Duration;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.datastream.configurable.reflective.ConfigurationProperty;
import org.palladiosimulator.edp2.datastream.configurable.reflective.ReflectivePropertyConfigurable;

public class WarmUpFilterConfiguration extends ReflectivePropertyConfigurable {
	
	 public final static String WARMUP_TIME_KEY = "warmupTime";
	 public final static String UNIT_KEY = "unit";

	    

	    @ConfigurationProperty(description = "WarmUp time")
	    private double warmupTime;
	    
	    @ConfigurationProperty(description = "Unit of time")
	    private Unit<Duration> unit;
	    
	

	    @Override
	    public Map<String, Object> getDefaultConfiguration() {
	        final Map<String, Object> result = new HashMap<String, Object>(super.getDefaultConfiguration());
	        result.put(WARMUP_TIME_KEY, 2.0);
	        result.put(UNIT_KEY, Unit.ONE);
	        return result;
	    }

	    /**
	     * @return the warmupFilterTime
	     */
	    public final double getWarmupTime() {
	        return warmupTime;
	    }

		public Unit<Duration> getUnit() {
			// TODO Auto-generated method stub
			return unit;
		}
	
	

}
