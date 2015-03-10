package genlab.examples.gui.wizards;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.IFlowType;
import genlab.examples.gui.creation.ExamplesCreation;
import genlab.gui.VisualResources;
import genlab.gui.examples.contributors.ExistingExamples;
import genlab.gui.examples.contributors.IGenlabExample;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

/**
 * TODO avoid stupid resizing at openintg
 * 
 * @author Samuel Thiriot
 *
 */
public class CreateExampleWizardPageExamples extends WizardPage {

	protected ScrolledComposite sc = null;
	protected Collection<Button> buttons = null;
	
	public static final int INDENT = 24;
	
	protected final Collection<IGenlabExample> listOfSelectedExamples;
	
	public CreateExampleWizardPageExamples(Collection<IGenlabExample> listOfSelectedExamples) {
		super(
				"select_example", 
				"select the example to create", 
				null
				);
		
		this.listOfSelectedExamples = listOfSelectedExamples;
	}

	@Override
	public void createControl(Composite parent) {
	
		sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
		GridData gdSc = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		sc.setLayoutData(gdSc);
		sc.setExpandHorizontal(false);
		sc.setExpandVertical(false);
		sc.setAlwaysShowScrollBars(true);
		
		
		final Composite inside = new Composite(sc, SWT.None);
		sc.setContent(inside);
		inside.setBackground(VisualResources.COLOR_BG_WIDGET);
		sc.addControlListener(new ControlAdapter() {
			
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle r = sc.getClientArea();
				Point preferedInside = inside.computeSize(r.width, SWT.DEFAULT); 
				sc.setMinSize(preferedInside);
				inside.setSize(preferedInside);
				inside.layout(true);
				
			}
			
		});
		
		
		GridLayout scLayout = new GridLayout(1, false);
		inside.setLayout(scLayout);
		
		buttons = new LinkedList<Button>();
		for (IGenlabExample ex: ExistingExamples.SINGLETON.getAvailableExamples()) {
			
			Button button = new Button(inside, SWT.CHECK);
			button.setText(ex.getName());
			button.setData(ex);
			button.setBackground(VisualResources.COLOR_BG_WIDGET);
			GridData gdButton = new GridData(SWT.FILL, SWT.TOP, true, false);
			gdButton.verticalIndent = 10;
			button.setLayoutData(gdButton);
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setPageComplete(validatePage());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					setPageComplete(validatePage());
				}
				
				
			});
			
			if (listOfSelectedExamples.contains(ex)) {
				button.setSelection(true);
			}
			
			buttons.add(button);
			
			// display difficulty
			{
				Label lblD = new Label(inside, SWT.NONE);
				lblD.setBackground(VisualResources.COLOR_BG_WIDGET);
				lblD.setText("difficulty: "+ex.getDifficulty());			
				lblD.setFont(JFaceResources.getFontRegistry().getItalic(""));
				GridData gdLabelD = new GridData(SWT.FILL, SWT.FILL, false, false);
				gdLabelD.horizontalIndent = INDENT;
				lblD.setLayoutData(gdLabelD);
			}
			
			// display description
			{
				Label lbl = new Label(inside, SWT.WRAP);
				lbl.setText(ex.getDescription());
				GridData gdLabel = new GridData(SWT.FILL, SWT.FILL, true, false);
				gdLabel.horizontalIndent = INDENT;
				lbl.setLayoutData(gdLabel);
				lbl.setBackground(VisualResources.COLOR_BG_WIDGET);
			}
			
			// display thematics
			{
				Label lbl = new Label(inside, SWT.WRAP);
				StringBuffer sb = new StringBuffer(); 
				sb.append("shows: ");
				for (AlgoCategory categ : ex.getIllustratedAlgoCategories()) {
					sb.append(categ.name);
					sb.append(", ");
				}
				for (IFlowType<?> flowtype: ex.getIllustratedFlowTypes()) {
					sb.append("type ");
					sb.append(flowtype.getShortName());
					sb.append(", ");
				}
				lbl.setText(sb.toString());
				lbl.setFont(JFaceResources.getFontRegistry().getItalic(""));
				GridData gdLabel = new GridData(SWT.FILL, SWT.FILL, true, false);
				gdLabel.horizontalIndent = INDENT;
				lbl.setLayoutData(gdLabel);
				lbl.setBackground(VisualResources.COLOR_BG_WIDGET);
			}
		}
		
		// layout
		inside.layout(true);
		sc.setBounds(parent.getClientArea());
		
		inside.setSize(inside.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setMinSize(inside.getSize());
		sc.layout(true);
		//inside.layout(true);
		//parent.layout(true);
		
		// update state of the wizard page
		setControl(sc);
	
        setPageComplete(validatePage());

	}
	
	protected boolean hasCheckedExample() {
		if (buttons == null)
			return false;
		
		for (Button b: buttons) {
			if (b.getSelection())
				return true;
		}
		
		return false;
	}
	
	 /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
    protected boolean validatePage() {
    	
    	if (!hasCheckedExample()) {
    		setErrorMessage("please select at least one example to generate");
            //setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty);
            return false;
    	}
    	
    	setErrorMessage(null);
    	
    	return true;
    	
    }
    
    public Collection<IGenlabExample> getExamplesToCreate() {
    	if (buttons == null)
			return Collections.EMPTY_LIST;
		
    	Collection<IGenlabExample> res = new LinkedList<IGenlabExample>();
    	
		for (Button b: buttons) {
			if (b.getSelection())
				res.add((IGenlabExample)b.getData());
		}
		
		return res;
    }
	

}
