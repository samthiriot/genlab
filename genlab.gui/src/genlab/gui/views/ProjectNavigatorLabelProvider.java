package genlab.gui.views;

import genlab.gui.VisualResources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * TODO IPipelinedTreeContentProvider2
 * http://devdesignandstuff.blogspot.fr/2010/10/contributing-to-eclipse-common.html
 * 
 * @author Samuel Thiriot
 *
 */
public class ProjectNavigatorLabelProvider extends LabelProvider  {

	@Override
	public String getText(Object element) {
		
		System.err.println("get text for "+element);

		if (element instanceof IResource) {
			return ((IResource)element).getName();
		}
		
		if (element instanceof IFile) {
			return ((IFile)element).getName();
		}
		
		if (element instanceof IFolder) {
			return ((IFolder)element).getName();
		}
		
		if (element instanceof org.eclipse.core.resources.IProject) {
			return ((org.eclipse.core.resources.IProject) element).getName();
		}
		
		
		return "??? "+element.toString();
		
	}

	@Override
	public Image getImage(Object element) {
		
		System.err.println("get image for "+element);
		
		// most frequent : file
		if (element instanceof IFile) {
			Image img = VisualResources.getDefaultImageForFile(((IFile)element).getFullPath().toFile().getAbsolutePath());
			if (img == null)
				img = VisualResources.getImage("/icons/filesystem_file.gif");
			return img;
		}
		
		// frequent: folder
		if (element instanceof IFolder) {
			return VisualResources.getImageDirectory();
		}
			
		// rare: project
		if (element instanceof IProject) {
			IProject prj = (IProject)element;
			if (prj.isOpen()) {
				return VisualResources.getImage("/icons/explorer_project_open.gif");
			} else
				return VisualResources.getImage("/icons/explorer_project_closed.gif");
		}
		
		return null;
	}
	
	
	
}