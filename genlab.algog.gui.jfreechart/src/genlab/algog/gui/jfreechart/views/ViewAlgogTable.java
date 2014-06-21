package genlab.algog.gui.jfreechart.views;

import genlab.algog.algos.meta.GeneticExplorationAlgo;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IParametersListener;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.ShapeUtilities;

public class ViewAlgogTable extends AbstractViewOpenedByAlgo implements IParametersListener {

	public static final String VIEW_ID = "genlab.algog.gui.jfreechart.views.ViewAlgoGTable";
	
	public GenlabTable glTable = null;

	protected Map<String, XYSeries> goal2serie = new HashMap<String, XYSeries>();
	protected Map<String, JFreeChart> goal2chart = new HashMap<String, JFreeChart>();
	protected Map<String, ChartComposite> goal2compositeChart = new HashMap<String, ChartComposite>();
	protected Map<String, XYLineAnnotation> goal2targetAnnotation = new HashMap<String, XYLineAnnotation>();

	protected Map<String, XYSeries> gene2serie = new HashMap<String, XYSeries>();
	protected Map<String, JFreeChart> gene2chart = new HashMap<String, JFreeChart>();
	protected Map<String, ChartComposite> gene2compositeChart = new HashMap<String, ChartComposite>();
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private Section sectionGenes;
	private Section sectionGoals;
	
	private Composite compoGoals;
	private Composite compoGenes;
		
	public ViewAlgogTable() {
		
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}
	
	
	protected void createWidgetsForGoal(String goal, Map<String,String> goalMetadata, int maxIterations) {

		messages.traceTech("init the jfreechart dataset for goal "+goal+" ...", getClass());
		
		XYDataset dataset = null;
		XYSeries serie = null;
		{
			XYSeriesCollection seriesAll = new XYSeriesCollection();
		 
			serie = new XYSeries(goal+"/values");
		    
			seriesAll.addSeries(serie);
			
			dataset = seriesAll;
		}
		
		
		messages.traceTech("init the jfreechart chart for goal "+goal+"...", getClass());
		
		// create chart
		JFreeChart chart = ChartFactory.createScatterPlot(
				"goal "+goal+": target and values for iterations", 
				"iterations", 
				"value", 
				dataset
				);
		
		// set parameters of chart
		chart.setAntiAlias(true);
		
		// set up X axis with fixed range (we know the max there)
		NumberAxis range = (NumberAxis) chart.getXYPlot().getDomainAxis();
        range.setRange(0, maxIterations+1);
        //range.setTickUnit(new NumberTickUnit(1d));
        
        // set small dots as shape
        /*
        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        Shape dotShape = ShapeUtilities.createRegularCross(3, 3); // new Rectangle(1, 1);
        renderer.setBaseShape(dotShape);
        renderer.setBasePaint(Color.DARK_GRAY);
        renderer.setSeriesShape(0, dotShape);
        renderer.setSeriesPaint(0, Color.DARK_GRAY);
        */
        


        
		messages.traceTech("init the chart composite for goal "+goal+"...", getClass());

		final int preferedWidth = 900;
		final int preferedHeight = 300;
	
		ChartComposite compositeChart = new ChartComposite(
				compoGoals, 
				SWT.NONE, 
				chart,
				true // use a buffer
				);	
		compositeChart.setSize(preferedWidth, preferedHeight);
		compositeChart.setLayoutData(new RowData(preferedWidth, preferedHeight));
		toolkit.adapt(compositeChart, true, true);
		//compositeChart.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		
		// save all of them
		goal2chart.put(goal, chart);
		goal2compositeChart.put(goal, compositeChart);
		goal2serie.put(goal, serie);
		
		// update display
		compoGoals.layout(true);
		sectionGoals.layout(true);
		
	}
	
	protected void displayDataForGoal(String goal, Map<String,String> goalMetadata, String columnIteration, int maxIterations) {

		XYSeries serie = goal2serie.get(goal);
		
		// create if necessary
		if (serie == null) {
			createWidgetsForGoal(goal, goalMetadata, maxIterations);
			serie = goal2serie.get(goal);
		}
		
		// update data
		final String columnTarget = goalMetadata.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_TARGET);
		final String columnValue = goalMetadata.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_VALUE);
		final String columnFitness = goalMetadata.get(GeneticExplorationAlgo.TABLE_COLUMN_METADATA_VALUE_FITNESS);
		
		// update data !
		try {
			serie.setNotify(false);
			serie.clear();
			for (int rowId=0; rowId<glTable.getRowsCount(); rowId++) {
		    	
				try {
					Object dataX = glTable.getValue(rowId, columnIteration);
					Object dataY = glTable.getValue(rowId, columnValue);
					
					// ignore incomplete data
			        if (dataX == null || dataY == null)
			        	continue;
			        
			        serie.add((Number)dataX, (Number)dataY);
				} catch (ClassCastException e) {
					// ignore, but this should not happen !
				}
		    }
			
			
			if (!goal2targetAnnotation.containsKey(goal)) {

				// retrieve the plot
				final JFreeChart chart = goal2chart.get(goal);
				final XYPlot plot = chart.getXYPlot();
			
				// add the annotation
				final Number targetValue = (Number)glTable.getValue(0, columnTarget);
				XYLineAnnotation line = new XYLineAnnotation(
						// from iteration 0
						0d, 	
						targetValue.doubleValue(), 
						// to last iteration
						maxIterations,	
						targetValue.doubleValue(),
						new BasicStroke(1f), 
						Color.black
						);
		        plot.addAnnotation(line);
		
		        // store annotation for later use
				goal2targetAnnotation.put(goal, line);
			
			}
						
		} finally {
			serie.setNotify(true);

		}
		
	}
	

	private void displayDataForGene(String gene,
			String columnValue, String columnIteration,
			Integer maxIterations) {
		
		XYSeries serie = gene2serie.get(gene);
		
		// create if necessary
		if (serie == null) {
			createWidgetsForGene(gene, maxIterations);
			serie = gene2serie.get(gene);
		}
		
		// update data !
		try {
			serie.setNotify(false);
			serie.clear();
			for (int rowId=0; rowId<glTable.getRowsCount(); rowId++) {
		    	
				try {
					Object dataX = glTable.getValue(rowId, columnIteration);
					Object dataY = glTable.getValue(rowId, columnValue);
					
					// ignore incomplete data
			        if (dataX == null || dataY == null)
			        	continue;
			        
			        serie.add((Number)dataX, (Number)dataY);
				} catch (ClassCastException e) {
					// ignore, but this should not happen !
				}
		    }
				
		} finally {
			serie.setNotify(true);

		}
	}
	
	private void createWidgetsForGene(String gene, Integer maxIterations) {

		messages.traceTech("init the jfreechart dataset for gene "+gene+" ...", getClass());
		
		XYDataset dataset = null;
		XYSeries serie = null;
		{
			XYSeriesCollection seriesAll = new XYSeriesCollection();
		 
			serie = new XYSeries(gene+"/values");
		    
			seriesAll.addSeries(serie);
			
			dataset = seriesAll;
		}
		
		
		messages.traceTech("init the jfreechart chart for gene "+gene+"...", getClass());
		
		// create chart
		JFreeChart chart = ChartFactory.createScatterPlot(
				"gene "+gene+" values for iterations", 
				"iterations", 
				"value", 
				dataset
				);
		
		// set parameters of chart
		chart.setAntiAlias(true);
		
		// set up X axis with fixed range (we know the max there)
		NumberAxis range = (NumberAxis) chart.getXYPlot().getDomainAxis();
        range.setRange(0, maxIterations+1);
        //range.setTickUnit(new NumberTickUnit(1d));
        
        // set small dots as shape
        /*
        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        Shape dotShape = ShapeUtilities.createRegularCross(3, 3); // new Rectangle(1, 1);
        renderer.setBaseShape(dotShape);
        renderer.setBasePaint(Color.DARK_GRAY);
        renderer.setSeriesShape(0, dotShape);
        renderer.setSeriesPaint(0, Color.DARK_GRAY);
        */
        

        
		messages.traceTech("init the chart composite for gene "+gene+"...", getClass());

		final int preferedWidth = 900;
		final int preferedHeight = 400;
	
		ChartComposite compositeChart = new ChartComposite(
				compoGenes, 
				SWT.NONE, 
				chart,
				true // use a buffer
				);	
		compositeChart.setSize(preferedWidth, preferedHeight);
		compositeChart.setLayoutData(new RowData(preferedWidth, preferedHeight));
		//compositeChart.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		toolkit.adapt(compositeChart, true, true);
		
		
		// save all of them
		gene2chart.put(gene, chart);
		gene2compositeChart.put(gene, compositeChart);
		gene2serie.put(gene, serie);
		
		// update display
		compoGenes.layout(true);
		sectionGenes.layout(true);

	}

	public void loadDataFromTable() {
		
		try {
			showBusy(true);
			

			final String columnIteration = (String) glTable.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_COLTITLE_ITERATION);
			final Integer maxIterations = (Integer)glTable.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_MAX_ITERATIONS);
			

			{
				Object metadataRawGoals = glTable.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_GOALS2COLS);
				if (metadataRawGoals == null) {
					messages.warnTech("unable to find the expected metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
					return;
				}
				Map<String,Map<String,String>> metadata = null;
				try {
					metadata = (Map<String,Map<String,String>>)metadataRawGoals;
				} catch (ClassCastException e) {
					messages.warnTech("wrong metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
					return;
				}
					
				// display goals
				for (String goal: metadata.keySet()) {
					
					final Map<String,String> goalMetadata = metadata.get(goal);
					
					displayDataForGoal(goal, goalMetadata, columnIteration, maxIterations);
					
				}
			}
			
			{
				Object metadataRawGenes = glTable.getTableMetaData(GeneticExplorationAlgo.TABLE_METADATA_KEY_GENES2VALUES);
				if (metadataRawGenes == null) {
					messages.warnTech("unable to find the expected metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
					return;
				}
				Map<String,String> metadata = null;
				try {
					metadata = (Map<String,String>)metadataRawGenes;
				} catch (ClassCastException e) {
					messages.warnTech("wrong metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
					return;
				}
					
				// display genes
				for (String gene: metadata.keySet()) {
										
					displayDataForGene(gene, metadata.get(gene), columnIteration, maxIterations);
					
				}
			}
			
			form.layout(true);
		} finally {
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
	

	@Override
	public void createPartControl(Composite parent) {
		
		messages.traceTech("init the form...", getClass());
		
		toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new FillLayout());
		
		form = toolkit.createScrolledForm(parent);
		Layout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		
		sectionGoals = toolkit.createSection(form.getBody(),  Section.TITLE_BAR);
		sectionGoals.setExpanded(true);
		sectionGoals.setText("Measures and targets");
		sectionGoals.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		compoGoals = toolkit.createComposite(sectionGoals);
		compoGoals.setLayout(new RowLayout(SWT.VERTICAL));
		sectionGoals.setClient(compoGoals);
		
		sectionGenes = toolkit.createSection(form.getBody(),  Section.TITLE_BAR);
		sectionGenes.setExpanded(true);
		sectionGenes.setText("Genes");
		sectionGenes.setLayout(new RowLayout(SWT.VERTICAL));
		sectionGenes.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		compoGenes = toolkit.createComposite(sectionGenes);
		compoGenes.setLayout(new RowLayout(SWT.VERTICAL));
		sectionGenes.setClient(compoGenes);
		
		
		// update display
		form.layout(true);
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
		
		
		loadDataFromTable();
		
	}
	
	@Override
	public void dispose() {
		
		// clear charts
		for (ChartComposite compositeChart : goal2compositeChart.values()) {
			compositeChart.dispose();
		}
		goal2chart.clear();
		goal2compositeChart.clear();
		goal2serie.clear();
		goal2targetAnnotation.clear();
		
		if (form != null)
			form.dispose();
		if (toolkit != null)
			toolkit.dispose();
		super.dispose();
	}


}
