package genlab.core.parameters;

import genlab.core.commons.WrongParametersException;

import java.util.StringTokenizer;


public class ColorRGBParameterValue  {

	public int r = 0;
	public int g = 0;
	public int b = 0;
	 
	public ColorRGBParameterValue() {
		
	}
	
	public ColorRGBParameterValue(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
		
	}

	public ColorRGBParameterValue(String s) {
		StringTokenizer st = new StringTokenizer(s, "|");
		try {
			
			// decode as RGB
			r = Integer.parseInt(st.nextToken()); 
			g = Integer.parseInt(st.nextToken()); 
			b = Integer.parseInt(st.nextToken());
		
		} catch (RuntimeException e) {
			throw new WrongParametersException("wrong value for RGB color; format is int|int|int", e);
		}
	}


	@Override
	public boolean equals(Object obj) {
		try {
			ColorRGBParameterValue other = (ColorRGBParameterValue)obj;
			return other.b == b && other.g == g && other.r == r; 
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(r).append(",");
		sb.append(g).append(",");
		sb.append(b);
		return sb.toString();
	}

	
}
