package genlab.core.model.meta.basics.flowtypes;

import java.util.Collection;

public interface IGenlabTable {

	public boolean isEmpty();
	

	public int getColumnsCount();
	public Collection<Integer> declareColumns(Collection<String> ids);
	public int declareColumn(String id);
	public boolean containsColumn(String id);
	
	/**
	 * Returns the columns ids in their order
	 * @return
	 */
	public Collection<String> getColumnsId();
	
	/**
	 * adds a line and returns it id
	 * @return
	 */
	public int addRow();
	public int getRowsCount();
	public void setValue(int rowId, String columnId, Object value);
	public void setValue(int rowId, int columnIdx, Object value);
	public void setValues(int rowId, Object[] values);
	public void fillColumn(int colIndex, Object value);

	
	public Object getValue(int rowId, String columnId);
	public Object[] getValues(int rowId);
	
	public boolean isColumnEmpty(String columnId);
	public boolean isColumnEmpty(int columnIdx);
	
	public Collection<String> getEmptyColumnsIds();
	public Collection<Integer> getEmptyColumnsIndexes();
	
	/**
	 * Adds a row with the following values
	 * @param values
	 * @return
	 */
	public int addRow(Object[] values);

	public Object[] getRow(int i);
	
}
