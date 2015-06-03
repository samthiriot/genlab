package genlab.gui.prefuse.views;

import org.eclipse.swt.graphics.RGB;

/**
 * Stores in an efficient and lisible way the parameters for a link type
 * 
 * @author Samuel Thiriot
 *
 */
public class LinktypeParameters {

	protected final static RGB defaultRGBColor = new RGB(100, 100, 100);
	protected final static Double defaultWeight = new Double(0.0000001);

	public RGB color;
	public double width;
	public boolean weightAuto;
	public Double weight;

	public LinktypeParameters(RGB color, double width, boolean weightAuto, Double weight) {
		super();
		this.color = color;
		this.width = width;
		this.weightAuto = weightAuto;
		this.weight = weight;
	}

}
