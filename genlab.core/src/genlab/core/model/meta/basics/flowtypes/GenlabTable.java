package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.ProgramException;
import genlab.core.commons.WrongParametersException;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Optimized for a given number of columns .
 * 
 * TODO synchronicity !!!???
 * 
 * @author Samuel Thiriot
 *
 */
public class GenlabTable implements IGenlabTable {

	private ArrayList<Object[]> content = new ArrayList<Object[]>();
	
	private List<String> columnsTitles = new LinkedList<String>();
	private Map<String,Integer> columnId2idx = new HashMap<String, Integer>(50);
	
	public GenlabTable() {
	}

	@Override
	public boolean isEmpty() {
		return content.isEmpty();
	}

	@Override
	public int getColumnsCount() {
		return columnId2idx.size();
	}
	
	protected int createNewIdForColumn(String id) {
		Integer idx = columnId2idx.size();
		columnId2idx.put(id, idx);
		columnsTitles.add(id);
		return idx;
	}

	@Override
	public int declareColumn(String id) {
		
		if (columnId2idx.containsKey(id))
			throw new WrongParametersException("this column already exists: "+id);
		
		GLLogger.debugTech("declaring new column "+id, getClass());
		
		// create the column id...
		int intId = createNewIdForColumn(id);
		
		// and resize all data
		int newSize = columnId2idx.size();
		for (int i = 0; i<content.size(); i++) {
			content.set(i, Arrays.copyOf(content.get(i), newSize));
		}
		
		return intId;
		
	}
	
	@Override
	public Collection<Integer> declareColumns(Collection<String> ids) {
		
		Collection<Integer> intIds = new LinkedList<Integer>();
		
		// check parameters
		for (String id : ids) {
			if (columnId2idx.containsKey(id))
				throw new WrongParametersException("this column already exists: "+id);
		}
		
		// create ids 
		for (String id : ids) {
			intIds.add(createNewIdForColumn(id));
		}
		
		// and resize all data
		int newSize = columnId2idx.size();
		for (int i = 0; i<content.size(); i++) {
			content.set(i, Arrays.copyOf(content.get(i), newSize));
		}

		return intIds;
	}

	@Override
	public boolean containsColumn(String id) {
		return columnId2idx.containsKey(id);
	}

	@Override
	public List<String> getColumnsId() {
		return Collections.unmodifiableList(columnsTitles);
	}

	@Override
	public String getColumnIdForIdx(int colIdx) {
		return columnsTitles.get(colIdx);
	}
	
	@Override
	public int addRow() {

		Object[] emptyRow = new Object[columnId2idx.size()];
		
		content.add(emptyRow);
		
		return content.size()-1;
	}

	@Override
	public int getRowsCount() {
		return content.size();
	}

	@Override
	public void setValue(int rowId, String columnId, Object value) {
		
		// TODO check index !
		Object[] row = content.get(rowId);
		if (row == null)
			throw new ProgramException("row was not declared: "+rowId);
		Integer colId = columnId2idx.get(columnId);
		if (colId == null)
			throw new ProgramException("column was not declared: "+columnId);
		
		row[colId] = value;
		
	}

	@Override
	public void setValue(int rowId, int columnIdx, Object value) {
		content.get(rowId)[columnIdx] = value;
	}


	@Override
	public void setValues(int rowId, Object[] values) {
		// check the number of values is OK
		if (values.length != columnId2idx.size())
			throw new WrongParametersException("wrong count of columns: expected "+columnId2idx.size()+", but found "+values.length);
		
		// TODO index !:
		content.set(rowId, values);
	}

	@Override
	public int addRow(Object[] values) {

		// check the number of values is OK
		if (values.length != columnId2idx.size())
			throw new WrongParametersException("wrong count of columns: expected "+columnId2idx.size()+", but found "+values.length);
		
		content.add(values);
		
		return content.size()-1;
		
	}


	@Override
	public String toString() {
	
		StringBuffer sb = new StringBuffer();
		sb
		 .append("a table with ")
		 .append(columnId2idx.size())
		 .append(" columns and ")
		 .append(content.size())
		 .append(" rows")
		 .append("\n");
		
		return sb.toString();
		
	}

	@Override
	public Object[] getRow(int i) {
		// TODO manage error
		return content.get(i);
	}

	@Override
	public void fillColumn(int colIndex, Object value) {
		
		for (Object[] values : content) {
			values[colIndex] = value;
		}
		
	}

	@Override
	public Object getValue(int rowId, String columnId) {
		return content.get(rowId)[columnId2idx.get(columnId)];
	}
	
	@Override
	public Object getValue(int rowId, int columnIdx) {
		return content.get(rowId)[columnIdx];
	}

	@Override
	public Object[] getValues(int rowId) {
		return content.get(rowId);
	}

	@Override
	public boolean isColumnEmpty(String columnId) {
		
		// TODO check parametrs
		int colIdx = columnId2idx.get(columnId);
		
		for (Object[] values : content) {
			if (values[colIdx] == null)
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isColumnEmpty(int colIdx) {
		
		for (Object[] values : content) {
			if (values[colIdx] == null)
				return true;
		}
		
		return false;
	}

	@Override
	public Collection<String> getEmptyColumnsIds() {

		Collection<String> emptyColumnsIds = new LinkedList<String>();
		
		for (Map.Entry<String,Integer> id2idx : columnId2idx.entrySet()) {
			if (isColumnEmpty(id2idx.getValue()))
				emptyColumnsIds.add(id2idx.getKey());
		}
		
		return emptyColumnsIds;
	}

	@Override
	public Collection<Integer> getEmptyColumnsIndexes() {
		
		Collection<Integer> emptyColumnsIdxs = new LinkedList<Integer>();
		
		for (Integer idx : columnId2idx.values()) {
			if (isColumnEmpty(idx))
				emptyColumnsIdxs.add(idx);
		}
		
		return emptyColumnsIdxs;
	}

	
}
