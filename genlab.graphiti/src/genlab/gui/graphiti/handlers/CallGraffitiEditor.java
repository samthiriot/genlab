package genlab.gui.graphiti.handlers;

import genlab.core.usermachineinteraction.GLLogger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
/**
 * On dark features: http://5ise.quanxinquanyi.de/2012/08/06/testing-graphiti-applications-with-dark-feature-processing/
 * On transactions: http://www.eclipse.org/forums/index.php/m/630075/
 * 
 * examples: http://code.google.com/a/eclipselabs.org/p/3dsl/source/browse/trunk/org.eclipselabs.threedsl/src/org/eclipse/graphiti/examples/tutorial/handlers/CreateDiagramWithAllClassesHandler.java?spec=svn3&r=3
 * 
 * @author Samuel Thiriot
 *
 */

public class CallGraffitiEditor extends AbstractHandler {

	final String diagramTypeId = "test.graphiti.nonemf.diagram";

    //@Inject
    private IWorkspace workspace = null;
    


    
	public CallGraffitiEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		
		GLLogger.debugTech("attempting to open editor");
		
		/*
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("diagram", new XMIResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("functionnetwork", new XMIResourceFactoryImpl());
        final ResourceSetImpl resourceSet = new ResourceSetImpl();
        
         TransactionalEditingDomain editingDomain1 = TransactionUtil.getEditingDomain(resourceSet);
        if (editingDomain1 == null) {
            // Not yet existing, create one
            editingDomain1 = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
        }
        final TransactionalEditingDomain editingDomain = editingDomain1;
        
		final Diagram diagram = Graphiti.getPeCreateService().createDiagram("genlab.graphiti.diagtypes.workflow",  "first test", true);
		 
		 String editorID = DiagramEditor.DIAGRAM_EDITOR_ID;
		   
		 //ResourceSet resourceSet = new ResourceSetImpl();
		 //TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(diagram);
	
		 String providerId = "meprovider"; //GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());

		 final URI uri = URI.createFileURI((new File("/tmp/test.diagram")).getAbsolutePath());
		 
			
		// Has to be in here for afterCreateDiagram to execute

	        editingDomain.getCommandStack().execute(new Command() {
				
				@Override
				public void undo() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void redo() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public Collection<?> getResult() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public String getLabel() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Collection<?> getAffectedObjects() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void execute() {
					System.err.println("fluck");
					 // open transaction

					 Resource createdResource = editingDomain.getResourceSet().createResource(uri);
					createdResource.getContents().add(diagram);
					
					Resource modelResource = new XMLResourceImpl();
					modelResource.setURI(URI.createFileURI("/tmp/test.functionnetwork"));
					resourceSet.getResources().add(modelResource);
					try {

						modelResource.save(null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void dispose() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public Command chain(Command command) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean canUndo() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean canExecute() {
					// TODO Auto-generated method stub
					return false;
				}
			});
			*/
		
		/*
		IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager()
				.createDiagramTypeProvider(diagram,
						"");
*/

		 // EcoreUtil.getURI(diagram)
		 
		 /*
		 DiagramEditorInput editorInput = new DiagramEditorInput(
				 uri, 
				 providerId
				 );
		 
		 
		//ResourceSet resourceSet = editorInput.getDiagram().eResource().getResourceSet();
		
		 try {
			 HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().openEditor(editorInput, editorID);
		 } catch (PartInitException e) {
			 e.printStackTrace();
		  // TODO let the user know
		  return false;
		 }
		
		 return null;
		 */
		
		final String containerName = "container";
        final String fileName = "toto.txt";
        final ResourceSet rs = new ResourceSetImpl();
        final URI uri = URI.createFileURI("/tmp/diag.diagram");
        		//URI.createPlatformResourceURI(containerName + "/" + fileName + ".diagram", true);
        final Resource resource = rs.createResource(uri);
        Diagram diagram = PictogramsFactory.eINSTANCE.createDiagram();
        diagram.setDiagramTypeId(diagramTypeId);
        diagram.setName(fileName);
        
        // init graph
        diagram.setGridUnit(10);
        diagram.setSnapToGrid(true);
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
            return false;
        } catch (InvocationTargetException e) {
        	e.printStackTrace();
            Throwable realException = e.getTargetException();
            MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", realException.getMessage());
    		GLLogger.errorTech("error while creating graph editor",e);

            return false;
        }
        return true;
	}

}
