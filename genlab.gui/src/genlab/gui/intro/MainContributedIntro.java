package genlab.gui.intro;

import genlab.core.IGenlabPlugin;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.Activator;

import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.part.IntroPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * An intro of GenLab displaying the list of examples which can be loaded.
 * 
 * TODO extensions to load the contribnutors
 * 
 * @author Samuel Thiriot
 *
 */
public class MainContributedIntro extends IntroPart implements IIntroPart {

	private ScrolledForm form = null;
	
	@Override
	public void standbyStateChanged(boolean standby) {
		// TODO Auto-generated method stub
		
	}

	public static final String EXTENSION_POINT_INTRO_CONTRIBUTOR = "genlab.gui.intro.contributors";
	
	private Collection<IGenlabIntroContributor> detectedFromExtensionPoints() {
		
		Collection<IGenlabIntroContributor> res = new LinkedList<IGenlabIntroContributor>();
		
	    IExtensionRegistry reg = Platform.getExtensionRegistry();
	    if (reg == null) {
	    	return Collections.EMPTY_LIST;
	    }
	    
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(EXTENSION_POINT_INTRO_CONTRIBUTOR);
	    for (IConfigurationElement e : elements) {
	    	Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o == null) {
					// skip
				} else if (o instanceof IGenlabIntroContributor) {
					res.add((IGenlabIntroContributor) o);
					
				} else {
					// skip... weirdo...
				}
			} catch (Throwable e1) {
				GLLogger.errorTech("error while detecting available algorithms: error with extension point "+e.getName(), getClass(), e1);
				e1.printStackTrace();
			}
			
		}
	    
	    return res;
	}
	
	protected Composite createPluginWidget(FormToolkit toolkit, Composite parent, String name, String desc) {
		
		final Composite host = toolkit.createComposite(parent, SWT.BORDER);
		host.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		host.setLayout(new GridLayout(1, false));
		
		// name
		toolkit
			.createLabel(host, name, SWT.WRAP | SWT.BOLD)
			.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
		
		// desc
		toolkit
			.createLabel(host, desc, SWT.WRAP)
			.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
	
		
		// create examples
		
		return host;
	}
	
	protected Section addSectionPlugins(FormToolkit toolkit) {
		
		Section sectionPlugins = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE );
		sectionPlugins.setText("Plugins");
		sectionPlugins.setDescription(
				"GenLab is a modular laboratory made of many plugins. "
				+ "Each plugin brings features: algorithms that can be used in workflows, views or other tools. "
				+ "Note you might contribute by creating your own plugin to integrate your favorite library !");

		
		Composite compoPlugins = toolkit.createComposite(sectionPlugins);
		compoPlugins.setLayout(new GridLayout(1,false));
		
		// TODO propose people to develop their plugins
		toolkit
			.createLabel(compoPlugins, "Currently "+ExistingAlgos.getExistingAlgos().getAlgos().size()+" algorithms are provided by the plugins.", SWT.WRAP)
			.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		// populate
		//Table tablePlugins = toolkit.createTable(compoPlugins, SWT.BORDER);
		//GridData gdTablePlugins = new GridData(SWT.FILL, SWT.FILL, false, true);
		//tablePlugins.setLayoutData(gdTablePlugins);
		//new TableColumn(tablePlugins, SWT.NONE).setText("name");
		//new TableColumn(tablePlugins, SWT.NONE).setText("description");
		
		//tablePlugins.setLayoutData(layoutData);
		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (int i=bundles.length -1 ; i>=0; i--) {
			Bundle bundle = bundles[i];
			try {
    			String activator =  (String)bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
    			if (activator == null)
    				continue;
    			
    			Class activatorClass = bundle.loadClass(activator);
    			
    			if (IGenlabPlugin.class.isAssignableFrom(activatorClass)) {
    				
    				String name = null;
    				String desc = null;
    				{
    					Method method = activatorClass.getMethod("getName");
    					Object o = method.invoke(null);
	    				name = (String)o;
    				}
    				{
    					Method method = activatorClass.getMethod("getDescription");
    					Object o = method.invoke(null);
	    				desc = (String)o;
    				}
    				
    				createPluginWidget(toolkit, compoPlugins, name, desc);
    				
    				//TableItem item = new TableItem(tablePlugins, SWT.WRAP);
    				//item.setText(new String[] { name, desc });
    						    					
    			}
    			
    			
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				GLLogger.warnTech("the plugin "+bundle+" activator does implement IGenlabPlugin, but does not declares static methods getName and getDescription", getClass());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//tablePlugins.getColumn(0).pack();
		//tablePlugins.getColumn(1).pack();
		sectionPlugins.setClient(compoPlugins);
		
		return sectionPlugins;
	}
	


	protected Section addSectionDoc(FormToolkit toolkit) {
		
		Section sectionPlugins = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE );
		sectionPlugins.setText("Documentation");
		sectionPlugins.setDescription("You can easily retrieve more information about GenLab online");

		
		Composite compoPlugins = toolkit.createComposite(sectionPlugins);
		compoPlugins.setLayout(new GridLayout(1,false));
		
		// TODO propose people to develop their plugins
		toolkit
			.createLabel(compoPlugins, "Currently "+ExistingAlgos.getExistingAlgos().getAlgos().size()+" algorithms are provided by the plugins.", SWT.WRAP)
			.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	
		// populate
		//Table tablePlugins = toolkit.createTable(compoPlugins, SWT.BORDER);
		//GridData gdTablePlugins = new GridData(SWT.FILL, SWT.FILL, false, true);
		//tablePlugins.setLayoutData(gdTablePlugins);
		//new TableColumn(tablePlugins, SWT.NONE).setText("name");
		//new TableColumn(tablePlugins, SWT.NONE).setText("description");
		
		//tablePlugins.setLayoutData(layoutData);
		for (Bundle bundle: Activator.getDefault().getBundle().getBundleContext().getBundles()) {
			
			try {
    			String activator =  (String)bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
    			if (activator == null)
    				continue;
    			
    			Class activatorClass = bundle.loadClass(activator);
    			
    			if (IGenlabPlugin.class.isAssignableFrom(activatorClass)) {
    				
    				String name = null;
    				String desc = null;
    				{
    					Method method = activatorClass.getMethod("getName");
    					Object o = method.invoke(null);
	    				name = (String)o;
    				}
    				{
    					Method method = activatorClass.getMethod("getDescription");
    					Object o = method.invoke(null);
	    				desc = (String)o;
    				}
    				
    				createPluginWidget(toolkit, compoPlugins, name, desc);
    				
    				//TableItem item = new TableItem(tablePlugins, SWT.WRAP);
    				//item.setText(new String[] { name, desc });
    						    					
    			}
    			
    			
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				GLLogger.warnTech("the plugin "+bundle+" activator does implement IGenlabPlugin, but does not declares static methods getName and getDescription", getClass());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//tablePlugins.getColumn(0).pack();
		//tablePlugins.getColumn(1).pack();
		sectionPlugins.setClient(compoPlugins);
		
		return sectionPlugins;
	}
	
	@Override
	public void createPartControl(final Composite parent) {

		Collection<IGenlabIntroContributor> contributors = detectedFromExtensionPoints();
		final Collection<Control> toResize = new LinkedList<Control>();
		
    	// toolkit
    	FormToolkit toolkit= new FormToolkit(parent.getDisplay());
    	
    	// scrolled form
    	form = toolkit.createScrolledForm(parent);
    	form.setText("Welcome to GenLab !");
    	final ColumnLayout layout = new ColumnLayout();
    	layout.minNumColumns=2;
    	form.getBody().setLayout(layout);
    	
    	final int wHint = (form.getClientArea().width - layout.leftMargin - layout.rightMargin)/layout.minNumColumns - layout.horizontalSpacing*layout.minNumColumns;
    			
    	{
    		// general intro
    		Label l1 = toolkit.createLabel(
    				form.getBody(), 
    				"GenLab is a laboratory dedicated to the generation of synthetic populations.\n"+
    				"Here 'populations' is understood as any set of entities necessary for a simulation.\n"+
    				"In GenLab, you can define the characteristics of your population, as different types of entities which might be structured.\n"+
    				"Then, you can use the various algorithms provided to actually create this population from your available data, which can be survey data, statistical tables or Bayesian networks.\n", 
    				SWT.WRAP
    				);
    		toResize.add(l1);
    		
    	}
    	
    	// propose first steps
    	
    	{
    		Section sectionFirstSteps = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.EXPANDED); //  | Section.TWISTIE
    		sectionFirstSteps.setText("First steps");
    		sectionFirstSteps.setDescription("Discover GenLab");
    		sectionFirstSteps.layout(true);
    		
    		Composite compoFirstSteps = toolkit.createComposite(sectionFirstSteps);
    		compoFirstSteps.setLayout(new GridLayout());
    		
    		for (IGenlabIntroContributor contributor: contributors) {
    			contributor.contributeFirstSteps(toolkit, compoFirstSteps);
    		}
    		
    		compoFirstSteps.pack(true);
    		sectionFirstSteps.setClient(compoFirstSteps);
    		
    	}
    	
    	// list plugins
    	toResize.add(addSectionPlugins(toolkit));	
    	
    	// add contributors
    	/*for (IGenlabIntroContributor contributor : contributors) {
    		contributor.contributeFirstSteps(toolkit, compoFirstSteps);
    	}*/
    	
    	
    	// update data of every plugin
    	for (Control c: toResize) {
    		c.setLayoutData(new ColumnLayoutData(150));
    	}
    	
    	form.reflow(true);    	
    	parent.layout(true);

    	parent.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {

				
		    	final int wHint = (parent.getClientArea().width - layout.leftMargin - layout.rightMargin)/layout.minNumColumns - layout.horizontalSpacing*layout.minNumColumns;
		    	System.err.println(wHint);
		    	for (Control c: toResize) {
		    		((ColumnLayoutData)c.getLayoutData()).widthHint = wHint;
		    		c.pack(true);
		    	}
		    	form.getBody().layout(true);
		    	form.reflow(true);
		    	parent.layout(true);
		    	
			}
    		
    		
		});
	}

	@Override
	public void setFocus() {
		if (form != null && !form.isDisposed())
			form.forceFocus();
	}
	
	

}
