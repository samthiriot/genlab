package bnj.plugins.xmlbif;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import edu.ksu.cis.bnj.ver3.streams.Exporter;
import edu.ksu.cis.bnj.ver3.streams.Importer;
import edu.ksu.cis.bnj.ver3.streams.OmniFormatV1;
import javax.xml.parsers.*;
/**
 * file: Converter_xmlbif.java
 * 
 * @author Jeffrey M. Barber
 */
public class Converter_xmlbif implements OmniFormatV1, Exporter, Importer
{
	public OmniFormatV1 getStream1()
	{
		return this;
	}
	private OmniFormatV1	_Writer;
	private int				bn_cnt;
	private int				bnode_cnt;
	private HashMap			_nodenames;
	public void load(InputStream stream, OmniFormatV1 writer)
	{
		_Writer = writer;
		_Writer.Start();
		bn_cnt = 0;
		bnode_cnt = 0;
		_nodenames = new HashMap();
		Document doc;
		DocumentBuilder parser;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		//Parse the document
		try
		{
			parser = factory.newDocumentBuilder();
			doc = parser.parse(stream);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		visitDocument(doc);
		System.gc();
	}
	public void visitDocument(Node parent)
	{
		NodeList l = parent.getChildNodes();
		if (l == null) throw new RuntimeException("Unexpected end of document!");
		int max = l.getLength();
		for (int i = 0; i < max; i++)
		{
			Node node = l.item(i);
			switch (node.getNodeType())
			{
				case Node.ELEMENT_NODE:
					String name = node.getNodeName();
					if (name.equals("BIF"))
					{ //$NON-NLS-1$
						NamedNodeMap attrs = node.getAttributes();
						if (attrs != null)
						{
							int amax = attrs.getLength();
							for (int j = 0; j < amax; j++)
							{
								Node attr = attrs.item(j);
								String aname = attr.getNodeName().toUpperCase();
								if (aname.equals("VERSION"))
								{ //$NON-NLS-1$
									//rowFirst = true;
									try
									{
										int ver = (int) (Double.parseDouble(attr.getNodeValue()) * 100);
										if (ver != 30)
										{
											System.out.println("version " + ver + " is not supported");
										}
									}
									catch (Exception exx)
									{}
								}
								else
								{
									System.out.println("property:" + aname + " not handled");
								}
							}
						}
						visitDocument(node);
					}
					else if (name.equals("NETWORK"))
					{ //$NON-NLS-1$
						_Writer.CreateBeliefNetwork(bn_cnt);
						visitModel(node);
						bn_cnt++;
					}
					else
						throw new RuntimeException("Unhandled element " + name);
					break;
				case Node.DOCUMENT_TYPE_NODE:
				case Node.DOCUMENT_NODE:
				case Node.COMMENT_NODE:
				case Node.TEXT_NODE:
					//Ignore this
					break;
				default:
			//if (Settings.DEBUG) System.out.println("Unhandled node " +
			// node.getNodeName());
			}
		}
	}
	public void visitModel(Node parent)
	{
		NodeList l = parent.getChildNodes();
		if (l == null) throw new RuntimeException("Unexpected end of document!");
		int max = l.getLength();
		// Split into two loops so that it can handle forward reference
		for (int i = 0; i < max; i++)
		{
			Node node = l.item(i);
			switch (node.getNodeType())
			{
				case Node.ELEMENT_NODE:
					String name = node.getNodeName();
					if (name.equals("NAME"))
					{
						_Writer.SetBeliefNetworkName(bn_cnt, getElementValue(node));
					}
					else if (name.equals("PRM_CLASS"))
					{}
					else if (name.equals("VARIABLE"))
					{
						_Writer.BeginBeliefNode(bnode_cnt);
						visitVariable(node);
						_Writer.EndBeliefNode();
						bnode_cnt++;
					}
					break;
				case Node.DOCUMENT_TYPE_NODE:
				case Node.DOCUMENT_NODE:
				case Node.COMMENT_NODE:
				case Node.TEXT_NODE:
					//Ignore this
					break;
				default:
			//if (Settings.DEBUG) System.out.println( "Unhandled node " +
			// node.getNodeName());
			}
		}
		for (int i = 0; i < max; i++)
		{
			Node node = l.item(i);
			switch (node.getNodeType())
			{
				case Node.ELEMENT_NODE:
					String name = node.getNodeName();
					if (name.equals("DEFINITION") || name.equals("PROBABILITY"))
					{
						visitDefinition(node);
					}
					break;
				case Node.DOCUMENT_TYPE_NODE:
				case Node.DOCUMENT_NODE:
				case Node.COMMENT_NODE:
				case Node.TEXT_NODE:
					//Ignore this
					break;
				default:
			//if (Settings.DEBUG) System.out.println( "Unhandled node " +
			// node.getNodeName());
			}
		}
	}
	protected void visitVariable(Node parent)
	{
		NodeList l = parent.getChildNodes();
		int max;
		String propType = "nature"; //$NON-NLS-1$
		NamedNodeMap attrs = parent.getAttributes();
		if (attrs != null)
		{
			max = attrs.getLength();
			for (int i = 0; i < max; i++)
			{
				Node attr = attrs.item(i);
				String name = attr.getNodeName();
				String value = attr.getNodeValue();
				if (name.equals("TYPE"))
				{ //$NON-NLS-1$
					propType = value;
					if (value.equals("decision"))
					{ //$NON-NLS-1$
						//System.out.println("decision not supported");
						_Writer.SetType("decision");
					}
					else if (value.equals("utility"))
					{ //$NON-NLS-1$
						//System.out.println("utility not supported");
						_Writer.SetType("utility");
					} // otherwise it's just "nature"
				}
				else
				{
					System.out.println("Unhandled variable property attribute " + name);
				}
			}
		}
		max = l.getLength();
		for (int i = 0; i < max; i++)
		{
			Node node = l.item(i);
			switch (node.getNodeType())
			{
				case Node.ELEMENT_NODE:
					String name = node.getNodeName();
					if (name.equals("NAME"))
					{ //$NON-NLS-1$
						String desc = getElementValue(node);
						_nodenames.put(desc, new Integer(bnode_cnt));
						_Writer.SetBeliefNodeName(desc);
					}
					else if (name.equals("OUTCOME") || name.equals("VALUE"))
					{
						String value = getElementValue(node);
						_Writer.BeliefNodeOutcome(value);
					}
					else if (name.equals("PROPERTY"))
					{ //$NON-NLS-1$
						String assignment = getElementValue(node);
						int eq = assignment.indexOf("=");
						String var = assignment.substring(0, eq).trim().toUpperCase();
						String val = assignment.substring(eq + 1).trim();
						if (var.equals("POSITION"))
						{
							//System.out.print("["+var+"]");
							//System.out.println("=["+val+"]");
							//System.out.println("need to parse:" + val);
							//System.out.println(left+","+cma+","+right);
							//System.out.println("need to parse:" + X + " : " +
							// Y);
							int cma = val.indexOf(",");
							int left = val.indexOf("(");
							int right = val.indexOf(")");
							String X = val.substring(left + 1, cma).trim();
							String Y = val.substring(cma + 1, right).trim();
							_Writer.SetBeliefNodePosition(Integer.parseInt(X), Integer.parseInt(Y));
						}
					}
					break;
				case Node.DOCUMENT_NODE:
				case Node.COMMENT_NODE:
				case Node.TEXT_NODE:
					//Ignore this
					break;
				default:
			//if (Settings.DEBUG) System.out.println("Unhandled node
			// "+node.getNodeName());
			}
		}
	}
	protected void visitDefinition(Node parent)
	{
		NodeList l = parent.getChildNodes();
		if (l == null) return;
		LinkedList parents = new LinkedList();
		int curNode = -1;
		String CPTString = "";
		int max = l.getLength();
		for (int i = 0; i < max; i++)
		{
			Node node = l.item(i);
			switch (node.getNodeType())
			{
				case Node.ELEMENT_NODE:
					String name = node.getNodeName();
					if (name.equals("FOR"))
					{
						String cNode = getElementValue(node);
						curNode = ((Integer) _nodenames.get(cNode)).intValue();
						//curNodeName = getElementValue(node);
					}
					else if (name.equals("GIVEN"))
					{ //$NON-NLS-1$
						parents.add(_nodenames.get(getElementValue(node)));
					}
					else if (name.equals("TABLE"))
					{ //$NON-NLS-1$
						CPTString = getElementValue(node);
					} // else if (Settings.DEBUG) System.out.println("Unhandled
					  // variable element "+name);
					break;
				case Node.DOCUMENT_NODE:
				case Node.COMMENT_NODE:
				case Node.TEXT_NODE:
					//Ignore this
					break;
				default:
			}
		}
		if (curNode >= 0)
		{
			for (Iterator i = parents.iterator(); i.hasNext();)
			{
				int p = ((Integer) i.next()).intValue();
				_Writer.Connect(p, curNode);
			}
			_Writer.BeginCPF(curNode);
			StringTokenizer tok = new StringTokenizer(CPTString);
			int maxz = tok.countTokens();
			for (int c = 0; c < maxz; c++)
			{
				String SSS = tok.nextToken();
				_Writer.ForwardFlat_CPFWriteValue(SSS);
			}
			_Writer.EndCPF();
		}
	}
	protected String getElementValue(Node parent)
	{
		NodeList l = parent.getChildNodes();
		if (l == null) return null;
		StringBuffer buf = new StringBuffer();
		int max = l.getLength();
		for (int i = 0; i < max; i++)
		{
			Node node = l.item(i);
			switch (node.getNodeType())
			{
				case Node.TEXT_NODE:
					buf.append(node.getNodeValue());
					break;
				case Node.ELEMENT_NODE:
				case Node.COMMENT_NODE:
					//Ignore this
					break;
				default:
					System.out.println("Unhandled node " + node.getNodeName());
			}
		}
		return buf.toString().trim();
	}
	Writer		w				= null;
	public int	netDepth;
	int			curBeliefNode	= 0;
	HashMap		BeliefNames;
	HashMap		AdjList;
	public void save(OutputStream os)
	{
		w = new OutputStreamWriter(os);
	}
	public void fwrite(String x)
	{
		try
		{
			w.write(x);
			w.flush();
		}
		catch (Exception e)
		{
			System.out.println("unable to write?");
		}
	}
	public void Start()
	{
		netDepth = 0;
		BeliefNames = new HashMap();
		AdjList = new HashMap();
		fwrite("<?xml version=\"1.0\" encoding=\"US-ASCII\"?>\n");
		fwrite("<!--\n");
		fwrite("Bayesian network in XMLBIF v0.3 (BayesNet Interchange Format)\n");
		fwrite("Produced by BNJ 3.0 (http://bndev.sourceforge.net/\n");
		fwrite("-->\n");
		fwrite("		<!-- DTD for the XMLBIF 0.3 format -->\n");
		fwrite("<!DOCTYPE BIF [\n");
		fwrite("<!ELEMENT BIF ( NETWORK )*>\n");
		fwrite("<!ATTLIST BIF VERSION CDATA #REQUIRED>\n");
		fwrite("<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n");
		fwrite("<!ELEMENT NAME (#PCDATA)>\n");
		fwrite("<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n");
		fwrite("\t<!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n");
		fwrite("<!ELEMENT OUTCOME (#PCDATA)>\n");
		fwrite("<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n");
		fwrite("<!ELEMENT FOR (#PCDATA)>\n");
		fwrite("<!ELEMENT GIVEN (#PCDATA)>\n");
		fwrite("<!ELEMENT TABLE (#PCDATA)>\n");
		fwrite("<!ELEMENT PROPERTY (#PCDATA)>\n");
		fwrite("]>\n");
		fwrite("<BIF VERSION=\"0.3\">\n");
	}
	public void CreateBeliefNetwork(int idx)
	{
		if (netDepth > 0)
		{
			netDepth = 0;
			;
			fwrite("</NETWORK>\n");
		}
		fwrite("<NETWORK>\n");
		netDepth = 1;
	}
	public void SetBeliefNetworkName(int idx, String name)
	{
		fwrite("<NAME>" + name + "</NAME>\n");
	}

	String internalNode;
	String nodeType;
	
	public void BeginBeliefNode(int idx)
	{
		nodeType = "nature";
		internalNode = "";
//		fwrite("\t<VARIABLE TYPE=\"nature\">\n");
		curBeliefNode = idx;
		AdjList.put(new Integer(curBeliefNode), new ArrayList());
	}
	public void SetType(String type)
	{
		if(!type.equals("chance"))
			nodeType = type;
//		System.out.println("SETTYPE NOT DONE!!");
	}	
	public void SetBeliefNodePosition(int x, int y)
	{
		internalNode += "\t\t<PROPERTY>position = (" + x + "," + y + ")</PROPERTY>\n";
		//fwrite();
	}
	public void BeliefNodeOutcome(String outcome)
	{
		internalNode+="\t\t<OUTCOME>" + outcome + "</OUTCOME>\n";
		//fwrite();
	}
	public void SetBeliefNodeName(String name)
	{
		internalNode +="\t\t<NAME>" + name + "</NAME>\n"; 
		//fwrite();
		BeliefNames.put(new Integer(curBeliefNode), name);
	}
	public void MakeContinuous(String var)
	{
		//System.out.println("MakeContinuous NOT DONE!!");
	}
	public void EndBeliefNode()
	{
		fwrite("\t<VARIABLE TYPE=\""+nodeType+"\">\n");
		fwrite(internalNode);
		fwrite("\t</VARIABLE>\n");
	}
	public void Connect(int par_idx, int chi_idx)
	{
		ArrayList adj = (ArrayList) AdjList.get(new Integer(chi_idx));
		adj.add(new Integer(par_idx));
	}
	public void BeginCPF(int idx)
	{
		ArrayList adj = (ArrayList) AdjList.get(new Integer(idx));
		String name = (String) BeliefNames.get(new Integer(idx));
		fwrite("\t<DEFINITION>\n");
		fwrite("\t\t<FOR>" + name + "</FOR>\n");
		for (Iterator it = adj.iterator(); it.hasNext();)
		{
			Integer given = (Integer) it.next();
			String gname = (String) BeliefNames.get(given);
			fwrite("\t\t<GIVEN>" + gname + "</GIVEN>\n");
		}
		fwrite("\t\t<TABLE>");
	}
	public void ForwardFlat_CPFWriteValue(String x)
	{
		fwrite(x + " ");
	}
	public void EndCPF()
	{
		fwrite("\t\t</TABLE>");
		fwrite("\t</DEFINITION>");
	}
	public int GetCPFSize()
	{
		return 0;
	}
	public void Finish()
	{
		if (netDepth > 0)
		{
			netDepth = 0;
			;
			fwrite("</NETWORK>\n");
		}
		fwrite("</BIF>\n");
		try
		{
			w.close();
		}
		catch (Exception e)
		{
		}
	}
	
	public String getExt()
	{
		return "*.xml";
	}
	
	public String getDesc()
	{
		return "XML Bayesian Network Interchange Format";
	}
}