package genlab.gui.intro;


import genlab.examples.gui.wizards.CreateExampleWizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class IntroExamplesContributor implements IGenlabIntroContributor {

	public IntroExamplesContributor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void contributeFirstSteps(FormToolkit toolkit, Composite compoFirstSteps) {
		toolkit.createLabel(compoFirstSteps, "A good way to discover GenLab is to have a look to the various examples proposed:");
		
		Hyperlink hl = toolkit.createHyperlink(
				compoFirstSteps, 
				"generate examples (TODO)", 
				SWT.WRAP
				);
		
		hl.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				Wizard wizard = new CreateExampleWizard();
				WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
			    wd.setTitle(wizard.getWindowTitle());
			    wd.open();
			}
		});
	}

}
