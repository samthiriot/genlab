package genlab.algog.gui.jfreechart.views;

import genlab.algog.algos.meta.GeneticExplorationAlgo;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IParametersListener;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.jfreechart.EnhancedSpiderWebPlot;
import genlab.gui.views.AbstractViewOpenedByAlgo;
import genlab.quality.Timers;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleEdge;

public final class ViewAlgogBigRadarTable 
						extends AbstractViewOpenedByAlgo<GenlabTable> 
						implements IParametersListener {

	public static final String VIEW_ID = "genlab.algog.gui.jfreechart.views.ViewAlgogBigRadarTable";
	
	public static final boolean DEBUG_DURATIONS = false;
	public static final String DEBUG_KEY_UPDATE_DATA = "ViewAlgogRadarTable:update data";
	public static final String DEBUG_KEY_LOAD_DATA = "ViewAlgogRadarTable:load data";
	public static final String DEBUG_KEY_CLEAN_LABELS = "ViewAlgogRadarTable:clean labels";
	public static final String DEBUG_KEY_FILL_CHART = "ViewAlgogRadarTable:fill one chart";
	public static final String DEBUG_KEY_FILL_CHART_LAYOUT = "ViewAlgogRadarTable:fill one chart layout";
	public static final String DEBUG_KEY_FILL_CHART_LABELS = "ViewAlgogRadarTable:fill one chart labels";

	
	public static final String DEBUG_KEY_HIDE_USELESS = "ViewAlgogRadarTable:hide";
	public static final String DEBUG_KEY_LAYOUT = "ViewAlgogRadarTable:layout";
	
	/**
	 * The list of plots and datasets used to display individuals; they will be reused to display results for 
	 * each iteration
	 */
	protected DefaultCategoryDataset dataset = null;
	protected EnhancedSpiderWebPlot plot = null;
	protected JFreeChart chart = null;
	protected ChartComposite chartComposites = null;
	protected Composite hostChart = null;
		
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private Label labelIteration ;
	private Composite compositeCharts;
	
	final int preferedWidth = 600;
	final int preferedHeight = 600;
		
	public ViewAlgogBigRadarTable() {
		
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}
	
	
	@SuppressWarnings("unchecked")
	protected Map<String,Map<String,String>> getMetadataFromTable() {
		
		Object metadataRaw = lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_GOALS2COLS);
		if (metadataRaw == null) {
			messages.warnTech("unable to find the expected metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return null;
		}
		try {
			return (Map<String,Map<String,String>>)metadataRaw;
		} catch (ClassCastException e) {
			messages.warnTech("wrong metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return null;
		}
			
	}
	
	public void loadDataFromTable() {
		
		if (lastVersionDataToDisplay == null)
			return;
		
		if (compositeCharts.isDisposed())
			return;
		
		try {
			showBusy(true);
			
			if (DEBUG_DURATIONS)
				Timers.SINGLETON.startTask(DEBUG_KEY_LOAD_DATA);
			
			chart.setNotify(false);
			dataset.clear(); 
		
			// decode metadata
			final Map<String,Map<String,String>> metadata = getMetadataFromTable();
			if (metadata == null)
				return;
			final String columnIteration = (String) lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_COLTITLE_ITERATION);
			
			// define what is the last iteration
			final Integer iterationToDisplay = (Integer)lastVersionDataToDisplay.getValue(lastVersionDataToDisplay.getRowsCount()-1, columnIteration);
			
			// search for the first line to display
			int currentRow = lastVersionDataToDisplay.getRowsCount();
			Integer currentRowIteration = null;
			do {
				currentRow --;
				currentRowIteration = (Integer)lastVersionDataToDisplay.getValue(currentRow, columnIteration);
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
		            
		        	String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_VALUE);
		            Number goalValue = (Number)lastVersionDataToDisplay.getValue(currentRow, colGoalValue);
		            if (goalValue == null) {
		            	// data not available
		            	continue;
		            }
		            
		            Number minBefore = key2min.get(colGoalValue);
		            if ( (minBefore == null) || (minBefore.doubleValue() > goalValue.doubleValue())) {
		            	key2min.put(colGoalValue, goalValue);
		            }
		            
		            Number maxBefore = key2max.get(colGoalValue);
		            if ( (maxBefore == null) || (maxBefore.doubleValue() < goalValue.doubleValue())) {
		            	key2max.put(colGoalValue, goalValue);
		            } 
	            
		            
		        }
				currentRowIteration = (Integer)lastVersionDataToDisplay.getValue(currentRow, columnIteration);
				currentRow --;
			} while ( (currentRowIteration == iterationToDisplay) && (currentRow >= 0) );

			if (DEBUG_DURATIONS) 
				Timers.SINGLETON.endTask(DEBUG_KEY_LOAD_DATA, 5);
				
			// display all the individuals for this iteration ID
			final int rowEnd = currentRow+1;
			
			labelIteration.setText("results for iteration "+iterationToDisplay+" ("+(rowFirst-rowEnd)+" Pareto efficient solutions)");
			          
			// for each individual...
	        int individualDisplayId = 1;
			for (currentRow = rowFirst; currentRow > rowEnd; currentRow --) {
				
	        	// for each goal...
		        for (String goal: metadata.keySet()) {
		        
		        	// retrieve information about this goal
		        	final Map<String,String> metadataGoal = metadata.get(goal);
		            String colGoalTarget = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_TARGET);
		            String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_VALUE);
		            final Object goalValueObject = lastVersionDataToDisplay.getValue(currentRow, colGoalValue);
		            
		            if (goalValueObject == null) {
		            	// data not available
		            	continue;
		            }
		            
		        	double goalTarget = ((Number)lastVersionDataToDisplay.getValue(rowFirst, colGoalTarget)).doubleValue();
		            double goalValue = ((Number)goalValueObject).doubleValue();

		            double valueMin = Math.min(key2min.get(colGoalValue).doubleValue(), goalTarget);
		            double valueMax = Math.max(key2max.get(colGoalValue).doubleValue(), goalTarget);
		            valueMin -= valueMin/10;
		            
		            double ratio  = (valueMax - valueMin);
		            double b = 1; //ratio/(goalTarget-valueMin)*0.5;
		            
		            // for the first individual, let's display the goal first
		            if (currentRow == rowFirst) {
		            	double propTarget = (goalTarget-valueMin)/ratio*b;
		            	final String axisTarget = "target";
			            dataset.addValue(
			            		propTarget, 
			            		axisTarget, 
			            		goal
			            		);			            	
		            }

		            // for all the individuals, also display the value !
		            double propValue = (goalValue-valueMin)/ratio*b;
		            dataset.addValue(
		            		propValue,
		            		"solution "+individualDisplayId, 
		            		goal
		            		);
		            
		        }

			
				individualDisplayId++;
				

			}
			
			
			
			chart.setNotify(true);
			
			// hide all the other ones
			if (DEBUG_DURATIONS) 
				Timers.SINGLETON.startTask(DEBUG_KEY_HIDE_USELESS);
			
			if (DEBUG_DURATIONS) 
				Timers.SINGLETON.endTask(DEBUG_KEY_HIDE_USELESS, 5);
			
			
		} catch(RuntimeException e) {
			e.printStackTrace();
			
		} finally {

			showBusy(false);
			
		}
	}
	

	@Override
	protected void refreshDisplaySync() {
		loadDataFromTable();
		
	}
	
	protected void manageResized() {
	
		try {
			
			final RowData layoutData = (RowData)compositeCharts.getLayoutData(); 
			int size = Math.min(
					form.getClientArea().width-40,					
					form.getClientArea().height-40
					);
			// not working for this chart ! 
			//   Point prefered = chartComposites.computeSize(size, SWT.DEFAULT);
			layoutData.width = size;
			layoutData.height = size;
			
			form.reflow(true);

		} catch (RuntimeException e) {
			// ignore
			e.printStackTrace();
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
		
		super.createPartControl(parent);
		
		messages.traceTech("init the form...", getClass());
		
		toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new FillLayout());
		form = toolkit.createScrolledForm(parent);
				
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.center = false;
		layout.fill = false;
		layout.pack = true;
		layout.justify = false;
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
		compositeCharts.setLayoutData(new RowData());
		compositeCharts.setLayout(new GridLayout(
				1, 
				false
				));
		

		
		// create the chart

		// create dataset
		dataset = new DefaultCategoryDataset();
        
        // create plot  
        plot = new EnhancedSpiderWebPlot(dataset);
        plot.setMaxValue(1.0);
        plot.setWebFilled(true);
        plot.setInteriorGap(0.2D);
        plot.setBackgroundPaint(Color.WHITE);
        
        // create chart
        chart = new JFreeChart(
        		"1th Pareto front", 
        		TextTitle.DEFAULT_FONT, 
        		plot, 
        		false
        		);
        LegendTitle legendtitle = new LegendTitle(plot);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
		chart.addSubtitle(legendtitle);
		chart.getPlot().setBackgroundPaint(Color.white);
		chart.setBackgroundPaint(Color.WHITE);
		
      
   		// create host composite
		chartComposites = new ChartComposite(
				compositeCharts, 
				SWT.NONE, 
				chart,
				true // use a buffer
				);	
		chartComposites.setSize(preferedWidth, preferedHeight);
		chartComposites.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		chartComposites.setBackground(compositeCharts.getBackground());
		
		// update display
		form.getBody().layout(true);

		form.reflow(true);
		
	
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
		
		if (DEBUG_DURATIONS)
			Timers.SINGLETON.startTask(DEBUG_KEY_UPDATE_DATA);
		
		loadDataFromTable();
		
		if (DEBUG_DURATIONS)
			Timers.SINGLETON.endTask(DEBUG_KEY_UPDATE_DATA, 5);
		
	}
	
	/**
	 * Dispose all the labels inside this composite
	 * You should freeze the composite first.
	 * 
	 * @param c
	 */
	protected void removeLabelsFromHostComposite(Composite c) {
		
		if (c.isDisposed())
			return;
				
		
		for (Control child: c.getChildren()) {
			if (child instanceof Label) {
				child.dispose();
			}
		}
		
	}
	
	@Override
	public void dispose() {
		
		try {
			
			// clear our componenents
			// ... freeze charts
			if (chart != null)
				chart.setNotify(false);
			// ... empty data
			if (dataset != null) 
				dataset.clear();
			
			// ... dispose chart composites
			if (chartComposites != null && !chartComposites.isDisposed()) 
				chartComposites.dispose();
			
						
			if (form != null)
				form.dispose();
			if (toolkit != null)
				toolkit.dispose();
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		super.dispose();
	}

	@Override
	public boolean isDisposed() {
		return form != null && form.isDisposed();
	}



}
