package genlab.population.yang;

public class YangUtilities {

	private YangUtilities() {
		// TODO Auto-generated constructor stub
	}


	public static String getBNDomainForJavaValue(Object v) {
		if (v instanceof Integer)
			return "i"+v.toString();
		else 
			return v.toString();
	}
	
}
