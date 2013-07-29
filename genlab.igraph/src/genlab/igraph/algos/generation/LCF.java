package genlab.igraph.algos.generation;

import genlab.core.commons.WrongParametersException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class LCF {

	public final int[] shifts;
	public final int count;
	
	public LCF(int[] shifts, int count) {
		this.shifts = shifts;
		this.count = count;
	}
	
	@Override
	public String toString() {
		if (count > 1)
			return Arrays.toString(shifts)+count;
		else
			return Arrays.toString(shifts);
	}
	
	private final static String messageForm = "the value should be in the form [1,2,-2]2";

	
	public static LCF parseFromString(String str) {
		
		if (!str.startsWith("["))
			throw new WrongParametersException(messageForm);
		
		if (str.indexOf('[', 1) > -1)
			throw new WrongParametersException(messageForm);
		
		int idxEndClosure = str.indexOf(']', 1);
		if (idxEndClosure < 1)
			throw new WrongParametersException(messageForm);
		
		String strValues = str.substring(1,idxEndClosure);
		
		StringTokenizer st = new StringTokenizer(strValues, ",");
		List<Integer> values = new LinkedList<Integer>();
		while (st.hasMoreElements()) {
			String token = st.nextToken().trim();
			values.add(Integer.parseInt(token));
		}
		int[] valuesArray = new int[values.size()];
		for (int i=0; i <values.size(); i++)
			valuesArray[i] = values.get(i);
		values.clear();
		
		Integer repeat;
		if (idxEndClosure == str.length()-1) {
			// no repeat index
			repeat = 1;
		} else {
			repeat = Integer.parseInt(str.substring(idxEndClosure+1));
		}
		
		return new LCF(valuesArray, repeat);
		
	}

}
