package genlab.gui.prefuse;

import prefuse.util.ColorLib;
import genlab.core.parameters.ColorRGBParameterValue;
import genlab.gui.prefuse.parameters.ParamValueContinuum;

public class PrefuseUtils {

	public static ColorRGBParameterValue getColorRGBFromPrefuseColor(int prefuseColor) {
	
		return new ColorRGBParameterValue(
				ColorLib.red(prefuseColor),
				ColorLib.green(prefuseColor),
				ColorLib.blue(prefuseColor)
				);
		
	}
	
	public static ParamValueContinuum getContinumCool() {
		int[] v = ColorLib.getCoolPalette(2);
		return new ParamValueContinuum(
				PrefuseUtils.getColorRGBFromPrefuseColor(v[0]), 
				PrefuseUtils.getColorRGBFromPrefuseColor(v[1])
				);
	}

	public static ParamValueContinuum getContinumHot() {
		int[] v = ColorLib.getHotPalette(2);
		return new ParamValueContinuum(
				PrefuseUtils.getColorRGBFromPrefuseColor(v[0]), 
				PrefuseUtils.getColorRGBFromPrefuseColor(v[1])
				);
	}

	public static ParamValueContinuum getContinumGray() {
		int[] v = ColorLib.getGrayscalePalette(2);
		return new ParamValueContinuum(
				PrefuseUtils.getColorRGBFromPrefuseColor(v[0]), 
				PrefuseUtils.getColorRGBFromPrefuseColor(v[1])
				);
	}

	public static int[] getPalette(ParamValueContinuum c) {
		return ColorLib.getInterpolatedPalette(
				ColorLib.getColor(c.value1.r, c.value1.g, c.value1.b).getRGB(), 
				ColorLib.getColor(c.value2.r, c.value2.g, c.value2.b).getRGB()
				);
		/*
		int[] res = new int[2];
		res[0] = ColorLib.getColor(c.value1.r, c.value1.g, c.value1.b).getRGB();
		res[1] = ColorLib.getColor(c.value2.r, c.value2.g, c.value2.b).getRGB();
		return res;
		*/
	}
	private PrefuseUtils() {
		
	}

}
