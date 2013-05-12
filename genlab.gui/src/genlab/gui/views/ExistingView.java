package genlab.gui.views;

import genlab.core.algos.ExistingAlgos;
import genlab.core.algos.IAlgo;
import genlab.core.algos.IInputOutput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays existing entities
 * 
 * @author Samuel Thiriot
 *
 */
public class ExistingView extends ViewPart {

	public static final String ID = "genlab.gui.views.ExistingView";

/*
	protected class ExistingElementsProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			ArrayList<Object> children;
			if (parentElement instanceof IAlgo) {
				IAlgo algo = (IAlgo)parentElement;
				children = new ArrayList<Object>();
		
				for (IInputOutput<?> input : algo.getInputs()) {
					children.add(input);
				}
				for (IInputOutput<?> output : algo.getOuputs()) {
					children.add(output);
				}
				return children.toArray();
			} else {
				// no child
				return new Object[0];
			}
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}*/
	
	public ExistingView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
	
		Tree tree = new Tree(parent, SWT.NONE);
		
		/*
		TreeViewer viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(new ExistingElementsProvider());
*/
		
		for (IAlgo algo : ExistingAlgos.getExistingAlgos().getAlgos()) {
			
			TreeItem algoItem = new TreeItem(tree, SWT.NONE);
			algoItem.setText(algo.getName());
			
			for (IInputOutput<?> input : algo.getInputs()) {
				TreeItem inputItem = new TreeItem(algoItem, SWT.NONE);
				inputItem.setText("in: "+input.getName()+" ("+input.getType().getShortName()+")");
			}
			
			for (IInputOutput<?> output : algo.getOuputs()) {
				TreeItem inputItem = new TreeItem(algoItem, SWT.NONE);
				inputItem.setText("out: "+output.getName()+" ("+output.getType().getShortName()+")");
			}
		}
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
