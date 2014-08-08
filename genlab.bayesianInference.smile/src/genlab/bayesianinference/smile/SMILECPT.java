package genlab.bayesianinference.smile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IConditionalProbabilityTable;

public final class SMILECPT implements IConditionalProbabilityTable {

	protected final SMILENode node;
	
	public SMILECPT(SMILENode node) {
		this.node = node;
	}

	@Override
	public int getCardinality() {
		return node.network.smileNet.getNodeDefinition(node.name).length;
	}

	@Override
	public int getLocalParentIndex(IBayesianNode parent) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IBayesianNode getParent(int localIdx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBayesianNode getVariable() {
		return node;
	}

	@Override
	public IBayesianNode[] getParents() {

		List<IBayesianNode> parents = node.network.getIncoming(node);
		IBayesianNode[] res = new IBayesianNode[parents.size()];
		
		return parents.toArray(res);
		
	}

	@Override
	public int getCellIdxForParents(int[] parentLocalIdx2domainIdx) {
		
		// TODO check !
		int[] parentHandlers = node.network.smileNet.getParents(node.name);
		
		int idxRes = 0;
		
		for (int i=0; i<parentLocalIdx2domainIdx.length; i++) {
		
			idxRes += parentLocalIdx2domainIdx[i] * node.network.smileNet.getOutcomeCount(parentHandlers[i]);
			
		}
		
		return idxRes;
	}
	
	@Override
	public double getProbabilityForParents(int[] parentLocalIdx2domainIdx) {

		final int idx = getCellIdxForParents(parentLocalIdx2domainIdx);
		
		return getProbabilityForCellIdx(idx);
	}

	@Override
	public void setProbabilityForCellIdx(int[] parentLocalIdx2domainIdx, double proba) {
		
		
		// will search the right id
		final int idx = getCellIdxForParents(parentLocalIdx2domainIdx);
		
		// change
		setProbabilityForCellIdx(idx, proba);
	}

	@Override
	public double getProbabilityForCellIdx(int cellIdx) {
		return node.network.smileNet.getNodeDefinition(node.name)[cellIdx];
	}

	@Override
	public void setProbabilityForCellIdx(int cellIdx, double proba) {
		
		double[] previousProbas = node.network.smileNet.getNodeDefinition(node.name);
		
		// change
		previousProbas[cellIdx] = proba;
		node.network.smileNet.setNodeDefinition(node.name, previousProbas);
	}

	@Override
	public Map<IBayesianNode, Integer> getValuesForCellIdx(int cellIdx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replaceVariables(Map<IBayesianNode, IBayesianNode> old2new) {
		// TODO Auto-generated method stub

	}

	@Override
	public IConditionalProbabilityTable cloneCPT() {
		return new SMILECPT(node);
	}

	@Override
	public void normalize() {

		// TODO
	}

	@Override
	public void fill(double value) {

		double[] previousProbas = node.network.smileNet.getNodeDefinition(node.name);
		
		// change
		Arrays.fill(previousProbas, value);
		
		node.network.smileNet.setNodeDefinition(node.name, previousProbas);
	}

}
