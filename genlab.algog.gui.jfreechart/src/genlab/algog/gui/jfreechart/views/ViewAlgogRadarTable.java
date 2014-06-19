package genlab.algog.gui.jfreechart.views;

import genlab.algog.algos.meta.GeneticExplorationAlgo;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IParametersListener;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;

public class ViewAlgogRadarTable extends AbstractViewOpenedByAlgo implements IParametersListener {

	public static final String VIEW_ID = "genlab.algog.gui.jfreechart.views.ViewAlgoGTableRadar";
	
	public GenlabTable glTable = null;

	
	protected Map<Integer, CategoryDataset> individualIdx2dataset = new HashMap<Integer, CategoryDataset>();
	protected Map<Integer, JFreeChart> individualIdx2chart = new HashMap<Integer, JFreeChart>();
	protected Map<Integer, ChartComposite> individualIdx2compositeChart = new HashMap<Integer, ChartComposite>();

	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private Label labelIteration ;
	private Composite compositeCharts;
	private GridLayout gridLayoutCompositeCharts;
	
	final int preferedWidth = 300;
	final int preferedHeight = 300;
		
	public ViewAlgogRadarTable() {
		
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}
	
	
	protected void displayDataForIndividual(
			int displayIndividualId, 
			final int rowId, 
			String columnIteration,
			final Map<String,Map<String,String>> metadata,
			Map<String, Number> key2min,
			Map<String, Number> key2max
			) {


		messages.traceTech("init the jfreechart dataset for individual "+displayIndividualId+" ...", getClass());
		
		// create dataset
        
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        final String axisTarget = "target";
        final String axisValue = "value";
        
        try {
	        for (String goal: metadata.keySet()) {
	        	
	        	final Map<String,String> metadataGoal = metadata.get(goal);
	            String colGoalTarget = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_TARGET);
	            String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_VALUE);
	            double goalTarget = ((Number)glTable.getValue(rowId, colGoalTarget)).doubleValue();
	            double goalValue = ((Number)glTable.getValue(rowId, colGoalValue)).doubleValue();
	            
	            double valueMin = Math.min(key2min.get(colGoalValue).doubleValue(), goalTarget);
	            double valueMax = Math.max(key2max.get(colGoalValue).doubleValue(), goalTarget);
	            valueMin -= valueMin/10;
	            //valueMax += valueMax/10;
	            /*
	            (valueMin - b) / ratio = 0
	            (valueMax - b) / ratio = 1
	            (goalTarget - b) / ratio = 0.5
	    			
	            goalTarget - 0.5*ratio  = b  
	            (valueMax - goalTarget - 0.5*ratio) / ratio = 1
	            valueMax - goalTarget - 0.5*ratio = ratio
	            valueMax - goalTarget  = 1.5 * ratio
	            (valueMax - goalTarget)/1.5 = ratio
	            */
	            //double b = 2 * goalTarget - valueMax + valueMin;
	            //double ratio = valueMax + b;
	            
	            
	            		
	            
	            //double b = goalTarget*2 - valueMax - valueMin;
	            double ratio  = (valueMax - valueMin);
	            double b = 1; //ratio/(goalTarget-valueMin)*0.5;
	            
	            //double b = goalTarget - ratio/2;
	            //double b = -valueMin/ratio;
	            //double propTarget = goalTarget/ratio; //(goalTarget-valueMin)/(valueMax-valueMin);
	            //double propValue = goalValue/ratio; // (goalTarget-valueMin)*2;
	            double propTarget = (goalTarget-valueMin)/ratio*b;
	            double propValue = (goalValue-valueMin)/ratio*b;
	            		
	            dataset.addValue(
	            		propTarget, 
	            		axisTarget, 
	            		goal
	            		);
	            dataset.addValue(
	            		propValue,
	            		axisValue, 
	            		goal
	            		);

	        }
        } catch (ClassCastException e) {
        	throw new WrongParametersException("this column does not contains numbers");
        }
        
        individualIdx2dataset.put(rowId, dataset);
        
		// create the plot
        final SpiderWebPlot chartRadar = new SpiderWebPlot(dataset);
        chartRadar.setMaxValue(1.0);
       
        chartRadar.setWebFilled(true);
        // TODO ???
        //chartRadar.setStartAngle(54D); 
        chartRadar.setInteriorGap(0.2D);
        chartRadar.setToolTipGenerator(new CategoryToolTipGenerator() {
			
			@Override
			public String generateToolTip(CategoryDataset dataset, int idx1, int idx2) {
				
				// for some reason, idx2 is the only one relevant (???)
				
				// String key1 = (String)dataset.getColumnKey(idx1);
				String key2 = (String)dataset.getColumnKey(idx2);
				
	        	final Map<String,String> metadataGoal = metadata.get(key2);
	            String colGoalTarget = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_TARGET);
	            String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_VALUE);

				Number goalTarget = (Number)glTable.getValue(rowId, colGoalTarget);
				Number goalValue = (Number)glTable.getValue(rowId, colGoalValue);
	            
				return key2+": target = "+goalTarget+", value = "+goalValue;
			}
		});
        
        final JFreeChart jfreechart = new JFreeChart(
        		"individual "+displayIndividualId, 
        		TextTitle.DEFAULT_FONT, 
        		chartRadar, 
        		false
        		);
        LegendTitle legendtitle = new LegendTitle(chartRadar);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
   		jfreechart.addSubtitle(legendtitle);
   		jfreechart.getPlot().setBackgroundPaint(Color.white);
   		individualIdx2chart.put(rowId, jfreechart);
       
		// add in a composite
		messages.traceTech("init the chart composite for individual "+displayIndividualId+"...", getClass());

		ChartComposite compositeChart = new ChartComposite(
				compositeCharts, 
				SWT.NONE, 
				jfreechart,
				true // use a buffer
				);	
		compositeChart.setSize(preferedWidth, preferedHeight);
		compositeChart.setLayoutData(new GridData(preferedWidth, preferedHeight));
		compositeChart.setBackground(compositeCharts.getBackground());
		
		individualIdx2compositeChart.put(rowId, compositeChart);
		
	}
	
	protected void clearWidgetsForCurrentIteration() {
		
		for (ChartComposite compositeChart: individualIdx2compositeChart.values()) {
			compositeChart.dispose();
		}
		individualIdx2chart.clear();
		individualIdx2compositeChart.clear();
		individualIdx2dataset.clear();
		
	}
	
	public void loadDataFromTable() {
		
		try {
			showBusy(true);
			
			compositeCharts.setLayoutDeferred(true);
			
			// clear all existing widgets
			clearWidgetsForCurrentIteration();
			
			// decode metadata
			Object metadataRaw = glTable.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_GOALS2COLS);
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
				
			final String columnIteration = (String) glTable.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_COLTITLE_ITERATION);
			final Integer maxIterations = (Integer)glTable.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_MAX_ITERATIONS);
			
			// TODO make a parameter of that
			final Integer iterationToDisplay = (Integer)glTable.getValue(glTable.getRowsCount()-1, columnIteration);
			labelIteration.setText("results for iteration "+iterationToDisplay);
			
			// search for the first line to display
			int currentRow = glTable.getRowsCount();
			Integer currentRowIteration = null;
			do {
				currentRow --;
				currentRowIteration = (Integer)glTable.getValue(currentRow, columnIteration);
			} while (currentRowIteration != iterationToDisplay);
			
			final int rowFirst = currentRow;

			// first iterate to find
			// * the row end
			// * the min and max for each goal
			Map<String, Number> key2min = new HashMap<String, Number>(metadata.keySet().size());
			Map<String, Number> key2max = new HashMap<String, Number>(metadata.keySet().size());
			do {
				// for each goal, measure min and max
				for (String goal: metadata.keySet()) {
		        	
		        	final Map<String,String> metadataGoal = metadata.get(goal);
		            
		        	String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_VALUE);
		            Number goalValue = (Number)glTable.getValue(currentRow, colGoalValue);
		            
		            Number minBefore = key2min.get(colGoalValue);
		            if ( (minBefore == null) || (minBefore.doubleValue() > goalValue.doubleValue())) {
		            	key2min.put(colGoalValue, goalValue);
		            }
		            
		            Number maxBefore = key2max.get(colGoalValue);
		            if ( (maxBefore == null) || (maxBefore.doubleValue() < goalValue.doubleValue())) {
		            	key2max.put(colGoalValue, goalValue);
		            } 
	            
		            
		        }
				currentRowIteration = (Integer)glTable.getValue(currentRow, columnIteration);
				currentRow --;
			} while ( (currentRowIteration == iterationToDisplay) && (currentRow >= 0) );

			// display all the individuals for this iteration ID
			final int rowEnd = currentRow;
			int individualDisplayId = 1;
			for (currentRow = rowFirst; currentRow > rowEnd; currentRow --) {
				displayDataForIndividual(individualDisplayId, currentRow, columnIteration, metadata, key2min, key2max);
				individualDisplayId++;	
			}
			
			
		} catch(RuntimeException e) {
			e.printStackTrace();
			
		} finally {

			
			// layout !
			compositeCharts.setLayoutDeferred(false);
			compositeCharts.layout(true);
			form.getBody().layout(true);
			showBusy(false);	
		}
	}
	
	public void adaptParametersWidgets() {
		
		String[] titles = new String[glTable.getColumnsCount()];
		
		glTable.getColumnsId().toArray(titles);
		
	}
	
	
	public void setData(IAlgoInstance viewAlgoInstance, GenlabTable glTable) {


		if (glTable == null)
			return;
		
		messages.traceTech("received data to display.", getClass());

		this.algoInstance = viewAlgoInstance;
		
		viewAlgoInstance.addParametersListener(this);

		// retrieve data
		this.glTable = glTable;

		adaptParametersWidgets();
				
		loadDataFromTable();
		
		
		
	}
	
	protected void manageResized() {
	
		try {
			gridLayoutCompositeCharts.numColumns = (int)Math.floor(form.getClientArea().width/preferedWidth);
			compositeCharts.layout(true);
			
		} catch (RuntimeException e) {
			// ignore
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		messages.traceTech("init the form...", getClass());
		
		toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new FillLayout());
		form = toolkit.createScrolledForm(parent);
		
		
		Layout layout = new RowLayout(SWT.VERTICAL);
		form.getBody().setLayout(layout);

		form.getBody().addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				manageResized();
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
			}
		});

		labelIteration = toolkit.createLabel(form.getBody(), "not data displayed yet");
		// TODO separator toolkit.createSeparator(form.getBody(), );
		compositeCharts = toolkit.createComposite(form.getBody());
		
		gridLayoutCompositeCharts = new GridLayout(
				(int)Math.floor(form.getBody().getClientArea().width/preferedWidth), 
				true
				);
		compositeCharts.setLayout(gridLayoutCompositeCharts);
		// update display
		form.layout(true);

		form.getBody().layout(true);
		
	
	}

	@Override
	public void setFocus() {
		
		if (form != null)
			form.setFocus();
		
	}

	@Override
	public void parameterValueChanged(
							IAlgoInstance ai, 
							String parameterId,
							Object novelValue) {
		
		
		loadDataFromTable();
		
	}
	
	@Override
	public void dispose() {
		
		// clear charts
		clearWidgetsForCurrentIteration();

		
		if (form != null)
			form.dispose();
		if (toolkit != null)
			toolkit.dispose();
		super.dispose();
	}


}
