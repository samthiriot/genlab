package genlab.gui.intro;


import genlab.core.model.meta.AlgoCategory;
import genlab.core.model.meta.IFlowType;
import genlab.examples.gui.wizards.CreateExampleWizard;
import genlab.gui.examples.contributors.ExistingExamples;
import genlab.gui.examples.contributors.GenlabExampleDifficulty;
import genlab.gui.examples.contributors.IGenlabExample;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * Contributes the "Intro" view with links to create examples easily 
 * @author Samuel Thiriot
 *
 */
public class IntroExamplesContributor implements IGenlabIntroContributor {

	public IntroExamplesContributor() {
	}

	@Override
	public void contributeFirstSteps(FormToolkit toolkit, Composite compoFirstSteps) {
		toolkit.createLabel(compoFirstSteps, "A good way to discover GenLab is to have a look to the various examples proposed.\nYou might:");
				
		// propose the creation of examples by difficulty:
		Composite linksContainer = toolkit.createComposite(compoFirstSteps, SWT.NONE);
		toolkit.createLabel(linksContainer, "create all examples of difficulty: ");
		linksContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		for (final GenlabExampleDifficulty d: GenlabExampleDifficulty.values()) {
			Hyperlink hlEasy = toolkit.createHyperlink(
					linksContainer, 
					d.humanReadable, 
					SWT.WRAP
					);
			
			hlEasy.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					CreateExampleWizard wizard = new CreateExampleWizard();
					wizard.init(ExistingExamples.SINGLETON.getAvailableExamplesOfDifficulty(d));
					WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
				    wd.setTitle(wizard.getWindowTitle());
				    wd.open();
				}
			});
				
		}
		
		// propose the creation of examples by thematics:
		linksContainer = toolkit.createComposite(compoFirstSteps, SWT.NONE);
		toolkit.createLabel(linksContainer, "explore all the examples manipulating data of type: ");
		linksContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		for (final Map.Entry<IFlowType<?>,Collection<IGenlabExample>> entry : ExistingExamples.SINGLETON.builtMappingOfFlowtypesToExamples().entrySet()) {
			Hyperlink hlFlowType = toolkit.createHyperlink(
					linksContainer, 
					entry.getKey().getShortName(), 
					SWT.WRAP
					);
			hlFlowType.setToolTipText(entry.getKey().getDescription());

			hlFlowType.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					CreateExampleWizard wizard = new CreateExampleWizard();
					wizard.init(entry.getValue());
					WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
				    wd.setTitle(wizard.getWindowTitle());
				    wd.open();
				}
			});
		}
		
		linksContainer = toolkit.createComposite(compoFirstSteps, SWT.NONE);
		toolkit.createLabel(linksContainer, "examples related to algorithm categories: ");
		linksContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		for (final Map.Entry<AlgoCategory,Collection<IGenlabExample>> entry : ExistingExamples.SINGLETON.builtMappingOfCategoriesToExamples().entrySet()) {
			Hyperlink hlFlowType = toolkit.createHyperlink(
					linksContainer, 
					entry.getKey().getName(), 
					SWT.WRAP
					);
			hlFlowType.setToolTipText(entry.getKey().getDescription());
			hlFlowType.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					CreateExampleWizard wizard = new CreateExampleWizard();
					wizard.init(entry.getValue());
					WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
				    wd.setTitle(wizard.getWindowTitle());
				    wd.open();
				}
			});
		}
		
		// propose the creation of all examples
		linksContainer = toolkit.createComposite(compoFirstSteps, SWT.NONE);
		toolkit.createLabel(linksContainer, "Or ");
		linksContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		Hyperlink hl = toolkit.createHyperlink(
				linksContainer, 
				"generate only the examples of your choice", 
				SWT.WRAP
				);
		hl.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				CreateExampleWizard wizard = new CreateExampleWizard();
				wizard.init(Collections.EMPTY_LIST);
				WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
			    wd.setTitle(wizard.getWindowTitle());
			    wd.open();
			}
		});
		toolkit.createLabel(linksContainer, ", or ");
		Hyperlink hl2 = toolkit.createHyperlink(
				linksContainer, 
				"generate all the examples", 
				SWT.WRAP
				);
		hl2.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				CreateExampleWizard wizard = new CreateExampleWizard();
				wizard.init(ExistingExamples.SINGLETON.getAvailableExamples());
				WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
			    wd.setTitle(wizard.getWindowTitle());
			    wd.open();
			}
		});
		
	}

}
