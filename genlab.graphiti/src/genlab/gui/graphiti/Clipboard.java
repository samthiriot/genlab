package genlab.gui.graphiti;

public class Clipboard {

	public static Clipboard singleton = new Clipboard();
	
	private Object[] copied = null;
	
	public Clipboard() {

	}
	
	public void setObjects(Object[] copied) {
		this.copied = copied;
	}
	
	public Object[] getObjects() {
		return this.copied;
	}

}
