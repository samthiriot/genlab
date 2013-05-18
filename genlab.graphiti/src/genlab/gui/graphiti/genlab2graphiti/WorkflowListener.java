package genlab.gui.graphiti.genlab2graphiti;

import genlab.basics.workflow.IWorkflowListener;
import genlab.core.algos.IGenlabWorkflow;
import genlab.core.projects.IGenlabProject;
import genlab.core.usermachineinteraction.GLLogger;

import org.eclipse.core.resources.IProject;

public class WorkflowListener implements IWorkflowListener {

	public WorkflowListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void workflowCreation(IGenlabWorkflow workflow) {

		//Genlab2GraphitiUtils.createDiagram(workflow, workflow.getProject().);
		
		GLLogger.debugTech(
				"a genlab workflow was created; will create a graphiti diagram to edit it", 
				getClass()
				);
		
		Genlab2GraphitiUtils.createDiagram(
				workflow, 
				(IProject) workflow.getProject().getAttachedObject(IGenlabProject.KEY_ECLIPSE_PROJECT)
				);
		
		
		// create diagram
		/*
		Diagram diagram = Graphiti.getPeCreateService().createDiagram(
				GraphitiDiagramTypeProvider.GRAPH_TYPE, 
				workflow.getName(), 
				true
				);
		*/
		/*
		
		// retrieve services
		IPeService peService = Graphiti.getPeService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();
		
		// retrieve resources
		final ResourceSetImpl resourceSet = new ResourceSetImpl();
	        
        TransactionalEditingDomain editingDomain1 = TransactionUtil.getEditingDomain(resourceSet);
        if (editingDomain1 == null) {
            // Not yet existing, create one
            editingDomain1 = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }
        final TransactionalEditingDomain editingDomain = editingDomain1;
        DiagramEditorInput editorInput = new DiagramEditorInput(
       		 	org.eclipse.emf.common.util.URI.createFileURI(
       		 			(new File("/tmp/test.diagram")
       		 			).getAbsolutePath()), 
       		 	GraphitiDiagramTypeProvider.GRAPH_TYPE
        		);
		 
		 try {
			 PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
					 editorInput, 
					 DiagramEditor.DIAGRAM_EDITOR_ID);
		 } catch (PartInitException e) {
			 e.printStackTrace();
			 GLLogger.errorTech("unable to create editor...",e);
		 }
		
		/* 
        diagram.setVisible(true);
        Color c1 = Graphiti.getGaService().manageColor(diagram, 249, 238, 227);
        Color c2 = Graphiti.getGaService().manageColor(diagram, IColorConstant.WHITE);
        Rectangle r = Graphiti.getGaService().createRectangle(diagram);
        r.setBackground(c2);
        r.setForeground(c1);
        r.setFilled(true);
        r.setHeight(1000);
        r.setWidth(1000);
        r.setLineStyle(LineStyle.SOLID);
        r.setLineVisible(true);
        r.setLineWidth(1);
        r.setX(0);
        r.setY(0);
        */
        
/*
        final Resource resource =  PlatformUI.getPreferenceStore().setValue(name, value) rs.createResource(uri);
        resource.getContents().add(diagram);
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    resource.save(Collections.emptyMap());
                    final IFile file = workspace.getRoot().getFile(new Path(uri.toPlatformString(true)));
                    BasicNewResourceWizard.selectAndReveal(file, PlatformUI.getWorkbench().getActiveWorkbenchWindow());

                    Display.getDefault().getActiveShell().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                            try {
                                IDE.openEditor(page, file, true);
                            } catch (final PartInitException e) {
                            	e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    monitor.done();
                }
            }
        };
       
        try {
        	PlatformUI.getWorkbench().getActiveWorkbenchWindow().run( true, false, op);
        } catch (InterruptedException e) {
        	GLLogger.errorTech("error while creating graph editor",e);
        } catch (InvocationTargetException e) {
        	e.printStackTrace();
            Throwable realException = e.getTargetException();
            MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", realException.getMessage());
    		GLLogger.errorTech("error while creating graph editor",e);

        }
         */
	}

	@Override
	public void workflowOpened(IGenlabWorkflow workflow) {

		// retrieve diagram filename
		String filename = (String) workflow.getObjectForKey(Genlab2GraphitiUtils.KEY_WORKFLOW_TO_GRAPHITI_FILE);
		
		GLLogger.debugTech("opening the graphiti diagram for this workflow, which should be there: "+filename, getClass()); 
		
	}

	@Override
	public void workflowSaving(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub

	}

	@Override
	public void workflowChanged(IGenlabWorkflow workflow) {
		// TODO Auto-generated method stub
		
		// TODO update graphiti when the workflow changes !
	}

}
