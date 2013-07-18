package genlab.core.model.doc;

import java.util.LinkedList;
import java.util.List;

public class DocLibrary {

	public final String id;
	
	public final List<DocAuthor> originalAuthors = new LinkedList<DocAuthor>();
	public final List<DocAuthor> integrationAuthors = new LinkedList<DocAuthor>();
	
	public final List<String> moreInfo = new LinkedList<String>();

	public final String name;
	public final String url;
	public final String description;
	
	
	public final String licenseName;
	public final String licenseUrl;
	public final List<String> verified = new LinkedList<String>();
	
	public DocLibrary(String id, String name, String url, String description,
			String licenseName, String licenseUrl) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.description = description;
		this.licenseName = licenseName;
		this.licenseUrl = licenseUrl;
	}
	

	
	@Override
	public String toString() {
		return name;
	}
}
