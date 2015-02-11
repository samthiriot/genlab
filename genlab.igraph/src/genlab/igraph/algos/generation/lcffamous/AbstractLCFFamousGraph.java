package genlab.igraph.algos.generation.lcffamous;

import genlab.core.exec.IExecution;
import genlab.core.model.doc.AvailableInfo;
import genlab.core.model.doc.DocAlgo;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.ExistingAlgoCategories;
import genlab.core.model.meta.ExistingAlgos;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.igraph.algos.generation.AbstractIGraphGenerator;
import genlab.igraph.algos.generation.AbstractIGraphGeneratorExec;
import genlab.igraph.algos.generation.LCF;
import genlab.igraph.natjna.IGraphGraph;
import genlab.igraph.natjna.IGraphLibrary;
import genlab.igraph.natjna.IGraphRawLibrary;

/**
 * Registers itself without extension point
 * 
 * @author Samuel Thiriot
 *
 */
public class AbstractLCFFamousGraph extends AbstractIGraphGenerator {

	
	protected final LCF lcf;
	public final int countVertices;
			
	public AbstractLCFFamousGraph(String graphName, String description, String lcf, String urlInfo, int countVertices) {
		super(
				graphName + "(igraph)", 
				"LCF graph noted "+lcf.toString()+"; "+description,
				ExistingAlgoCategories.STATIC_GRAPHS_LCF
				);
				
		this.countVertices = countVertices;
		this.lcf = LCF.parseFromString(lcf);
		
		// declare algo 
		ExistingAlgos.getExistingAlgos().declareAlgo(this);
		// declare its doc
		DocAlgo docAlgo = new DocAlgo(
				this, 
				AvailableInfo.getAvailableInfo().id2library.get("genlab.igraph.doc.library.igraph"), 
				null, 
				null, 
				null
				);
		docAlgo.moreInfo.add("<a href=\"http://en.wikipedia.org/wiki/LCF_notation\">wikipedia page on the LCF notation</a>");
		docAlgo.moreInfo.add("<a href=\""+urlInfo+"\">description of this graph in Wolfram Alpha</a>");
		docAlgo.moreInfo.add("<a href=\"http://mathworld.wolfram.com/LCFNotation.html\">list of LCF famous graphs in Wolfram Alpha</a>");
		AvailableInfo.getAvailableInfo().declareAlgo(docAlgo);
	}
	
	@Override
	protected String constructId(String name) {
		return getClass().getCanonicalName()+"/"+name.replaceAll("[-+.^:, ]","_");
	}

	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		
		return new AbstractIGraphGeneratorExec(execution, algoInstance) {
			
			@Override
			public long getTimeout() {
				return 1000;
			}
			
			@Override
			protected IGraphGraph generateGraph(IGraphLibrary lib,
					ListOfMessages messages) {
												
				return lib.generateLCF(countVertices, lcf.shifts, lcf.count);

			}
		};
	}

	@Override
	public boolean isAvailable() {
		return IGraphRawLibrary.isAvailable;
	}
}
