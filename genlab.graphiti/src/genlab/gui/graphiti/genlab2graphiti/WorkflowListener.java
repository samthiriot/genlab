package genlab.gui.graphiti.genlab2graphiti;

import genlab.core.model.instance.IAlgoInstance;
import genlab.core.model.instance.IConnection;
import genlab.core.model.instance.IGenlabWorkflowInstance;
import genlab.core.model.instance.IWorkflowContentListener;
import genlab.core.model.instance.IWorkflowListener;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;
import genlab.gui.genlab2eclipse.GenLab2eclipseUtils;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;
import genlab.gui.graphiti.diagram.GraphitiFeatureProvider;
import genlab.gui.graphiti.features.BasicLayoutDiagramFeature;
import genlab.gui.graphiti.features.BeautifyDiagramFeature;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * Listens all the genlab worflows (is registered as a listener using an extension point)
 * and update the graphiti diagram accordingly. Each time a workflow is opened/added,
 * this listener observes it. 
 * 
 * @author Samuel Thiriot
 *
 */
public class WorkflowListener implements IWorkflowListener, IWorkflowContentListener {

	public WorkflowListener() {
		lastInstance = this;
	}
	
	public static WorkflowListener lastInstance = null;

	public static class UIInfos {
		public int x, y, width, height;
		public ContainerShape containerShape;
			
	}
	
	private Map<Object, UIInfos> objectCreated2infos = new HashMap<Object, WorkflowListener.UIInfos>();
	
	public UIInfos getLastUIParameters(Object o) {
		return objectCreated2infos.get(o);
	}
	
	/**
	 * Enables the transmission of the last informations from UI
	 * like position, size. So when something is created into the
	 * workflow, we still have some insights on what the user 
	 * asked for
	 */
	public void transmitLastUIParameters(Object o, UIInfos infos) {
		objectCreated2infos.put(o, infos);
	}
	
	@Override
	public void workflowCreation(IGenlabWorkflowInstance workflow) {

		//Genlab2GraphitiUtils.createDiagram(workflow, workflow.getProject().);
		
		GLLogger.debugTech(
				"a genlab workflow was created; will create a graphiti diagram to edit it", 
				getClass()
				);
		

		// register the workflow so we can map its keys and so on
		// this call is a bit redondant but solves problems of race conditions with the GUI 
		GenLabIndependenceSolver.singleton.registerWorkflow(workflow);

		
		Genlab2GraphitiUtils.createDiagram(
				workflow, 
				GenLab2eclipseUtils.getEclipseProjectForGenlabProject(workflow.getProject())
				);
		
		workflow.addListener(this);
		
	}
	
	@Override
	public void workflowOpened(IGenlabWorkflowInstance workflow) {

		workflow.addListener(this);

		// retrieve diagram filename
		/*
		String filename = (String) workflow.getObjectForKey(Genlab2GraphitiUtils.KEY_WORKFLOW_TO_GRAPHITI_FILE);
		
		GLLogger.debugTech("opening the graphiti diagram for this workflow, which should be there: "+filename, getClass()); 
		*/
		
		//  TODO remove ? MappingObjects.register(workflow.getAbsolutePath(), workflow);
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflowInstance workflow) {

		
	}

	@Override
	public void workflowChanged(IGenlabWorkflowInstance workflow) {
		
		/*
		GLLogger.debugTech("the genlab workflow changed; will update the graphiti diagram accordingly", getClass());
		
		
		if (dfp == null) {
			GLLogger.errorTech("unable to retrieve a feature provider; unable to maintain synchronicity between genlab an graphiti", getClass());
			return;
		}
		
		Diagram diagram = (Diagram) dfp.getPictogramElementForBusinessObject(workflow);
		if (diagram == null) {
			GLLogger.errorTech("unable to retrieve the diagram; unable to maintain synchronicity between genlab an graphiti", getClass());
			return;
		}
		
		
		// ensure all the instances in workflow exist in the diagram
		for (IAlgoInstance instance: workflow.getAlgoInstances()) {
			
			PictogramElement e = dfp.getPictogramElementForBusinessObject(instance);
			if (e == null) {
				
				try {
					GLLogger.debugTech("the instance "+instance+" has no graphiti counterpart; let's create it", getClass());
					
					AddContext ctxt = new AddContext();
					ctxt.setTargetContainer(diagram);
					ctxt.setNewObject(instance);
					
					UIInfos uiInfos = objectCreated2infos.get(instance);
					if (uiInfos != null) {
						ctxt.setHeight(uiInfos.height);
						ctxt.setWidth(uiInfos.width);
						ctxt.setX(uiInfos.x);
						ctxt.setY(uiInfos.y);
						objectCreated2infos.remove(instance); // should not be of use anymore
					}
					
					dfp.addIfPossible(ctxt);
				} catch (RuntimeException e2) {
					GLLogger.errorTech("unable to create a graphical representation for algo instance: "+instance+"; the graphical representation is no more consistant with the actual data", getClass(), e2);
				}
			}
			
		}
		
		// ensure each connection is displayed
		for (Connection c : workflow.getConnections()) {
			
			PictogramElement e = dfp.getPictogramElementForBusinessObject(c);
			if (e == null) {
				
				try {
					GLLogger.debugTech("the connection "+c+" has no graphiti counterpart; let's create it", getClass());
					
					Anchor anchorFrom = (Anchor) dfp.getPictogramElementForBusinessObject(c.getFrom());
					Anchor anchorTo = (Anchor) dfp.getPictogramElementForBusinessObject(c.getTo());
					
					AddConnectionContext addContext = new AddConnectionContext(
							anchorFrom, 
							anchorTo
							);
					addContext.setNewObject(c);
					
					dfp.addIfPossible(addContext);
				} catch (RuntimeException e2) {
					GLLogger.errorTech("unable to create a graphical representation for connection: "+c+"; the graphical representation is no more consistant with the actual data", getClass(), e2);
				}
			}
		}
		
		// reverse: has each diagram object a counterpart ?
		//diagram.getChildren()
		Collection<PictogramElement> allContainedPictogramElements = Graphiti.getPeService().getAllContainedPictogramElements(
				diagram
				);
		for (PictogramElement pe : allContainedPictogramElements) {
			Property property = Graphiti.getPeService().getProperty(pe, ExternalPictogramLink.KEY_INDEPENDENT_PROPERTY);
			if (property != null) {
				List<String> values = Arrays.asList(GraphitiFeatureProvider.getValues(property.getValue()));
				
				Object bo = dfp.getBusinessObjectForPictogramElement(pe);
				
				
				
			}
		}
		
		
		// TODO update graphiti when the workflow changes !
		
		
		// let's build the list of all the objects expected
		
		// iterate accross all the elements in the diagram,
		// in order to remove things in the diag but not in the actual workflow
		
		*/
		
		// updates are catched by the other listener !
	}

	@Override
	public void workflowSaved(IGenlabWorkflowInstance workflow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectSaved(IGenlabProject project) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyConnectionAdded(IConnection c) {
		
		final GraphitiFeatureProvider dfp = GraphitiFeatureProvider.getFeatureProviderForWorkflow(c.getWorkflow());

		PictogramElement e = dfp.getPictogramElementForBusinessObject(c);
		if (e == null) {
			
			try {
				GLLogger.debugTech("the connection "+c+" has no graphiti counterpart; let's create it", getClass());
				
				Anchor anchorFrom = (Anchor) dfp.getPictogramElementForBusinessObject(c.getFrom());
				Anchor anchorTo = (Anchor) dfp.getPictogramElementForBusinessObject(c.getTo());
				
				AddConnectionContext addContext = new AddConnectionContext(
						anchorFrom, 
						anchorTo
						);
				addContext.setNewObject(c);
				
				dfp.addIfPossible(addContext);
			} catch (RuntimeException e2) {
				GLLogger.errorTech("unable to create a graphical representation for connection: "+c+"; the graphical representation is no more consistant with the actual data", getClass(), e2);
			}
		}
	}

	@Override
	public void notifyConnectionRemoved(IConnection c) {

		final GraphitiFeatureProvider dfp = GraphitiFeatureProvider.getFeatureProviderForWorkflow(c.getWorkflow());

		PictogramElement e = dfp.getPictogramElementForBusinessObject(c);
		
		Diagram diag = (Diagram) dfp.getPictogramElementForBusinessObject(c.getWorkflow());
		
		Genlab2GraphitiUtils.deleteInTransaction(diag, e);
		
		/*
		if (e != null) {
			
			try {
				GLLogger.debugTech("the connection "+c+" has a graphiti counterpart, which has to be removed", getClass());
				
				Genlab2GraphitiUtils.linkInTransaction(dfp, pictogramElement, businessObject);
				Graphiti.getPeService().deletePictogramElement(e);
				
			} catch (RuntimeException e2) {
				GLLogger.errorTech("unable to delete the graphical representation for connection: "+c+"; the graphical representation is no more consistant with the actual data", getClass(), e2);
			}
		}
		*/
	}

	@Override
	public void notifyAlgoAdded(IAlgoInstance instance) {
	
		// retrieve the right feature provider (there is one per diagram)
		// it is stored into the workflow
		
		final GraphitiFeatureProvider dfp = GraphitiFeatureProvider.getFeatureProviderForWorkflow(instance.getWorkflow());
		if (dfp == null) {
			GLLogger.errorTech("unable to retrieve in the workflow "+instance.getWorkflow()+" the feature provider; unable to maintain synchronicity between genlab an graphiti", getClass());
			return;
		}
		
		Diagram diagram = (Diagram) dfp.getPictogramElementForBusinessObject(instance.getWorkflow());
		if (diagram == null) {
			GLLogger.errorTech("unable to retrieve in "+dfp+" the diagram for instance "+instance.getWorkflow()+" ; unable to maintain synchronicity between genlab an graphiti", getClass());
			return;
		}
		
		PictogramElement e = dfp.getPictogramElementForBusinessObject(instance);
		if (e == null) {
			
			try {
				GLLogger.debugTech("the instance "+instance+" has no graphiti counterpart; let's create it", getClass());
				
				AddContext ctxt = new AddContext();
				
				if (instance.getContainer() == null) {
					// direct diagram child
					ctxt.setTargetContainer(diagram);
				} else {	
					// inside another container ?
					PictogramElement containerPE = dfp.getPictogramElementForBusinessObject(instance.getContainer());
					ctxt.setTargetContainer((ContainerShape) containerPE);
				}
				
				UIInfos uiInfos = objectCreated2infos.get(instance);
				ctxt.setNewObject(instance);
				
				if (uiInfos != null) {
					ctxt.setHeight(uiInfos.height);
					ctxt.setWidth(uiInfos.width);
					ctxt.setX(uiInfos.x);
					ctxt.setY(uiInfos.y);
					if (uiInfos.containerShape != null)
						ctxt.setTargetContainer(uiInfos.containerShape);
					objectCreated2infos.remove(instance); // should not be of use anymore
				}
				
				dfp.addIfPossible(ctxt);
			} catch (RuntimeException e2) {
				GLLogger.errorTech("unable to create a graphical representation for algo instance: "+instance+"; the graphical representation is no more consistant with the actual data", getClass(), e2);
			}
		}
		
	}

	@Override
	public void notifyAlgoRemoved(IAlgoInstance ai) {
		
		final GraphitiFeatureProvider dfp = GraphitiFeatureProvider.getFeatureProviderForWorkflow(ai.getWorkflow());

		PictogramElement e = dfp.getPictogramElementForBusinessObject(ai);
		if (e != null) {
			
			try {
				GLLogger.debugTech("the algo instance "+ai+" has a graphiti counterpart, which has to be removed", getClass());
				
				Graphiti.getPeService().deletePictogramElement(e);
				
			} catch (RuntimeException e2) {
				GLLogger.errorTech("unable to delete the graphical representation for algo instance: "+ai+"; the graphical representation is no more consistant with the actual data", getClass(), e2);
			}
		}
	}

	@Override
	public void notifyAlgoChanged(IAlgoInstance ai) {
		
		// notably called when an algo instance is renamed
		
		final GraphitiFeatureProvider dfp = GraphitiFeatureProvider.getFeatureProviderForWorkflow(ai.getWorkflow());

		PictogramElement e = dfp.getPictogramElementForBusinessObject(ai);
		if (e != null) {
			
			try {
				GLLogger.debugTech("the algo instance "+ai+" has a graphiti counterpart, which has changed", getClass());
				
				UpdateContext uc = new UpdateContext(e);
				IUpdateFeature uf = dfp.getUpdateFeature(uc);
				if (uf == null) {
					GLLogger.warnTech("unable to find an update feature for this algo instance; graphic dysplay is no more in sync", getClass());
					return;
				}
				if (!uf.canExecute(uc)) {
					GLLogger.warnTech("unable to execute an update feature for this algo instance; graphic dysplay is no more in sync", getClass());
					return;
				}
				if (uf.updateNeeded(uc).toBoolean())
					Genlab2GraphitiUtils.ExecuteInTransaction(
							uf, 
							uc, 
							(Diagram) dfp.getPictogramElementForBusinessObject(ai.getWorkflow())
							);
				
			} catch (RuntimeException e2) {
				GLLogger.errorTech("unable to update the graphical representation for algo instance: "+ai+"; the graphical representation is no more consistant with the actual data", getClass(), e2);
			}
		}
	}

	@Override
	public void workflowAutomaticallyCreatedAndFinished(
			IGenlabWorkflowInstance instance) {

		GLLogger.debugTech("a diagram was automatically created; let's layout it.", getClass());
		
		final GraphitiFeatureProvider dfp = GraphitiFeatureProvider.getFeatureProviderForWorkflow(instance);
		final Diagram diagram = (Diagram) dfp.getPictogramElementForBusinessObject(instance);
		
		// layout diagram
		BasicLayoutDiagramFeature bldf = new BasicLayoutDiagramFeature(dfp);
		Genlab2GraphitiUtils.ExecuteInTransaction(bldf, null, diagram);
		
		// beautifize it
		BeautifyDiagramFeature bdf = new BeautifyDiagramFeature(dfp);
		Genlab2GraphitiUtils.ExecuteInTransaction(bdf, null, diagram);
		
		
	}

	@Override
	public void workflowLoaded(IGenlabWorkflowInstance instance) {
		
	}

}
