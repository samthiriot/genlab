package genlab.igraph.parameters;

import genlab.core.commons.WrongParametersException;
import genlab.core.parameters.ListParameter;

import java.util.LinkedList;

public class ChoiceOfImplementationParameter extends ListParameter {

	public static enum EIgraphImplementation {
		
		JNA_ONLY ("Native library only"),
		R_ONLY 	("R only"),
		JNA_OR_R ("Native or fallback to R"),
		R_OR_JNA ("R or fallback to Native");
		
		public final String label;
		
		private EIgraphImplementation(String label) {
			this.label = label;
		}
		
		public static EIgraphImplementation forLabel(String l) {
			for (EIgraphImplementation e: values()) {
				if (l.equals(e.label))
					return e;
			}
			throw new WrongParametersException("unknown value: "+l);
		}
		
		public static LinkedList<String> labelsAsList() {
			LinkedList<String> res=  new LinkedList<String>();
			for (EIgraphImplementation e: values()) {
				res.add(e.label);
			}
			return res;
		}
		
	}
	
	public ChoiceOfImplementationParameter() {
		super(
				"igraph_implementation", 
				"implementation to use", 
				"Native is faster but only available on some platforms; R is slower but more cross-platform",
				2,
				EIgraphImplementation.labelsAsList()
				);

	}

	public EIgraphImplementation getImplementationForString(String s) {
		return EIgraphImplementation.forLabel(s);
	}
}
