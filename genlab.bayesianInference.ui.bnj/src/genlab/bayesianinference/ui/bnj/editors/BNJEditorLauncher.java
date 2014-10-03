package genlab.bayesianinference.ui.bnj.editors;

import genlab.bayesianinference.ui.bnj.Genlab2BNJ;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

// TODO finish and test integration of BNJ !
public class BNJEditorLauncher implements IEditorLauncher {

	@Override
	public void open(IPath file) {

		System.err.println("trying to open file "+file+": "+file.toOSString());
		Genlab2BNJ.openGuiEditor(file.toOSString());
		
	}

}
