package genlab.bayesianinference;

import java.util.Map;

/**
 * Contains the whole probability table that is for p(X{x1,x2} | Y{y1,y2}, Z{z1,z2,z3} ),
 * it contains 2 lines (for x1 and x2) for a total cardinality of 12 (2*2*3).
 *  
 * @author Samuel Thiriot
 *
 */
public interface IConditionalProbabilityTable {

	
	/**
	 * returns the cardinality of this 
	 * table, that is the total count of cells
	 * (in this example, 12)
	 * @return
	 */
	public int getCardinality();
	
	/**
	 * Returns the local parent index (0-based),
	 * which is only valid within this CPT.
	 * As a node, the main variable (X) has
	 * always 0 as an index.
	 * @param parentIdx
	 * @return
	 */
	public int getLocalParentIndex(IBayesianNode parent);
	
	/**
	 * Returns the parent corresponding
	 * to this local index (corresponds
	 * to getParents()[localIdx])
	 * @param localIdx
	 * @return
	 */
	public IBayesianNode getParent(int localIdx);
	
	/**
	 * Returns the main variable (here, X)
	 * @return
	 */
	public IBayesianNode getVariable();
	
	/**
	 * Returns the parents for this CPT 
	 * (for instance Y,Z). Note that
	 * idx in this table correspond to 
	 * the local id of the variable. 
	 * @return
	 */
	public IBayesianNode[] getParents();
	
	/**
	 * Returns the probability for this index.
	 * For instance to retrieve p(X=x2|Y=y1,Z=z3),
	 * ask for 1,[0,2]. 
	 * @param parentLocalIdx2domainIdx
	 * @return
	 */
	public double getProbabilityForParents(int[] parentLocalIdx2domainIdx);
	
	public void setProbabilityForCellIdx(int[] parentLocalIdx2domainIdx, double proba);
	
	public double getProbabilityForCellIdx(int cellIdx);
	
	public void setProbabilityForCellIdx(int cellIdx, double proba);
	
	/**
	 * returns the idx of the internal cell corresponding to this combination
	 * of variable values.
	 * @param domainIdx
	 * @param parentLocalIdx2domainIdx
	 * @return
	 */
	public int getCellIdxForParents(int[] parentLocalIdx2domainIdx);
	
	/**
	 * Returns the indexes for parents' domains for this 
	 * cell. 
	 * @param cellIdx
	 * @return
	 */
	public Map<IBayesianNode,Integer> getValuesForCellIdx(int cellIdx);
	
	
	public void replaceVariables(Map<IBayesianNode,IBayesianNode> old2new);
	
	public IConditionalProbabilityTable cloneCPT();
	
	/**
	 * Normalizes all the probabilities
	 */
	public void normalize();

	public void fill(double value);
}
