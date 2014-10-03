package genlab.bayesianinference.ui.bnj;

import bnj.plugins.huginImport.HuginImport;
import bnj.plugins.xmlbif.Converter_xmlbif;
import edu.ksu.cis.bnj.ver3.plugin.IOPlugInLoader;

// TODO adapt for eclipse packaged plugin: load from resources and not directories, etc.
/**
 * Instead of smart detection of plugins, we load the classes directly.
 * 
 * @author Samuel Thiriot
 *
 */
public class IOPlugInLoaderForGenlab extends IOPlugInLoader {

	public static final IOPlugInLoaderForGenlab SINGLETON = new IOPlugInLoaderForGenlab();

	
	public IOPlugInLoaderForGenlab()
	{
		super();

		// add from plugins
		_importers.add(new HuginImport());
		_importers.add(new Converter_xmlbif());
		_exporters.add(new Converter_xmlbif());
		
	}
	
}
