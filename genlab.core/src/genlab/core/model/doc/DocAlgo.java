package genlab.core.model.doc;

import genlab.core.commons.WrongParametersException;
import genlab.core.model.meta.IAlgo;

import java.util.LinkedList;
import java.util.List;

public class DocAlgo {

	public final IAlgo algo;
	
	public final DocLibrary library;
	
	public final String complexityTime;
	public String complexityTimeWorst = null;
	public String complexityTimeBest = null;
	public final String complexityMemoryAverage;
	
	public final String implementationNotes;

	public final List<DocAuthor> originalAuthors = new LinkedList<DocAuthor>();
	public final List<DocAuthor> integrationAuthors = new LinkedList<DocAuthor>();
	
	public final List<String> moreInfo = new LinkedList<String>();
	public final List<String> knownLimitations = new LinkedList<String>();

	public final List<String> verified = new LinkedList<String>();

	
	public DocAlgo(IAlgo algo, DocLibrary library, String complexityTime, String complexityMemory, String implementationNotes) {
		super();
		if (algo == null)
			throw new WrongParametersException("algo can not be null");
		if (library== null)
			throw new WrongParametersException("library can not be null");
		this.algo = algo;
		this.library = library;
		this.complexityTime = complexityTime;
		this.complexityMemoryAverage = complexityMemory;
		this.implementationNotes = implementationNotes;
	}

	
	

}
