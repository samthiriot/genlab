package genlab.core.model.meta.basics.flowtypes;

public class TableFlowType extends AbstractFlowType<IGenlabTable> {

	public static TableFlowType SINGLETON = new TableFlowType();

	public TableFlowType() {
		super(
				"core.types.table", 
				"a table of values",
				"stores values in a table"
				);
		
	}
	
	

	@Override
	public IGenlabTable decodeFrom(Object value) {
		return (IGenlabTable)value;
	}

	

}
