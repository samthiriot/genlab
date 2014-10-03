package bnj.plugins.xmlbif;
import edu.ksu.cis.bnj.ver3.plugin.IOPlugin;

/*!
 * \file PlugIn.java
 * \author Jeffrey M. Barber
 */
public class PlugIn implements IOPlugin
{
	public int getType()
	{
		return 2;
	}
	

	public int getVersion()
	{
		return 1;
	}
	public String getClassName(int mode)
	{
		if(mode==0) return "Converter_xmlbif";
		if(mode==1) return "Converter_xmlbif";
		return "PlugIn";
	}
}
