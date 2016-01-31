package genlab.gui.views;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.parameters.BooleanParameter;
import genlab.core.parameters.DoubleParameter;
import genlab.core.parameters.FileParameter;
import genlab.core.parameters.IntParameter;
import genlab.core.parameters.ListParameter;
import genlab.core.parameters.Parameter;
import genlab.core.parameters.RNGSeedParameter;
import genlab.core.parameters.StringBasedParameter;
import genlab.core.parameters.TextParameter;
import genlab.core.persistence.GenlabPersistence;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.parameters.ParameterCreatingWidget;
import genlab.gui.parameters.RGBParameter;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

/**
 * This view tunes the parameters of an algo instance
 * 
 * @author Samuel Thiriot
 */ 
public class ParametersView extends ViewPart implements IPropertyChangeListener, SelectionListener, ModifyListener {

	public static final String PROPERTY_WORKFLOW_FILENAME = "targetWorkflow";
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
			final String workflowfilename = this.getPartProperty(PROPERTY_WORKFLOW_FILENAME);
			if (workflowfilename == null)
				return; // all the parameters are not there yet, ignore that
			final String algoId = this.getPartProperty(PROPERTY_ALGOINSTANCE_ID);
			if (algoId == null)
				return; // idem
			
			// load corresponding algo
			IGenlabWorkflowInstance workflow = GenlabPersistence.getPersistence().getWorkflowForFilename(workflowfilename);
			if (workflow == null) {
				GLLogger.errorTech("unable to find the workflow with id "+workflowfilename+"; will not open the corresponding parameter view", getClass());
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
	
	protected void clearWidgets() {
		
		for (Control c: form.getBody().getChildren()) {
			c.dispose();
		}
		/*for (Widget w: widget2param.keySet()) {
			w.dispose();
		}*/
		widget2param.clear();
		
		form.update();
		
	}
	
	protected void loadData() {
		
		if (toolkit == null) 
			return;

		clearWidgets();
		
		// title
		form.setText("Parameters for algo "+algo.getName());

		// state
		showBusy(true);
		form.setRedraw(false);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		form.getBody().setLayout(layout);
		// for layout testing : form.getBody().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		
		for (Parameter<?> param : algo.getParameters()) {
			
			Object value = algo.getValueForParameter(param.getId());
			
			if (value == null)
				value = param.getDefaultValue();
			
			//Label label = new Label(form.getBody(), SWT.NULL);
			Text label = toolkit.createText(form.getBody(), param.getName()+":"+"\n"+param.getDesc(), SWT.READ_ONLY);

			//label.setText(param.getName()+":");
			//label.setBackground(form.getBackground());
			
			// condition should initialize the widget
			Control createdWidget = null;
			boolean updateWidgetLayout = true; // set later to false if you want a specific layout
			
			if (param instanceof ListParameter) {
				
				ListParameter p = (ListParameter)param;
				Combo combo = new Combo(form.getBody(), SWT.READ_ONLY);
				combo.setItems(p.getItemsAsArray());
				try {
					combo.select((Integer)value);
				} catch (RuntimeException e) {
					combo.select((Integer) param.getDefaultValue());
				}
				combo.setEnabled(true);

				createdWidget = combo;
				toolkit.adapt(combo);

				combo.addSelectionListener(this);
				
			} else if (param instanceof IntParameter) {
				IntParameter p = (IntParameter)param;
				Spinner sp = new Spinner(form.getBody(), SWT.NONE);
				Integer minValue = p.getMinValue();
				if (minValue == null)
					minValue = Integer.MIN_VALUE;
				sp.setMinimum(minValue);
				Integer maxValue = p.getMaxValue();
				if (maxValue == null)
					maxValue = Integer.MAX_VALUE;
				sp.setMaximum(maxValue);

				if (p.getStep() != null)
					sp.setIncrement(p.getStep());

				if (value == null)
					sp.setSelection(p.getDefaultValue());
				else
					sp.setSelection((Integer)value);
				
				createdWidget = sp;
				toolkit.adapt(sp, true, true);
				sp.addSelectionListener(this);
				
			} else if (param instanceof DoubleParameter) {
				
				DoubleParameter p = (DoubleParameter)param;
				Spinner sp = new Spinner(form.getBody(), SWT.NONE);
				
				sp.setDigits(p.getPrecision()-1);

				// TODO check this behavior
				Double minValue = p.getMinValue();
				if (minValue == null)
					minValue = -Double.MAX_VALUE;
				sp.setMinimum((int)Math.floor(minValue * Math.pow(10, sp.getDigits())));
				Double maxValue = p.getMaxValue();
				if (maxValue == null)
					maxValue = Double.MAX_VALUE;
				sp.setMaximum((int)Math.ceil(maxValue*Math.pow(10, sp.getDigits())));
				if (p.getStep() != null)
					sp.setIncrement((int)Math.round(p.getStep()*Math.pow(10, sp.getDigits())));
				else
					sp.setIncrement((int)Math.pow(10, sp.getDigits()-1));
				sp.setSelection((int)Math.round(((Double)value)*Math.pow(10, sp.getDigits())));
				
				createdWidget = sp;
				toolkit.adapt(sp, true, true);
				
				sp.addSelectionListener(this);
				
			} else if (param instanceof RNGSeedParameter) {
				
				final RNGSeedParameter p = (RNGSeedParameter)param;

				String valueStr = value == null ? "": ((Long)value).toString();

				final Text txt = toolkit.createText(form.getBody(), valueStr);
				
				txt.addVerifyListener(new VerifyListener() {  
				    @Override  
				    public void verifyText(VerifyEvent e) {
				    	String totalText = txt.getText()+e.text;
				        if (totalText == "") {
				        	e.doit = true;
				        	return;
				        }
				    	try {
				    		long v = Long.parseLong(totalText);
				    		e.doit = (v >= p.getMinValue() && v < p.getMaxValue());  
				        } catch(NumberFormatException ex){  
				            e.doit = false;  
				        }  
				    }  
				});
				
				txt.setText(valueStr);
				
				createdWidget = txt;
				txt.addModifyListener(this);
				
			} else if (param instanceof TextParameter) {
				
				updateWidgetLayout = false;
				
				TextParameter p = (TextParameter)param;
				
				Text txt = toolkit.createText(form.getBody(), value.toString(), SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
				
				GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
				gd.minimumHeight = 40;
				txt.setLayoutData(gd);
				
				//txt.setText(value.toString());
								
				createdWidget = txt;
				txt.addModifyListener(this);
				
			} else if (param instanceof StringBasedParameter<?>) {
				StringBasedParameter p = (StringBasedParameter)param;
				
				Text txt = toolkit.createText(form.getBody(), value.toString());
				
				//Text txt = new Text(form.getBody(), SWT.BORDER);
				
				txt.setText(value.toString());
								
				createdWidget = txt;
				txt.addModifyListener(this);
				
			} else if (param instanceof BooleanParameter) {
				
				BooleanParameter b = (BooleanParameter)param;
				
				Button check = toolkit.createButton(form.getBody(), "", SWT.CHECK);

				//Button check = new Button(form.getBody(), SWT.CHECK);
				//check.setBackground(form.getBackground());
				
				Boolean valueB = (Boolean)value;
				
				check.setSelection(valueB);
				
				createdWidget = check;
				
				check.addSelectionListener(this);
			} else if (param instanceof FileParameter) {
				
				final FileParameter f = (FileParameter)param;
				
				String txt = null;
				if (value == null)
					txt = "push to select a file";
				else 
					txt = ((File)value).getAbsolutePath();
				
				final Button button = toolkit.createButton(form.getBody(), txt, SWT.PUSH);

				createdWidget = button;
								
				button.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						
						File value = (File)algo.getValueForParameter(f);
						
						FileDialog dialog = new FileDialog (form.getShell(), SWT.OPEN);
						if (value != null)
							dialog.setFileName(value.getAbsolutePath());
						String res = dialog.open();
						if (res != null)
							algo.setValueForParameter(f.getId(), new File(res));
						button.setText(res);
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						
					}
				});
				
				
			} else if (param instanceof ParameterCreatingWidget) {
				
				final ParameterCreatingWidget f = (ParameterCreatingWidget)param;
				
				createdWidget = f.createWidget(
						algo, 
						value, 
						form.getBody(),
						toolkit
						);
				
				
			}  else {

				GLLogger.errorTech("unable to manage parameter type "+param.getClass().getCanonicalName()+"; the parameter "+param.getName()+" will not be displayed...", getClass());
				
			}
			// set the layout of the component
			
			if (updateWidgetLayout)
				createdWidget.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			widget2param.put(createdWidget, param);
		}
		
		{
			Label label = toolkit.createLabel(form.getBody(), "(changes are saved automatically)");

			//Label label = new Label(form.getBody(), SWT.NULL);
			//label.setText("(changes are saved automatically)");
			// TODO italic (or smaller ?)
			//label.setBackground(form.getBackground());
			GridData d = new GridData(SWT.RIGHT, SWT.TOP, true, false);
			d.horizontalSpan = layout.numColumns;
			label.setLayoutData(d);
		}
		
		form.layout(true);
		// end / state
		
		form.setRedraw(true);
		showBusy(false);

	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		parent.setLayout(new FillLayout());
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
		if (param instanceof ListParameter) {
			
			Integer idx = ((Combo)e.widget).getSelectionIndex();
			algo.setValueForParameter(param.getId(), idx);
			
		} else if (param instanceof IntParameter) {
			Integer value = ((Spinner)e.widget).getSelection();
			algo.setValueForParameter(param.getId(), value);
		} else if (param instanceof DoubleParameter) {
			Spinner spin = (Spinner)e.widget;
			// not working because of known bugs:
			// Double value = spin.getSelection()/Math.pow(10, spin.getDigits());
			Double value = null;
			try {
				value = Double.parseDouble(spin.getText());
			} catch (NumberFormatException e2) {
			}
			if (value == null) {
				try {
					value = Double.parseDouble(spin.getText().replace('.', ','));
				} catch (NumberFormatException e2) {
				}	
			}
			if (value == null) {
				try {
					value = Double.parseDouble(spin.getText().replace(',', '.'));
				} catch (NumberFormatException e2) {
				}	
			}
			if (value == null)
				return;
			algo.setValueForParameter(param.getId(), value);
		} else if (param instanceof RNGSeedParameter) {
			String value = ((Text)e.widget).getText().trim();
			algo.setValueForParameter(
					param.getId(), 
					value.isEmpty()?null:Long.parseLong(value)
					);
		} else if (param instanceof BooleanParameter) {
			Boolean value = ((Button)e.widget).getSelection();
			algo.setValueForParameter(param.getId(), value);
		} else {
			throw new ProgramException("unable to deal with this type of widget: "+e.getClass());
		}
		
	}


	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

		Parameter<?> param = widget2param.get(e.widget);
		
		if (param == null) {
			GLLogger.warnTech("received a strange event which will be ignored: "+e, getClass());
			return;
		}
	}


	@Override
	public void modifyText(ModifyEvent e) {
		
		Parameter<?> param = widget2param.get(e.widget);
		
		if (param == null) {
			GLLogger.warnTech("received a strange event which will be ignored: "+e, getClass());
			return;
		}
		
		// TODO depends of the type !
		if (param instanceof StringBasedParameter || param instanceof RNGSeedParameter) {
			String value = ((Text)e.widget).getText();
			algo.setValueForParameter(param.getId(), param.parseFromString(value));
		} else {
			throw new ProgramException("unable to deal with this type of widget: "+e.getClass());
		}
	}


	@Override
	protected void finalize() throws Throwable {
		
		if (toolkit != null)
			toolkit.dispose();
		if (form != null) {
			for (Control c: form.getBody().getChildren()) {
				c.dispose();
			}
			form.dispose();
		}
		
		super.finalize();
	}

	

}
