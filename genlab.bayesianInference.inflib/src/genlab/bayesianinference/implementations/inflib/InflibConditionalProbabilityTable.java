package genlab.bayesianinference.implementations.inflib;

import edu.ucla.belief.CPTShell;
import edu.ucla.belief.FiniteVariable;
import edu.ucla.belief.Table;
import edu.ucla.belief.TableShell;
import edu.ucla.belief.VariableInstance;
import edu.ucla.belief.io.hugin.HuginNetImpl;
import edu.ucla.belief.io.hugin.HuginNode;
import edu.ucla.belief.io.hugin.HuginNodeImpl;
import genlab.bayesianinference.IBayesianNode;
import genlab.bayesianinference.IConditionalProbabilityTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class InflibConditionalProbabilityTable implements IConditionalProbabilityTable {
	
	/**
	 * The inflib implementation of the CPT
	 */
	protected CPTShell cpt;
	

	public InflibConditionalProbabilityTable(CPTShell cpt) {

		this.cpt = cpt;
		
	}

	@Override
	public IConditionalProbabilityTable cloneCPT() {
		
		HuginNetImpl huginNetwork = ((InflibNode)cpt.getVariable().getUserObject()).network.huginNetwork;
		HuginNodeImpl huginNodeImpl = (HuginNodeImpl) cpt.getVariable();
		
		Set in = huginNetwork.inComing(huginNodeImpl);
		FiniteVariable [] vars = new FiniteVariable[in.size()+1];
		int i=0;
		for (Object f : in) {
			vars[i] = (FiniteVariable)f;
			i++;
		}
		
		//final IConditionalProbabilityTable old = currentNode.getCPT();
		final CPTShell oldCPTSHell = huginNodeImpl.getCPTShell();
					
		vars[in.size()] = (FiniteVariable)huginNodeImpl;
		Table novelTable = new Table(vars);
		for (int id=0; id<novelTable.getCPLength(); id++) {
			double prev = oldCPTSHell.getCP(id);
			novelTable.setCP(id, prev);
		}
		/*
		novelTable.fill(1);
		try {
			novelTable.normalize();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		return new InflibConditionalProbabilityTable(new TableShell(novelTable));
	}

	@Override
	public void replaceVariables(Map<IBayesianNode, IBayesianNode> old2new) {
		
		Map<HuginNode,HuginNode> map3 = new HashMap<HuginNode, HuginNode>(old2new.size());
		for (Entry<IBayesianNode,IBayesianNode> entry : old2new.entrySet()) {
			map3.put(
					((InflibNode)entry.getKey()).huginNode,
					((InflibNode)entry.getValue()).huginNode
					);
		}
		cpt.replaceVariables(map3, true);
		
	}


	@Override
	public int getCardinality() {
		return cpt.getCPT().index().size();
	}


	@Override
	public int getCellIdxForParents(int[] parentLocalIdx2domainIdx) {		
		return cpt.index().index(parentLocalIdx2domainIdx);
	}


	@Override
	public int getLocalParentIndex(IBayesianNode parent) {
		return cpt.getCPT().index().variableIndex( ((InflibNode) parent).huginNode);
	}


	@Override
	public IBayesianNode getParent(int localIdx) {
		return (IBayesianNode) cpt.getCPT().index().variable(localIdx).getUserObject();
	}


	@Override
	public IBayesianNode[] getParents() {
		IBayesianNode[] res = new IBayesianNode[cpt.getCPT().index().getParents().length];
		for (int i=0; i<res.length; i++) {
			res[i] = getParent(i);
		}
		return res;
	}


	@Override
	public double getProbabilityForCellIdx(int cellIdx) {
		// table.getCPScaled(idx);
		return cpt.getCPScaled(cellIdx);
	}


	@Override
	public double getProbabilityForParents(int[] parentLocalIdx2domainIdx) {
		return getProbabilityForCellIdx(
				getCellIdxForParents(parentLocalIdx2domainIdx)
				);
	}

	@Override
	public IBayesianNode getVariable() {
		return (IBayesianNode) cpt.getVariable().getUserObject();
	}


	@Override
	public void setProbabilityForCellIdx(int[] parentLocalIdx2domainIdx, double proba) {
		setProbabilityForCellIdx(
				getCellIdxForParents(parentLocalIdx2domainIdx), 
				proba
				);
	}


	@Override
	public void setProbabilityForCellIdx(int cellIdx, double proba) {
		cpt.getCPT().setCP(cellIdx, proba);
	}

	@Override
	public Map<IBayesianNode, Integer> getValuesForCellIdx(int cellIdx) {
		
		Map<IBayesianNode, Integer> map = new HashMap<IBayesianNode, Integer>(
												cpt.getCPT().index().getParents().length+1
												);
		
		
		VariableInstance[] parentInstances = cpt.getCPTParameter(cellIdx).getParentInstances();
		for (int i=0; i<parentInstances.length; i++) {
			map.put(
					(IBayesianNode) parentInstances[i].getVariable().getUserObject(),
					parentInstances[i].getIndex()		
					);
			
		}
		
		map.put(
				(IBayesianNode) cpt.getCPTParameter(cellIdx).getJointInstance().getVariable().getUserObject(),
				cpt.getCPTParameter(cellIdx).getJointInstance().getIndex()
				);
		
		return map;
	}
	

	@Override
	public void normalize() {
		try {
			cpt.normalize();
		} catch (Exception e) {
			throw new RuntimeException("error during normalizing", e);
		}
	}

	@Override
	public void fill(double value) {
		cpt.getCPT().fill(value);
	}

}
