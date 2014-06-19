package genlab.gui.jfreechart.views;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IParametersListener;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.actions.ShowParametersAction;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.jfreechart.instance.ScatterPlotAlgoInstance;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

public class ScatterView extends AbstractViewOpenedByAlgo implements IParametersListener {

	public static final String VIEW_ID = "genlab.gui.jfreechart.views.ScatterView";
	
	protected String chartTitle = "xy";
	protected String chartXLabel = "x";
	protected String chartYLabel = "y";
	
	protected int glTableColumnXIdx;
	protected int glTableColumnYIdx;
	
	public GenlabTable glTable = null;

	XYSeries serie = null;
			
	protected JFreeChart chart = null;
	protected ChartComposite compositeChart = null;

	private FormToolkit toolkit;
	private ScrolledForm form;
		
	public ScatterView() {
		
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}
	
	public void loadDataFromParameters(IAlgoInstance viewAlgoInstance) {
		
		ScatterPlotAlgoInstance ai = (ScatterPlotAlgoInstance)viewAlgoInstance;
		
		this.glTableColumnXIdx = (Integer) viewAlgoInstance.getValueForParameter(ai.getParameterColumnX());
		this.glTableColumnYIdx = (Integer) viewAlgoInstance.getValueForParameter(ai.getParameterColumnY());
		
	}
	
	public void loadDataFromTable() {
		
		try {
			showBusy(true);

			chartXLabel = glTable.getColumnIdForIdx(glTableColumnXIdx);
			chartYLabel = glTable.getColumnIdForIdx(glTableColumnYIdx);
		
			// update labels
			chart.getXYPlot().getDomainAxis(0).setLabel(chartXLabel);
			chart.getXYPlot().getRangeAxis().setLabel(chartYLabel);
			
			// update data !
			serie.setNotify(false);
			serie.clear();
			for (int rowId=0; rowId<glTable.getRowsCount(); rowId++) {
		    	
				Object dataX = glTable.getValue(rowId, glTableColumnXIdx);
				Object dataY = glTable.getValue(rowId, glTableColumnYIdx);
				
				// ignore incomplete data
		        if (dataX == null || dataY == null)
		        	continue;
		        
		        // attempt case
		        Number x;
		        Number y;
		        try {
					x = (Number)glTable.getValue(rowId, glTableColumnXIdx);
				} catch (ClassCastException e) {
		        	messages.errorUser("wrong parameter: the column with index "+glTableColumnXIdx+" does not contains numbers", getClass());
		        	throw new WrongParametersException("wrong parameter: the column with index "+glTableColumnXIdx+" does not contains numbers");
		        	// TODO error !
		        }
		        try {
					y = (Number)glTable.getValue(rowId, glTableColumnYIdx);
		        } catch (ClassCastException e) {
		        	messages.errorUser("wrong parameter: the column with index "+glTableColumnYIdx+" does not contains numbers", getClass());
		        	throw new WrongParametersException("wrong parameter: the column with index "+glTableColumnYIdx+" does not contains numbers");
		        }
		        
		        serie.add(x, y);
		        
		    }
			
		} finally {
			serie.setNotify(true);
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
		
		loadDataFromParameters(viewAlgoInstance);
		
		loadDataFromTable();
		
		//chart.getXYPlot().
		
		
	}
	

	@Override
	public void createPartControl(Composite parent) {
		
		messages.traceTech("init the form...", getClass());
		
		toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new FillLayout());
		form = toolkit.createScrolledForm(parent);
		Layout layout = new RowLayout(SWT.VERTICAL);
		form.getBody().setLayout(layout);

		messages.traceTech("init the jfreechart dataset...", getClass());
		
		XYDataset dataset = null;
		{
			XYSeriesCollection seriesAll = new XYSeriesCollection();
		 
			serie = new XYSeries("data");
		    
			seriesAll.addSeries(serie);
			
			dataset = seriesAll;
		}
		
		
		messages.traceTech("init the jfreechart chart...", getClass());

		chart = ChartFactory.createScatterPlot(
				chartTitle, 
				chartXLabel, 
				chartYLabel, 
				dataset
				);
		chart.setAntiAlias(true);
		
		messages.traceTech("init the chart composite...", getClass());

		final int preferedWidth = 900;
		final int preferedHeight = 500;
	/*	
		compositeChart = new ChartComposite(
				form.getBody(), 
				SWT.NONE, 
				chart,
				preferedWidth,
				preferedHeight,
				100,
				100,
				preferedWidth*2,
				preferedHeight*2,
				true, // use a buffer
				true, // allosw properties
				true, // allow save
				true, // allow print
				true, // allow zoom
				false // allow tooltips
				);		
				*/
		compositeChart = new ChartComposite(
				form.getBody(), 
				SWT.NONE, 
				chart,
				true // use a buffer
				);	
		compositeChart.setSize(preferedWidth, preferedHeight);
		compositeChart.setLayoutData(new RowData(preferedWidth, preferedHeight));
		compositeChart.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		
		
		// update display
		form.layout(true);

		// add parameters access
		getViewSite().getActionBars().getToolBarManager().add(new ShowParametersAction());  

	
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
		

		loadDataFromParameters(ai);
		
		loadDataFromTable();
		
	}
	
	@Override
	public void dispose() {
		if (compositeChart != null)
			compositeChart.dispose();
		if (form != null)
			form.dispose();
		if (toolkit != null)
			toolkit.dispose();
		super.dispose();
	}


}
