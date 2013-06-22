package genlab.gui.views;

import java.util.HashMap;
import java.util.Map;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.Parameter;
import genlab.core.projects.GenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

/**
 * This view tunes the parameters of... something that has parameters, 
 * like an algo instance
 * 
 * @author Samuel Thiriot
 */ 
public class ParametersView extends ViewPart implements IPropertyChangeListener, SelectionListener {

	public static final String PROPERTY_PROJECT_ID = "targetProject";
	public static final String PROPERTY_WORKFLOW_ID = "targetWorkflow";
	public static final String PROPERTY_ALGOINSTANCE_ID = "targetAlgoInstance";
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private IAlgoInstance algo = null;
	
	private Map<Widget, Parameter<?>> widget2param = new HashMap<Widget, Parameter<?>>();
	
	public ParametersView() {
		
		// listen for changes in our properties;
		// we will load data as soon as everything will be defined
		addPartPropertyListener(this);
		
	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		showBusy(true);
		
		// if all properties are OK, then we load parameters
		
		try {
			// retrieve params
			final String projectId = this.getPartProperty(PROPERTY_PROJECT_ID);
			if (projectId == null)
				return;
			final String workflowId = this.getPartProperty(PROPERTY_WORKFLOW_ID);
			if (workflowId == null)
				return;
			final String algoId = this.getPartProperty(PROPERTY_ALGOINSTANCE_ID);
			if (algoId == null)
				return;
			
			// load corresponding algo
			GenlabProject project = GenlabProject.getProject(projectId);
			if (project == null) {
				GLLogger.errorTech("unable to find the project with id "+projectId+"; will not open the corresponding view", getClass());
				return;
			}
			IGenlabWorkflowInstance workflow = project.getWorkflowForId(workflowId);
			if (workflow == null) {
				GLLogger.errorTech("unable to find the workflow with id "+workflowId+"; will not open the corresponding view", getClass());
				return;
			}
			algo = workflow.getAlgoInstanceForId(algoId);
			if (algo == null) {
				GLLogger.errorTech("unable to find the algo with id "+algoId+"; will not open the corresponding view", getClass());
				return;
			}
			
			GLLogger.debugTech("displaying parameters for algo instance "+algo.getId(), getClass());
			
			loadData();
		} finally {
			showBusy(false);
			
		}
	}
	
	protected void loadData() {
		
		if (toolkit == null) 
			return;
		
		// title
		form.setText("Parameters for algo "+algo.getName());

		// state
		showBusy(true);
		form.setRedraw(false);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		form.getBody().setLayout(layout);

		for (Parameter<?> param : algo.getAlgo().getParameters()) {
			
			Object value = algo.getValueForParameter(param.getId());
			
			Label label = new Label(form.getBody(), SWT.NULL);
			label.setText(param.getName()+":");
			label.setBackground(form.getBackground());
			
			Widget createdWidget = null;
			
			if (param instanceof IntParameter) {
				IntParameter p = (IntParameter)param;
				Spinner sp = new Spinner(form.getBody(), SWT.NONE);
				if (p.getMinValue() != null)
					sp.setMinimum(p.getMinValue());
				if (p.getMaxValue() != null)
					sp.setMaximum(p.getMaxValue());
				if (p.getStep() != null)
					sp.setIncrement(p.getStep());

				if (value == null)
					sp.setSelection(p.getDefaultValue());
				else
					sp.setSelection((Integer)value);
				
				
				createdWidget = sp;
				sp.addSelectionListener(this);
				
			} else if (param instanceof DoubleParameter) {
				
				DoubleParameter p = (DoubleParameter)param;
				Spinner sp = new Spinner(form.getBody(), SWT.NONE);
				
				sp.setDigits(p.getPrecision());

				// TODO check this behavior
				if (p.getMinValue() != null)
					sp.setMinimum((int)Math.floor(p.getMinValue()*Math.pow(10, p.getPrecision())));
				if (p.getMaxValue() != null)
					sp.setMinimum((int)Math.ceil(p.getMaxValue()*Math.pow(10, p.getPrecision())));
				if (p.getStep() != null)
					sp.setMinimum((int)Math.round(p.getStep()*Math.pow(10, p.getPrecision())));

				if (value == null)
					sp.setSelection((int)Math.round(p.getDefaultValue()*Math.pow(10, p.getPrecision())));
				else
					sp.setSelection((int)Math.round(((Double)value)*Math.pow(10, p.getPrecision())));
				
				createdWidget = sp;
				
				sp.addSelectionListener(this);
			} else {

				GLLogger.errorTech("unable to manage parameter type "+param.getClass().getCanonicalName()+"; the parameter "+param.getName()+" will not be displayed...", getClass());
				
			}
			
			widget2param.put(createdWidget, param);
		}
		
		form.layout(true);
		
		// end / state
		
		form.setRedraw(true);
		showBusy(false);

	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	@Override
	public void dispose() {
		form.dispose();
		toolkit.dispose();
		super.dispose();
	}


	@Override
	public void widgetSelected(SelectionEvent e) {
		
		Parameter<?> param = widget2param.get(e.widget);
		
		if (param == null) {
			GLLogger.warnTech("received a strange event which will be ignored: "+e, getClass());
			return;
		}
		
		// TODO depends of the type !
		if (param instanceof IntParameter) {
			Integer value = ((Spinner)e.widget).getSelection();
			algo.setValueForParameter(param.getId(), value);
		} else if (param instanceof DoubleParameter) {
			Double value = ((double)((Spinner)e.widget).getSelection())/Math.pow(10, ((DoubleParameter)param).getPrecision());
			algo.setValueForParameter(param.getId(), value);		
		} else {
			throw new ProgramException("unable to deal with this type of widget: "+e.getClass());
		}
		
	}


	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}




}
