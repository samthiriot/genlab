package bnj.plugins.huginImport;
import edu.ksu.cis.bnj.ver3.plugin.*;

/*!
 * \file PlugIn.java
 * \author Jeffrey M. Barber
 */
public class PlugIn implements IOPlugin
{
	public int getType()
	{
		return 0;
	}
	public String getExt()
	{
		return "net";
	}
	public String getDesc()
	{
		return "Hugin's .net 5.7 or 6.0";
	}
	public int getVersion()
	{
		return 1;
	}
	public String getClassName(int mode)
	{
		if(mode==0)
			return "HuginImport";
		
		return "PlugIn";
	}
}
