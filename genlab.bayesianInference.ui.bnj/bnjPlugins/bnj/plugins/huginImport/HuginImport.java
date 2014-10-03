package bnj.plugins.huginImport;
import java.io.InputStream;
import edu.ksu.cis.bnj.ver3.streams.Importer;
import edu.ksu.cis.bnj.ver3.streams.OmniFormatV1;

/*!
 * \file HuginImport.java
 * \author Jeffrey M. Barber
 */
public class HuginImport implements Importer
{
	public void load(InputStream stream, OmniFormatV1 writer)
	{
		NetParser.load(stream,writer);
	}
	
	public String getExt()
	{
		return "*.net;*.hugin";
	}
	
	public String getDesc()
	{
		return "Hugin 5.7/6";
	}
}
