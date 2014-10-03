package genlab.bayesianinference.ui.bnj;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ksu.cis.bnj.gui.GUIWindow;

public class TestGUI {

	@Test
	public void testOpenExistingFile() {
		
		 GUIWindow window = Genlab2BNJ.openGuiEditor(TestFilePersistence.FILE_BN_WEATHER);
		 
		 assertNotNull(window);
		 assertEquals(1, window.getCounter());
	}

}
