package genlab.algog.gui.misc.algos.views;

import genlab.algog.algos.meta.GeneticExplorationAlgoConstants;
import genlab.algog.gui.misc.GenomeWidget;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.gui.algos.AbstractOpenViewAlgoExec;
import genlab.gui.views.AbstractViewOpenedByAlgo;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public final class PlotPhenotypeView extends AbstractViewOpenedByAlgo<GenlabTable> {

	public static final String VIEW_ID = "genlab.algog.gui.misc.algos.views.PlotPhenotypeView";
	
	protected GenomeWidget widget = null;
	
	private FormToolkit toolkit;
	private ScrolledForm form;
		
		
	public PlotPhenotypeView() {
		
	}
	
	@Override
	protected String getName(AbstractOpenViewAlgoExec exec) {
		return exec.getAlgoInstance().getName();
	}
	
	private float[][] createEmptySerie() {
		float[][] serie = new float[2][];
		serie[0] = new float[0];
		serie[1] = new float[0];
		return serie;
	}
	

	public void loadDataFromTable() {
		
		if (lastVersionDataToDisplay == null || lastVersionDataToDisplay.isEmpty())
			return;
		
	
		widget.setData(lastVersionDataToDisplay);
	}
		

	@Override
	public void createPartControl(Composite parent) {
		
		super.createPartControl(parent);
		
		messages.traceTech("init the form...", getClass());
		
		toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new FillLayout());
		
		form = toolkit.createScrolledForm(parent);
		Layout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		widget = new GenomeWidget(form.getBody(), SWT.NONE);
		toolkit.adapt(widget);
		widget.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
				
		// update display
		form.layout(true);
		form.reflow(true);
		form.getParent().layout(true, true);
		
		
	}

	@Override
	public void setFocus() {
		
		if (form != null)
			form.setFocus();
		
	}

	@Override
	public void dispose() {
		
		if (widget != null)
			widget.dispose();
		
		if (form != null)
			form.dispose();
		if (toolkit != null)
			toolkit.dispose();
		
		super.dispose();
	}

	@Override
	public boolean isDisposed() {
		return form != null && form.isDisposed();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}


	@Override
	protected void refreshDisplaySync() {
		loadDataFromTable();	
		
	}

}
