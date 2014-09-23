package genlab.populations.bo;

import genlab.core.commons.NotImplementedException;
import genlab.core.model.meta.basics.flowtypes.IGenlabTable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides an access of a collection of agents as a GenLab table: 
 * each attribute of the agent becomes a column, each agent a line
 * 
 * @author Samuel Thiriot
 *
 */
public final class AccessAgentsCollectionAsTable implements IGenlabTable {

	private final IAgentType agentType;
	private final List<IAgent> agents;
	
	public AccessAgentsCollectionAsTable(IAgentType agentType, List<IAgent> agents) {
		this.agentType = agentType;
		this.agents = agents;
	}

	@Override
	public boolean isEmpty() {
		return agents.isEmpty();
	}

	@Override
	public int getColumnsCount() {
		return agentType.getAttributesCount();
	}

	@Override
	public Collection<Integer> declareColumns(Collection<String> ids) {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
		
	}

	@Override
	public int declareColumn(String id) {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
		
	}

	@Override
	public boolean containsColumn(String id) {
		return agentType.containsAttribute(id);
	}

	@Override
	public List<String> getColumnsId() {
		return agentType.getAllAttributesIds();
	}

	@Override
	public String getColumnIdForIdx(int colIdx) {
		return agentType.getAllAttributesIds().get(colIdx);
	}

	@Override
	public int addRow() {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
	}

	@Override
	public int getRowsCount() {
		return agents.size();
	}

	@Override
	public void setValue(int rowId, String columnId, Object value) {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
		
	}

	@Override
	public void setValue(int rowId, int columnIdx, Object value) {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
		
	}

	@Override
	public void setValues(int rowId, Object[] values) {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
		
	}

	@Override
	public void fillColumn(int colIndex, Object value) {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
		
	}

	@Override
	public Object getValue(int rowId, int columnIdx) {
		return agents.get(rowId).getValueForAttribute(columnIdx);
	}

	@Override
	public Object getValue(int rowId, String columnId) {
		return agents.get(rowId).getValueForAttribute(columnId);
	}

	@Override
	public Object[] getValues(int rowId) {
		return agents.get(rowId).getValuesOfAttributesAsArray();
	}

	@Override
	public boolean isColumnEmpty(String columnId) {
		for (IAgent agent: agents) {
			if (agent.getValueForAttribute(columnId) == null)
				return true;
		}
		return false;
	}

	@Override
	public boolean isColumnEmpty(int columnIdx) {
		for (IAgent agent: agents) {
			if (agent.getValueForAttribute(columnIdx) != null)
				return false;
		}
		return true;
	}

	@Override
	public Collection<String> getEmptyColumnsIds() {
		
		LinkedList<String> emptyColumnIds = new LinkedList<String>();
		for (String colId : agentType.getAllAttributesIds()) {
			if (isColumnEmpty(colId))
				emptyColumnIds.add(colId);
				
		}
		return emptyColumnIds;
	}

	@Override
	public Collection<Integer> getEmptyColumnsIndexes() {
		
		LinkedList<Integer> emptyColumnIdxs = new LinkedList<Integer>();
		for (int i=0; i<agentType.getAttributesCount(); i++) {
			if (isColumnEmpty(i))
				emptyColumnIdxs.add(i);
				
		}
		return emptyColumnIdxs;
		
	}

	@Override
	public int addRow(Object[] values) {
		throw new NotImplementedException("this table is readonly, as it is only an accessor to a collection of agents");
	}

	@Override
	public Object[] getRow(int i) {
		return agents.get(i).getValuesOfAttributesAsArray();
	}

}
