package genlab.core.model.meta.basics.flowtypes;

import java.util.Collection;

public interface IGenlabTable {

	public boolean isEmpty();
	

	public int getColumnsCount();
	public void declareColumns(Collection<String> ids);
	public void declareColumn(String id);
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
	public void setValues(int rowId, Object[] values);
	
	/**
	 * Adds a row with the following values
	 * @param values
	 * @return
	 */
	public int addRow(Object[] values);

	public Object[] getRow(int i);
	
}
