package genlab.algog.gui.jfreechart.exec;

import genlab.algog.algos.meta.GeneticExplorationAlgo;
import genlab.algog.gui.jfreechart.algos.FirstFront2DAlgo;
import genlab.algog.gui.jfreechart.instance.FirstFront2DInstance;
import genlab.algog.gui.jfreechart.views.FirstFront2DView;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.exec.IConnectionExecution;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.jfreechart.exec.AbstractJFreeChartAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.util.Map;

public class FirstFront2DExec extends AbstractJFreeChartAlgoExec {

	// the table loaded from a continuous update, or a sequential update.
	private GenlabTable table;
	private boolean parametersDefined = false;
	
	public FirstFront2DExec(IExecution exec, IAlgoInstance algoInst) {
		super(exec, algoInst);
		
	}

	protected void adaptParametersForData(IAlgoInstance algoInst, GenlabTable table) {
		
		if (table == null)
			return;
		
		FirstFront2DInstance algoInstance = (FirstFront2DInstance)algoInst;
		
		// sets the item values for the parameters based on existing content
		algoInstance.getParameterColumnX().setItems(table.getColumnsId());
		algoInstance.getParameterColumnY().setItems(table.getColumnsId());
		
		// also, attempt to use the values or fitness if available
		Object metadataRaw = table.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_GOALS2COLS);
		if (metadataRaw == null) {
			messages.warnTech("unable to find the expected metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return;
		}		
		Map<String,Map<String,String>> metadata = null;
		try {
			metadata = (Map<String,Map<String,String>>)metadataRaw;
		} catch (ClassCastException e) {
			messages.warnTech("wrong metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return;
		}
		
		// search for two goals to display
		String goalDisplayed1 = null;
		String goalDisplayed2 = null;
		for (String goal: metadata.keySet()) {
	    	final Map<String,String> metadataGoal = metadata.get(goal);
	        String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_FITNESS);
	        	        
	        if (goalDisplayed1 == null) {
	        	goalDisplayed1 = colGoalValue;
	        } else if (goalDisplayed2 == null) {
	        	goalDisplayed2 = colGoalValue;
	        	break;
	        }
		}
		
		// then use these goals as default value for these parameters
		if (goalDisplayed1 != null) {
			if (goalDisplayed2 != null) {
				// perfect, we found 2 goals
				algoInstance.getParameterColumnX().setDefaultValue(table.getIndexForColumnId(goalDisplayed1));
				algoInstance.getParameterColumnY().setDefaultValue(table.getIndexForColumnId(goalDisplayed2));
			} else {
				// oops, we found only one goal 
				// let's let the default for X (should be 0, so iteration) and use the goal for the second
				algoInstance.getParameterColumnY().setDefaultValue(table.getIndexForColumnId(goalDisplayed1));
			}
			// else nothing will be defined, normal behaviour: the user has to select the parameters by himself.
		}
		
		parametersDefined = true;
	}
	
	protected void loadDataSuccessiveFromInput() {
		table = (GenlabTable)getInputValueForInput(FirstFront2DAlgo.INPUT_TABLE);

	}
		
	protected void setDataFromContinuousUpdate(IAlgoExecution continuousProducer,
			Object keyWave, IConnectionExecution connectionExec, Object value) {

		table = (GenlabTable)value;

	}

	
	@Override
	protected void displayResultsSync(AbstractViewOpenedByAlgo theView) {
		
		if (! parametersDefined ) 
			adaptParametersForData(algoInst, table);
		
		((FirstFront2DView)theView).setData(
				algoInst,
				table
				);
	}

	@Override
	protected void displayResultsSyncReduced(AbstractViewOpenedByAlgo theView,
			IAlgoExecution executionRun, IConnectionExecution connectionExec,
			Object value) {
		// TODO Auto-generated method stub
		
	}


}
