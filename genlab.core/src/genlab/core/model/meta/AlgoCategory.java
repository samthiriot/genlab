package genlab.core.model.meta;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class AlgoCategory {

	public final AlgoCategory parent;
	
	public final String name;
	public final String desc;
	
	public final String id;
	
	public final String parentId;
	
	public final Collection<AlgoCategory> children = new LinkedList<AlgoCategory>();
	
	
	public AlgoCategory(AlgoCategory parent, String name, String desc, String id) {
		super();
		this.parent = parent;
		this.name = name;
		this.desc = desc;
		this.id = id;
		this.parentId = buildTotalId();
		if (parent != null) {
			parent.addChildren(this);
		}
	}
	
	protected void addChildren(AlgoCategory child) {
		children.add(child);
	}

	/**
	 * returns null if no parent
	 * @return
	 */
	public AlgoCategory getParentCategory() {
		return parent;
	}
	
	public String getName() {
		if (parent == null)
			return name;
		else 
			return parent.getName()+" / "+name;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public String getId() {
		return id;
	}
	
	public String getTotalId() {
		return parentId;
	}
	
	protected String buildTotalId() {
		return (
				parent==null?
						id:
						parent.getId()+"."+id
						);
	}
	
	public Collection<AlgoCategory> getChildren() {
		return Collections.unmodifiableCollection(children);
	}
	
	public AlgoCategory getTopParent() {
		if (parent == null)
			return this;
		else
			return parent.getTopParent();
	}
	
	@Override
	public String toString() {
		return getTotalId();
	}
	
}
