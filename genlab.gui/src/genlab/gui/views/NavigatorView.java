package genlab.gui.views;

import genlab.core.projects.IProject;
import genlab.gui.VisualResources;

import java.io.File;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * see http://www.vogella.com/articles/EclipseJFaceTree/article.html#tutorialjfacetree_label
 * 
 * @author B12772
 *
 */
public class NavigatorView extends ViewPart {

	public static final String ID = "genlab.gui.views.NavigatorView";


	public NavigatorView() {
	
		// TODO Auto-generated constructor stub
	}
	
	protected class FilesystemContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// read only !
		}

		@Override
		public Object[] getElements(Object inputElement) {
			// get children :-)
			if (inputElement instanceof IProject) {
				IProject project = (IProject)inputElement;
				return project.getFolder().listFiles();
			} 
			System.err.println("oops ! is in getElements but not a project...");
			
			
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof File) {
				File file = (File)parentElement;
				if (file.isDirectory()) {
					return file.listFiles();
				} else {
					return new Object[0];
				}
			}
			
			System.err.println("oops ! is in getChildren but not a child...");

			
			return null;
		}

		@Override
		public Object getParent(Object element) {
			
			if (element instanceof File) {
				return ((File)element).getParentFile();
			}
			System.err.println("oops ! is in getParent but not a file...");

			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return (
					(element instanceof IProject)
					||
					(
						element instanceof File &&
						((File)element).isDirectory()
						)
					);
		}
		
	}
	
	protected class MyLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof File) {
				File file = (File)element;
				return file.getName();
			} else {
				return element.toString();
			}
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof File) {
				File file = (File)element;
				
				if (file.isDirectory())
					return VisualResources.getImageDirectory();
					//return VisualResources.getImage("/icons/filesystem_folderclosed.gif");
				else {
					Image img = VisualResources.getDefaultImageForFile(file.getAbsolutePath());
					if (img == null)
						img = VisualResources.getImage("/icons/filesystem_file.gif");
					return img;
				}
				
			} else if (element instanceof IProject) {
				return VisualResources.getImage("/icons/package.gif");
			}
			return null;
		}
		
		
		
	}

	@Override
	public void createPartControl(Composite parent) {
	
		String baseFile = "/home/B12772";
		
		TreeViewer treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		final FilesystemContentProvider contentProvider = new FilesystemContentProvider();
		
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new MyLabelProvider());
		treeViewer.setInput(new IProject(){

			private File f = new File("/home/B12772");
			
			@Override
			public String getBaseDirectory() {
				return f.getAbsolutePath();
			}

			@Override
			public File getFolder() {
				return f;
			}
			
		});
		//treeViewer.expandAll();
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

	        @Override
	        public void doubleClick(DoubleClickEvent event) {
	          TreeViewer viewer = (TreeViewer) event.getViewer();
	          IStructuredSelection thisSelection = (IStructuredSelection) event
	              .getSelection();
	          Object selectedNode = thisSelection.getFirstElement();
	          if (contentProvider.hasChildren(selectedNode))
	        	  viewer.setExpandedState(selectedNode, !viewer.getExpandedState(selectedNode));
	          else
	        	  VisualResources.openFile((File)selectedNode);
	        }
	      });

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
