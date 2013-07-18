package genlab.core.model.doc;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.model.meta.IAlgo;
import genlab.core.model.meta.IInputOutput;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class AvailableInfo {

	public Map<String,DocAuthor> id2author = new HashMap<String,DocAuthor>(100);
	public Map<String,DocLibrary> id2library = new HashMap<String,DocLibrary>(50);
	public Map<String,DocAlgo> id2algo = new HashMap<String,DocAlgo>(300);

	public static final String EXTENSION_POINT_DOCUMENTATION_ID = "genlab.core.doc";
			
	private static final AvailableInfo singleton = new AvailableInfo();
	
	public static AvailableInfo getAvailableInfo() {
		return singleton;
	}
	
	protected DocAuthor getAuthor(String id) {
		return id2author.get(id);
	}
	
	protected DocLibrary getLibrary(String id) {
		return id2library.get(id);
	}
	
	protected DocAlgo getAlgoDoc(String id) {
		return id2algo.get(id);
	}
	
	public boolean hasAlgoDoc(String id) {
		return id2algo.containsKey(id);
	}
	
	public void declareAuthor(DocAuthor author) {
		
		if (id2author.containsKey(author.id))
			throw new WrongParametersException("duplicate author doc id: "+author.id);
		
		GLLogger.traceTech("found author "+author, getClass());
		id2author.put(author.id, author);
	}
	
	public void declareLibrary(DocLibrary l) {
		
		if (id2library.containsKey(l.id))
			throw new WrongParametersException("duplicate library doc id: "+l.id);
		
		GLLogger.traceTech("found library "+l, getClass());
		id2library.put(l.id, l);
	}
	
	public void declareAlgo(DocAlgo a) {
		
		if (id2algo.containsKey(a.algo.getId()))
			throw new WrongParametersException("duplicate algo doc id: "+a.algo.getId());
		
		GLLogger.traceTech("found doc for algo "+a.algo.getId(), getClass());
		id2algo.put(a.algo.getId(), a);
	}

	
	public void detectFromExtensions() {

		GLLogger.debugTech("detecting available docs from plugins...", getClass());
		
	    IExtensionRegistry reg = Platform.getExtensionRegistry();
	    if (reg == null) {
	    	GLLogger.warnTech("no extension registry detected; no doc can be detected", this.getClass());
	    	return;
	    }
	    
	    IConfigurationElement[] elements = reg.getConfigurationElementsFor(EXTENSION_POINT_DOCUMENTATION_ID);
	    for (IConfigurationElement e : elements) {
	    	
	    	try {
	    		
	    		if (e.getName().equals("docalgo"))
		    		decodeAlgo(e);
	    		else if (e.getName().equals("author"))
		    		decodeAuthor(e);
		    	else if (e.getName().equals("library"))
		    		decodeLibrary(e);
		    	else {
		    		GLLogger.warnTech("wrong extension content: "+e.getName()+"; this content will be ignored", getClass());
		    	}
		    	
	    	} catch (RuntimeException e2) {
	    		GLLogger.warnTech("error while processing the extension "+e.getName()+"; this content will be ignored", getClass(), e2);
	    	}
		}
	    
		GLLogger.infoTech("detected doc for "+id2author.size()+" authors and "+id2library.size()+" libraries  provided by plugins", getClass());

	}
	
	private void decodeAlgo(IConfigurationElement e) {
		
		GLLogger.traceTech("loading doc for algo "+e.getAttribute("algo_id"), getClass());
		
		DocAlgo a = new DocAlgo(
				ExistingAlgos.getExistingAlgos().getAlgoForExtensionId(e.getAttribute("algo_id")),
				getLibrary(e.getAttribute("library_id")),
				e.getAttribute("time_complexity"),
				e.getAttribute("memory_complexity"),
				e.getAttribute("implementation_notes")
				);
		a.complexityTimeBest = e.getAttribute("time_complexity_best");
		a.complexityTimeWorst = e.getAttribute("time_complexity_worst");
		
		// add children:
		for (IConfigurationElement e2 : e.getChildren()) {
			// authors
			if (e2.getName().equals("integrator_author_id")) {
				a.integrationAuthors.add(getAuthor(e2.getAttribute("id")));
			} else if (e2.getName().equals("original_author_id")) {
				a.originalAuthors.add(getAuthor(e2.getAttribute("id")));
			} else if (e2.getName().equals("more_info")) {
				String url = e2.getAttribute("url");
				if (url != null) {
					StringBuffer sb = new StringBuffer();
					sb.append(e2.getAttribute("content"));
					sb.append(": ");
					sb.append("<a href=\"");
					sb.append(url);
					sb.append("\">");
					sb.append(url);
					sb.append("</a>");
					a.moreInfo.add(sb.toString());
				} else
					a.moreInfo.add(e2.getAttribute("content"));
			} else if(e2.getName().equals("known_limitation")) {
				a.knownLimitations.add(e2.getAttribute("content"));
			} else {
				GLLogger.warnTech("unknown element: "+e2.getName(), getClass());
			}
		}
		
		declareAlgo(a);
	}

	private void decodeLibrary(IConfigurationElement e) {
				
		
		DocLibrary l = new DocLibrary(
				e.getAttribute("id"),
				e.getAttribute("name"), 
				e.getAttribute("url"),
				e.getAttribute("description"), 
				e.getAttribute("licence_name"),
				e.getAttribute("licence_url")
				);
		
		// add children:
		for (IConfigurationElement e2 : e.getChildren()) {
			// authors
			if (e2.getName().equals("integrator_author_id")) {
				l.integrationAuthors.add(getAuthor(e2.getAttribute("id")));
			} else if (e2.getName().equals("original_author_id")) {
				l.originalAuthors.add(getAuthor(e2.getAttribute("id")));
			} else if (e2.getName().equals("more_info")) {
				String url = e2.getAttribute("url");
				if (url != null) {
					StringBuffer sb = new StringBuffer();
					sb.append(e2.getAttribute("content"));
					sb.append(": ");
					sb.append("<a href=\"");
					sb.append(url);
					sb.append("\">");
					sb.append(url);
					sb.append("</a>");
					l.moreInfo.add(sb.toString());
				} else
					l.moreInfo.add(e2.getAttribute("content"));
			} else {
				GLLogger.warnTech("unknown element: "+e2.getName(), getClass());
			}
		}
		
		declareLibrary(l);
		
	}

	private DocAuthor decodeAuthor(IConfigurationElement e) {
		
		DocAuthor a = new DocAuthor(
				e.getAttribute("id"), 
				e.getAttribute("name"), 
				e.getAttribute("email"), 
				e.getAttribute("webpage")
				);
		
		declareAuthor(a);
		
		return a;
	}

	private AvailableInfo() {
		
	}
	
	public static void generateHtmlDoc(DocAuthor author, StringBuffer sb) {
		
		
		sb.append(author.name);

		if (author.webpage != null || author.email != null) {

			sb.append(" (");
			
			if (author.webpage != null)
				sb.append("<a href=\"").append(author.webpage).append("\">").append("webpage").append("</a> ");
				
			if (author.email != null)
				sb.append("<a href=\"mailto:").append(author.email).append("\">").append("email").append("</a>");

			sb.append(")");
		}
				
	}
	
	public static void generateHtmlDoc(List<DocAuthor> authors, StringBuffer sb) {
		
		// display each author
		for (int i=0; i<authors.size(); i++) {
			
			DocAuthor author = authors.get(i);
			generateHtmlDoc(author, sb);
			
			if (i==authors.size()-2 && authors.size()>1)
				sb.append(" and ");
			else if ((i > 0) && (i< authors.size()-2))
				sb.append(", ");
			
		}
	}
	
	public static void generateHtmlDoc(DocLibrary library, StringBuffer sb) {
		
		sb.append("<b><i>");
		sb.append(library.name);
		sb.append("</i></b>");
		
		if (library.url != null) {
			sb.append(" (<a href=\"").append(library.url).append("\">").append(library.url).append("</a>)");
		}
		sb.append(". ").append(library.name);
		sb.append(" is developed by ");
		generateHtmlDoc(library.originalAuthors, sb);
		sb.append(". ");
		
		if (library.licenseName != null) {
			sb.append("It is distributed under the license: ");
			sb.append("<a href=\"").append(library.licenseUrl).append("\">");
			sb.append(library.licenseName);
			sb.append("</a>.");
				
		}
		
		sb.append("It was integrated into genlab by ");
		generateHtmlDoc(library.integrationAuthors, sb);
		
	}
	
	private static final int MAX_CACHED_DOCS = 20;
	
	private LinkedHashMap<String,String> algoId2cachedDoc = new LinkedHashMap<String, String>(MAX_CACHED_DOCS, 0.75f, true) {
        
		protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
			  return size() > MAX_CACHED_DOCS;
        }

	};
	
	
	public String generateHtmlDoc(String algoId) {
		
		String res = algoId2cachedDoc.get(algoId);
		if (res == null) {
			res = generateHtmlDoc(getAlgoDoc(algoId));
			algoId2cachedDoc.put(algoId, res);
		}
		
		return res;
	}

		
	protected static String textOrUnknown(String txt) {
		if (txt == null)
			return "<i>unknown</i>";
		else 
			return txt;
	}
	
	
	public static void generateHtmlHeader(StringBuffer sb) {
		
		sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		
		sb.append("<head>");
		sb.append("<style type=\"text/css\">");
		sb.append("body { style='font-family:Tahoma; font-size:10pt;' }");
		sb.append("</style>");
		sb.append("</head>");
		
		
		sb.append("<body>");
		
		
	}
	
	public static void generateHtmlFooter(StringBuffer sb) {
		sb.append("</body>");
		sb.append("</html>");
	}
	
	public static String generateHtmlDoc(DocAlgo algo) {
		
		StringBuffer sb = new StringBuffer();
		
		generateHtmlHeader(sb);
		
		sb.append("<h1>").append(algo.algo.getName()).append("</h1>\n\n");
		
		// brief description
		sb.append("<p>").append(algo.algo.getDescription()).append("</p>\n\n");

		// usage
		sb.append("<p>");
		sb.append("takes as inputs:");
		if (!algo.algo.getInputs().isEmpty()) {
			sb.append("<ul>\n");
			for (IInputOutput<?> input : algo.algo.getInputs()) {
				sb.append("<li>");
				sb.append(input.getName());
				sb.append(" [").append(input.getType()).append("] ");
				sb.append(": ");
				sb.append(input.getDesc());
				sb.append("</li>");
			}	
			sb.append("</ul>\n");
		}
		sb.append("</p>\n\n");
		sb.append("<p>");
		sb.append("takes as outputs:");
		if (!algo.algo.getOuputs().isEmpty()) {
			sb.append("<ul>\n");
			for (IInputOutput<?> output : algo.algo.getOuputs()) {
				sb.append("<li>");
				sb.append(output.getName());
				sb.append(" [").append(output.getType()).append("] ");
				sb.append(": ");
				sb.append(output.getDesc());
				sb.append("</li>");
			}	
			sb.append("</ul>\n");
		}
		sb.append("</p>\n\n");
		
		// TODO parameters
		
		// source and reliability
		sb.append("<h2>").append("source").append("</h2>\n");
		
		// library ?
		sb.append("<p>");
		sb.append("This algorithm comes from the third-party library: ");
		generateHtmlDoc(algo.library, sb);
		sb.append("</p>");
		
		sb.append("<p>");
		if (!algo.originalAuthors.isEmpty()) {
			sb.append("This algorithm was implemented by ");
			generateHtmlDoc(algo.originalAuthors, sb);
			sb.append(". ");
		}
		if (!algo.integrationAuthors.isEmpty()) {
			sb.append("It was integrated into genlab by ");
			generateHtmlDoc(algo.integrationAuthors, sb);
			sb.append(".");
		}
		sb.append("</p>");
		
		
		
		// implementation
		sb.append("<h3>").append("Implementation").append("</h3>\n");

		// complexity		
		sb.append("<ul>\n");
		sb.append("<li>").append("time complexity (average)").append(": ").append(textOrUnknown(algo.complexityTime)).append("</li>\n");
		if (algo.complexityTimeBest != null)
			sb.append("<li>").append("time complexity (best case)").append(": ").append(algo.complexityTimeBest).append("</li>\n");
		if (algo.complexityTimeBest != null)
			sb.append("<li>").append("time complexity (worst case)").append(": ").append(algo.complexityTimeWorst).append("</li>\n");
		sb.append("<li>").append("memory complexity").append(":</b>").append(textOrUnknown(algo.complexityMemoryAverage)).append("</li>\n");
		sb.append("</ul>\n");
		
		if (algo.implementationNotes != null) {
			sb.append("<p>");
			sb.append(algo.implementationNotes);
			sb.append("</p>\n\n");
		}
		

		if (!algo.knownLimitations.isEmpty()) {
			sb.append("<p>");
			sb.append("known limitations: ");
			sb.append("<ul>");
			for (String s: algo.knownLimitations) {
				sb.append("<li>");
				sb.append(s);
				sb.append("</li>\n");
			}
			sb.append("</ul>");
			sb.append("</p>\n\n");
		}
		
		// more information
		if (!algo.moreInfo.isEmpty() || !algo.library.moreInfo.isEmpty()) {
			
			sb.append("<h2>").append("More information").append("</h2>\n");

			sb.append("<ul>\n");
			for (String s: algo.moreInfo) {
				sb.append("<li>").append(s).append("</li>\n");
			}
			for (String s: algo.library.moreInfo) {
				sb.append("<li>").append(s).append("</li>\n");
			}			
			sb.append("</ul>\n");
			
		}
		
		generateHtmlFooter(sb);
		
		return sb.toString();
	}

}
