package genlab.gui.graphiti.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

import genlab.core.persistence.GenlabPersistence;
import genlab.gui.graphiti.diagram.GraphitiDiagramTypeProvider;

/**
 * Offers eclipse expressions to test wether the graphiti diagram file does exist for 
 * a given workflow file
 * 
 * @author sam
 *
 */
public class GraphitiDiagramExistsPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "genlab.gui.graphiti.testgraphitidiagram";
			
	public static final String PROPERTY_HAS_GRAPHITI_DIAGRAM = "hasNoGraphitiDiagram";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		
		// return false if its not concerning us
		if (!PROPERTY_HAS_GRAPHITI_DIAGRAM.equals(property)) {
			return false;
		} 
		
		IFile selectedFile = (IFile)receiver;
		
		// should be a workflow file 
		if (!selectedFile.getFileExtension().equals(GenlabPersistence.EXTENSION_WORKFLOW.substring(1)))
			return false;
		
		// does a workflow diagram exist ? 
		return !selectedFile.getParent().exists(new Path(selectedFile.getName()+"."+GraphitiDiagramTypeProvider.GRAPH_EXTENSION));
		
	}

}
