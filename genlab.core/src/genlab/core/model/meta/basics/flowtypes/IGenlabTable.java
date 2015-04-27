package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.NotImplementedException;

import java.util.Collection;
import java.util.List;

public interface IGenlabTable {

	public boolean isEmpty();
	

	public int getColumnsCount();
	
	/**
	 * Declares a set of column.
	 * Might raise a {@link NotImplementedException} if the table is readonly.
	 * @param ids
	 * @return
	 */
	public Collection<Integer> declareColumns(Collection<String> ids) throws NotImplementedException;
	
	public int declareColumn(String id) throws NotImplementedException;
	public boolean containsColumn(String id);
	
	/**
	 * Returns the columns ids in their order
	 * @return
	 */
	public List<String> getColumnsId();
	
	public String getColumnIdForIdx(int colIdx);
	
	/**
	 * Returns the id of this id, or null if no column has this id
	 * @param id
	 * @return
	 */
	public Integer getIndexForColumnId(String id);
		
	
	/**
	 * adds a line and returns it id
	 * @return
	 */
	public int addRow() throws NotImplementedException;
	public int getRowsCount();
	public void setValue(int rowId, String columnId, Object value) throws NotImplementedException;
	public void setValue(int rowId, int columnIdx, Object value)  throws NotImplementedException;
	public void setValues(int rowId, Object[] values) throws NotImplementedException;
	public void fillColumn(int colIndex, Object value) throws NotImplementedException;

	public Object getValue(int rowId, int columnIdx);
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
