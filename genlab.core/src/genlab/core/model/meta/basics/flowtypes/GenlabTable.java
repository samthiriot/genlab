package genlab.core.model.meta.basics.flowtypes;

import genlab.core.commons.WrongParametersException;
import genlab.core.usermachineinteraction.GLLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	public void declareColumn(String id) {
		
		if (columnId2idx.containsKey(id))
			throw new WrongParametersException("this column already exists: "+id);
		
		GLLogger.debugTech("declaring new column "+id, getClass());
		
		// create the column id...
		createNewIdForColumn(id);
		
		// and resize all data
		int newSize = columnId2idx.size();
		for (int i = 0; i<content.size(); i++) {
			content.set(i, Arrays.copyOf(content.get(i), newSize));
		}
		
	}
	
	@Override
	public void declareColumns(Collection<String> ids) {
		
		// check parameters
		for (String id : ids) {
			if (columnId2idx.containsKey(id))
				throw new WrongParametersException("this column already exists: "+id);
		}
		
		// create ids 
		for (String id : ids) {
			createNewIdForColumn(id);
		}
		
		// and resize all data
		int newSize = columnId2idx.size();
		for (int i = 0; i<content.size(); i++) {
			content.set(i, Arrays.copyOf(content.get(i), newSize));
		}

	}

	@Override
	public boolean containsColumn(String id) {
		return columnId2idx.containsKey(id);
	}

	@Override
	public Collection<String> getColumnsId() {
		return columnsTitles;
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
		
		content.get(rowId)[columnId2idx.get(columnId)] = value;
		
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

	
}
