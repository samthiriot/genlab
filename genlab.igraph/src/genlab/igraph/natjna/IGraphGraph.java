package genlab.igraph.natjna;

import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.TypeMapper;

public class IGraphGraph extends Structure {

	public IGraphGraph() {
		// TODO Auto-generated constructor stub
	}

	public IGraphGraph(TypeMapper mapper) {
		super(mapper);
		// TODO Auto-generated constructor stub
	}

	public IGraphGraph(int alignType) {
		super(alignType);
		// TODO Auto-generated constructor stub
	}

	public IGraphGraph(Pointer p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	public IGraphGraph(int alignType, TypeMapper mapper) {
		super(alignType, mapper);
		// TODO Auto-generated constructor stub
	}

	public IGraphGraph(Pointer p, int alignType) {
		super(p, alignType);
		// TODO Auto-generated constructor stub
	}

	public IGraphGraph(Pointer p, int alignType, TypeMapper mapper) {
		super(p, alignType, mapper);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List getFieldOrder() {
		// TODO Auto-generated method stub
		return null;
	}

}
