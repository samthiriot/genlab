package fr.edf.everest.algos;

import java.util.ArrayList;
import java.util.Arrays;

import fr.edf.everest.ConstantEnumeratedModelInput;
import fr.edf.everest.DoubleTimeSerieModelInput;
import fr.edf.everest.LastValueOfTimeserieModelOutput;

/**
 * The everest model AGG6.3
 * 
 * @author Samuel Thiriot
 *
 */
public class AGG6_3EverestModelAlgo extends AbstractEverestModelAlgo {

	public static final Double[] getDoubleZeros(int count) {
		Double[] res = new Double[count];
		Arrays.fill(res, 0.0);
		return res;
		
	}
	
	public AGG6_3EverestModelAlgo() {
		super(
				"Everest AGG6.3PhaseII", 
				"runs a model AGG6.3 on an Everest server"
				);
		
		declareModelInput(new ConstantEnumeratedModelInput(
				"Building1", 
				"1400003061",
				"ControlTypeENDFC",
				new ArrayList<String>() {{
					add("1");
					add("2");
					add("3");
				}}
				));
		declareModelInput(new ConstantEnumeratedModelInput(
				"Building1", 
				"1400003061",
				"TechTypeENDFC",
				new ArrayList<String>() {{;
					add("SOFC");
					add("PEMFC");
				}}
				));
		declareModelInput(new DoubleTimeSerieModelInput(
				"Building1", 
				"1400003061", 
				"PowerNomElENDFC", 
				0.0, // TODO !
				50.0, // TODO ! 
				getDoubleZeros(21), // TODO from default values ! 
				0.0, 
				5 // TODO !
				));
		declareModelInput(new DoubleTimeSerieModelInput(
				"Singapor", 
				"10", 
				"COPGreenTicks0_ACENBGH_AC", 
				0.0, // TODO !
				6.0, // TODO ! 
				getDoubleZeros(1), // TODO from default values ! 
				0.0, 
				0 // TODO !
				));
		
		declareModelOutput(new LastValueOfTimeserieModelOutput(
				"Neighborhood", 
				"1200000001", "Yuhua",
				"Neighborhood_TotAggGHG"
				)		
		);
		declareModelOutput(new LastValueOfTimeserieModelOutput(
				"Neighborhood", 
				"1200000001", "Yuhua",
				"Neighborhood_AnTotalCost"
				)		
		);
		declareModelOutput(new LastValueOfTimeserieModelOutput(
				"Neighborhood", 
				"1200000001", "Yuhua",
				"Neighborhood_PointsGreenMarkBUIGM"
				)		
		);
		declareModelOutput(new LastValueOfTimeserieModelOutput(
				"Building", 
				"1400004344", "Block231 !!!",
				"Building_AnFuelConsumptionENDFC"
				)		
		);
		
	}

}
