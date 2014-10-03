package genlab.bayesianinference.ui.bnj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import edu.ksu.cis.bnj.gui.GUIWindow;
import edu.ksu.cis.bnj.gui.dialogs.FeedbackDlg;
import edu.ksu.cis.bnj.gui.tools.LanguageUnicodeParser;
import edu.ksu.cis.bnj.ver3.core.BeliefNetwork;
import edu.ksu.cis.bnj.ver3.inference.approximate.sampling.BBNLifted;
import edu.ksu.cis.bnj.ver3.plugin.IOPlugInLoader;
import edu.ksu.cis.bnj.ver3.streams.Importer;
import edu.ksu.cis.bnj.ver3.streams.OmniFormatV1_Reader;
import edu.ksu.cis.util.GlobalOptions;
import genlab.core.commons.WrongParametersException;
import genlab.gui.Utils;
import genlab.gui.VisualResources;

public class Genlab2BNJ {

	private Genlab2BNJ() {
		
	}
	
	protected static Importer getImporterForFile(String filename) {
		IOPlugInLoaderForGenlab pil = IOPlugInLoaderForGenlab.SINGLETON;
		Importer IMP = pil.GetImporterByExt(pil.GetExt(filename));
		return IMP;
	}
	
	public static BeliefNetwork loadBBNGraphFromFile(String filename) {
		
		try
		{
			FileInputStream FIS = new FileInputStream(filename);
			
			Importer importer = getImporterForFile(filename);
			OmniFormatV1_Reader ofv1w = new OmniFormatV1_Reader();
			importer.load(FIS, ofv1w);
			return ofv1w.GetBeliefNetwork(0);
			
		} catch (FileNotFoundException fnfe) {
			throw new WrongParametersException("unable to find file: "+filename+": "+fnfe.getLocalizedMessage(), fnfe);
		}
		
	}
	
	public static GUIWindow openGuiEditor(String filename) {
		

		final Display display = Display.getDefault();
		final Shell splash = new Shell(display, SWT.ON_TOP);
		
		
		Image image = VisualResources.loadImageFromPlugin(
				Activator.getDefault(), 
				"imgs/splash.gif"
				);

		Label label = new Label(splash, SWT.NONE);
		label.setImage(image);
		
		final ProgressBar bar = new ProgressBar(splash, SWT.NONE);
		bar.setMaximum(100);
		label.setImage(image);

		FormLayout layout = new FormLayout();
		splash.setLayout(layout);
		/*
		FormData labelData = new FormData ();
		labelData.right = new FormAttachment (100, 0);
		labelData.bottom = new FormAttachment (100, 0);
		label.setLayoutData(labelData);
		
		FormData progressData = new FormData ();
		progressData.left = new FormAttachment (0, 5);
		progressData.right = new FormAttachment (100, -5);
		progressData.bottom = new FormAttachment (100, -5);
		bar.setLayoutData(progressData);
		*/
		splash.pack();
		Rectangle splashRect = splash.getBounds();
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		splash.setLocation(x, y);
		splash.open();
		
		
		GUIWindow window = new GUIWindow();
		
		//LanguageUnicodeParser.getInstance().parse("russian.ini");
		GlobalOptions GO = GlobalOptions.getInstance();
		String lang = GO.getString("language", "default.ini");
		if (!lang.equals("default.ini"))
		{
			LanguageUnicodeParser.getInstance().parse(lang);
		}
		BeliefNetwork bn = null;

		// will create the widgets
		//window.open(null);
		/*
		if (!filename.equals(""))
		{
			window.Open(filename);
		}
		*/

		try	{
			bn = loadBBNGraphFromFile(filename);
			window.open(bn);
			window._filename = filename;
			//window.Open(filename);
			Thread.sleep(1500);
			display.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String message = "";
			StackTraceElement[] ste = e.getStackTrace();
			message += e.getMessage() + "\n\n";
			message += e + "\n";
			for (int i = 0; i < ste.length; i++)
			{
				message += ste[i].getFileName() + "[" + ste[i].getLineNumber() + "] ~ " + ste[i].getClassName() + "::"
						+ ste[i].getMethodName() + "\n";
				//				System.out.println(e.getMessage());
			}
			System.out.println(message);
			FeedbackDlg.Send(message);
		}
		
		
		/*
		GUIWindow gui = new GUIWindow();
		gui.Open();
		BeliefNetwork bn = loadBBNGraphFromFile(filename);
		gui.open(bn);
		*/
		
		return window;
	}

}
