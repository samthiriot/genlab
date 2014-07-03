package genlab.algog.gui.jfreechart.views;

import genlab.algog.algos.meta.GeneticExplorationAlgo;
import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IParametersListener;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.VisualResources;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleEdge;

public final class ViewAlgogRadarTable 
						extends AbstractViewOpenedByAlgo<GenlabTable> 
						implements IParametersListener {

	public static final String VIEW_ID = "genlab.algog.gui.jfreechart.views.ViewAlgoGTableRadar";
	
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
	protected final List<DefaultCategoryDataset> datasets = new LinkedList<DefaultCategoryDataset>();
	protected final List<SpiderWebPlot> plots = new LinkedList<SpiderWebPlot>();
	protected final List<JFreeChart> charts = new LinkedList<JFreeChart>();
	protected final List<ChartComposite> chartComposites = new LinkedList<ChartComposite>();
	protected final List<Composite> hostCharts = new LinkedList<Composite>();
	
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private Label labelIteration ;
	private Composite compositeCharts;
	private GridLayout gridLayoutCompositeCharts;
	
	final int preferedWidth = 350;
	final int preferedHeight = 200;
		
	public ViewAlgogRadarTable() {
		
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}
	
	/**
	 * adds the required composites and chart and all graphical elements, if necessary, to make it 
	 * possible to display the Nth individual 
	 */
	protected boolean prepareChartsForIndividual(int displayId) {
		
		if (datasets.size() <= displayId) {
			// not enough charts; should add one more chart !

			// create dataset
			final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        datasets.add(dataset);
	        
	        // create plot  
	        final SpiderWebPlot plotRadar = new SpiderWebPlot(dataset);
	        plotRadar.setMaxValue(1.0);
	        plotRadar.setWebFilled(true);
	        // TODO ???chartRadar.setStartAngle(54D); 
	        plotRadar.setInteriorGap(0.2D);
			plotRadar.setBackgroundPaint(Color.WHITE);

	        plots.add(plotRadar);
	        
	        // create chart
	        final JFreeChart jfreechart = new JFreeChart(
	        		"individual "+(displayId+1), 
	        		TextTitle.DEFAULT_FONT, 
	        		plotRadar, 
	        		false
	        		);
	        LegendTitle legendtitle = new LegendTitle(plotRadar);
			legendtitle.setPosition(RectangleEdge.BOTTOM);
	   		jfreechart.addSubtitle(legendtitle);
	   		jfreechart.getPlot().setBackgroundPaint(Color.white);
	   		jfreechart.setBackgroundPaint(Color.WHITE);

	   		charts.add(jfreechart);
	        
	   		// create host composite
	   		Composite compositeHost = toolkit.createComposite(compositeCharts);
			compositeHost.setLayout(new GridLayout(1, false));
			compositeHost.setLayoutData(new GridData());
			hostCharts.add(compositeHost);			
			
			ChartComposite compositeChart = new ChartComposite(
					compositeHost, 
					SWT.NONE, 
					jfreechart,
					true // use a buffer
					);	
			compositeChart.setSize(preferedWidth, preferedHeight);
			compositeChart.setLayoutData(new GridData(preferedWidth, preferedHeight));
			compositeChart.setBackground(compositeCharts.getBackground());
			chartComposites.add(compositeChart);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Displays the strings as labels in the composite. Reuses previous labels. 
	 * returns true if a layout is required. 
	 * @param host
	 * @param toDisplay
	 * @return
	 */
	protected boolean displayLabels(Composite host, List<String> toDisplay) {
		
		boolean shouldLayout = false;
		
		// retrieve existing labels
		List<Label> existingLabels = new LinkedList<Label>();
		for (Control child: host.getChildren()) {
			if (child instanceof Label) {
				existingLabels.add((Label) child);
			}
		}
		
		// now use them to display data
		for (int i=0; i<toDisplay.size(); i++) {
			
			final String content = toDisplay.get(i);
			
			if (existingLabels.size()<=i) {
				// should add one label for this one !
				final Label l = toolkit.createLabel(host, content);
				l.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
				shouldLayout = true;
			} else {
				// reuse existing one !
				final Label l = existingLabels.get(i);
				l.setText(content);
				if (!l.isVisible())
					shouldLayout = true;
				l.setVisible(true);
				((GridData)l.getLayoutData()).exclude = false;
			}
			toDisplay.get(i);
		}
		
		// hide the labels which are useless
		if (toDisplay.size() < existingLabels.size()) {
			shouldLayout = true;
			for (int i=toDisplay.size(); i<existingLabels.size(); i++) {
				final Label l = existingLabels.get(i);
				l.setVisible(false);
				((GridData)l.getLayoutData()).exclude = true;
			}
		}
			
		return shouldLayout;
	}
	
	protected boolean displayDataForIndividual(
			int displayIndividualId, 
			final int rowId, 
			String columnIteration,
			final Map<String,Map<String,String>> metadata,
			Map<String, Number> key2min,
			Map<String, Number> key2max
			) {

		boolean widgetChanged = false;
		
		if (DEBUG_DURATIONS) 
			Timers.SINGLETON.startTask(DEBUG_KEY_FILL_CHART);
		
		
		// ensure we will find all objects for this individual
		widgetChanged = prepareChartsForIndividual(displayIndividualId);
		
		messages.traceTech("init the jfreechart dataset for individual "+displayIndividualId+" ...", getClass());
		
		// update dataset
        
		final DefaultCategoryDataset dataset = datasets.get(displayIndividualId);
		dataset.clear();
		
        final String axisTarget = "target";
        final String axisValue = "value";
                
        try {
	        for (String goal: metadata.keySet()) {
	        	
	        	final Map<String,String> metadataGoal = metadata.get(goal);
	            String colGoalTarget = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_TARGET);
	            String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_VALUE);
	            double goalTarget = ((Number)lastVersionDataToDisplay.getValue(rowId, colGoalTarget)).doubleValue();
	            double goalValue = ((Number)lastVersionDataToDisplay.getValue(rowId, colGoalValue)).doubleValue();
	            
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
       
		// update the plot
        final SpiderWebPlot chartRadar = plots.get(displayIndividualId);
        chartRadar.setToolTipGenerator(new CategoryToolTipGenerator() {
			
			@Override
			public String generateToolTip(CategoryDataset dataset, int idx1, int idx2) {
				
				// for some reason, idx2 is the only one relevant (???)
				
				// String key1 = (String)dataset.getColumnKey(idx1);
				String key2 = (String)dataset.getColumnKey(idx2);
				
	        	final Map<String,String> metadataGoal = metadata.get(key2);
	            String colGoalTarget = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_TARGET);
	            String colGoalValue = metadataGoal.get(GeneticExplorationAlgo.TABLE_COLUMN_GOAL_METADATA_VALUE_VALUE);

				Number goalTarget = (Number)lastVersionDataToDisplay.getValue(rowId, colGoalTarget);
				Number goalValue = (Number)lastVersionDataToDisplay.getValue(rowId, colGoalValue);
	            
				return key2+": target = "+goalTarget+", value = "+goalValue;
			}
		});
        
        if (DEBUG_DURATIONS) {
			Timers.SINGLETON.startTask(DEBUG_KEY_FILL_CHART_LABELS);
        }
        
		// update the composite
		messages.traceTech("init the chart composite for individual "+displayIndividualId+"...", getClass());

		final Composite compositeHost = hostCharts.get(displayIndividualId);
		compositeHost.setLayoutDeferred(true);
		compositeHost.setVisible(true);
		((GridData)compositeHost.getLayoutData()).exclude = false;
		
		// remove all labels
		Map<String,Map<String,Object>> gene2col = (Map<String, Map<String,Object>>) lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_GENES2METADATA);
		LinkedList<String> toDisplay = new LinkedList<String>();
		for (String gene: gene2col.keySet()) {
			String col = (String)gene2col.get(gene).get(GeneticExplorationAlgo.TABLE_COLUMN_GENE_METADATA_KEY_VALUE);
			toDisplay.add("gene "+gene+": "+lastVersionDataToDisplay.getValue(rowId, col));
		}
		widgetChanged = displayLabels(compositeHost, toDisplay) || widgetChanged;
		
		if (DEBUG_DURATIONS) {
			Timers.SINGLETON.endTask(DEBUG_KEY_FILL_CHART_LABELS, 5);
			Timers.SINGLETON.startTask(DEBUG_KEY_FILL_CHART_LAYOUT);
        }
        
		compositeHost.setLayoutDeferred(false);
		if (widgetChanged)
			compositeHost.pack(true);
		
		if (DEBUG_DURATIONS) { 
			Timers.SINGLETON.endTask(DEBUG_KEY_FILL_CHART_LAYOUT, 5);
			Timers.SINGLETON.endTask(DEBUG_KEY_FILL_CHART, 10);
		}
		return widgetChanged;
	}
	
	protected boolean hideLabelsForIndividualsCount(int totalDisplayedIndividuals) {
		
		boolean somethingChanged = false;
		
		for (int i=totalDisplayedIndividuals; i<datasets.size(); i++) {
			final Composite c = hostCharts.get(i);
			if (c.isVisible())
				somethingChanged = true;
			c.setVisible(false);
			((GridData)c.getLayoutData()).exclude = true;
		}
		
		return somethingChanged;
	}
	
	public void loadDataFromTable() {
		
		if (lastVersionDataToDisplay == null)
			return;
		
		if (compositeCharts.isDisposed())
			return;
		
		boolean shouldLayout = false;
		
		try {
			showBusy(true);
			
			compositeCharts.setLayoutDeferred(true);
			
			if (DEBUG_DURATIONS)
				Timers.SINGLETON.startTask(DEBUG_KEY_LOAD_DATA);
			
			// decode metadata
			Object metadataRaw = lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_GOALS2COLS);
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
				
			final String columnIteration = (String) lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_COLTITLE_ITERATION);
			final Integer maxIterations = (Integer)lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_MAX_ITERATIONS);
			
			// TODO make a parameter of that
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
			
			int individualDisplayId = 0;
			for (currentRow = rowFirst; currentRow > rowEnd; currentRow --) {
			
				
				shouldLayout = displayDataForIndividual(individualDisplayId, currentRow, columnIteration, metadata, key2min, key2max) || shouldLayout;
				individualDisplayId++;
				
			}
			
			// hide all the other ones
			if (DEBUG_DURATIONS) 
				Timers.SINGLETON.startTask(DEBUG_KEY_HIDE_USELESS);
			
			shouldLayout = hideLabelsForIndividualsCount(individualDisplayId) || shouldLayout;
			
			if (DEBUG_DURATIONS) 
				Timers.SINGLETON.endTask(DEBUG_KEY_HIDE_USELESS, 5);
			
			
		} catch(RuntimeException e) {
			e.printStackTrace();
			
		} finally {

			if (DEBUG_DURATIONS) 
				Timers.SINGLETON.startTask(DEBUG_KEY_LAYOUT);
			
			// layout !
			compositeCharts.setLayoutDeferred(false);
			//compositeCharts.layout(true);
			//form.getBody().layout(true);
			if (shouldLayout) {
				form.reflow(true);
			}
			showBusy(false);
			
			
			if (DEBUG_DURATIONS) 
				Timers.SINGLETON.endTask(DEBUG_KEY_LAYOUT, 5);
			
		}
	}
	

	@Override
	protected void dataReceived() {
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
			for (JFreeChart c: charts) {
				c.setNotify(false);
			}
			// ... empty data
			for (DefaultCategoryDataset ds : datasets) {
				ds.clear();
			}
			// ... dispose chart composites
			for (ChartComposite c : chartComposites) {
				if (!c.isDisposed())
					c.dispose();
			}
			// ... dispose host composites
			for (Composite c: hostCharts) {
				if (!c.isDisposed()) {
					removeLabelsFromHostComposite(c);
					c.dispose();
				}
			}
			
			
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
