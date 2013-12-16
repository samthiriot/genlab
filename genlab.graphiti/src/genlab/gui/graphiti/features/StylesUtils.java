package genlab.gui.graphiti.features;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.styles.AdaptedGradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredArea;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.LocationType;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.algorithms.styles.StylesPackage;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.ColorUtil;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.IGradientType;
import org.eclipse.graphiti.util.IPredefinedRenderingStyle;
import org.eclipse.graphiti.util.PredefinedColoredAreas;

/**
 * @see http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fresources%2Fdocu%2Fgfw%2Fstyles.htm
 * @see http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fresources%2Fdocu%2Fgfw%2Fcolor-schemas.htm
 * @author B12772
 *
 */
public class StylesUtils {


    private static final IColorConstant E_CLASS_TEXT_FOREGROUND = new ColorConstant(51, 51, 153);
 
//    private static final IColorConstant E_CLASS_FOREGROUND = new ColorConstant(255, 102, 0);
    private static final IColorConstant E_CLASS_FOREGROUND = new ColorConstant(98, 131, 167);

    private static final IColorConstant E_CLASS_BACKGROUND = new ColorConstant(255, 204, 153);
    
    private static final IColorConstant ALGOINSTANCE_CONTAINER_INSIDE_FOREGROUND = new ColorConstant(98, 131, 167);
    private static final IColorConstant ALGOINSTANCE_CONTAINER_INSIDE_BACKGROUND = new ColorConstant(255, 255, 255);

 
    private static String DEFAULT_FONT = "Arial";
 	
    /**
     * Retried from PredefinedColors
     * @param colorStart
     * @param locationValueStart
     * @param locationTypeStart
     * @param colorEnd
     * @param locationValueEnd
     * @param locationTypeEnd
     * @return
     */
    protected static GradientColoredArea createGradientColoredArea(String colorStart, int locationValueStart,
			LocationType locationTypeStart, String colorEnd, int locationValueEnd, LocationType locationTypeEnd) {
		/*
		 * Colors are stored locally at the gradient colored area for sake of
		 * better readability and in the assumption that colors are not
		 * intensively reused. The complete gradient definition is stored only
		 * once per diagram so reuse is in place at a higher level.
		 */
		GradientColoredArea ret = StylesFactory.eINSTANCE.createGradientColoredArea();
		ret.setStart(StylesFactory.eINSTANCE.createGradientColoredLocation());
		ret.getStart().setColor(StylesFactory.eINSTANCE.createColor());
		ret.getStart().getColor().eSet(StylesPackage.eINSTANCE.getColor_Blue(), ColorUtil.getBlueFromHex(colorStart));
		ret.getStart().getColor().eSet(StylesPackage.eINSTANCE.getColor_Green(), ColorUtil.getGreenFromHex(colorStart));
		ret.getStart().getColor().eSet(StylesPackage.eINSTANCE.getColor_Red(), ColorUtil.getRedFromHex(colorStart));
		ret.getStart().setLocationType(locationTypeStart);
		ret.getStart().setLocationValue(locationValueStart);
		ret.setEnd(StylesFactory.eINSTANCE.createGradientColoredLocation());
		ret.getEnd().setColor(StylesFactory.eINSTANCE.createColor());
		ret.getEnd().getColor().eSet(StylesPackage.eINSTANCE.getColor_Blue(), ColorUtil.getBlueFromHex(colorEnd));
		ret.getEnd().getColor().eSet(StylesPackage.eINSTANCE.getColor_Green(), ColorUtil.getGreenFromHex(colorEnd));
		ret.getEnd().getColor().eSet(StylesPackage.eINSTANCE.getColor_Red(), ColorUtil.getRedFromHex(colorEnd));
		ret.getEnd().setLocationType(locationTypeEnd);
		ret.getEnd().setLocationValue(locationValueEnd);
		return ret;
	}
    
    private static GradientColoredAreas createStyleRectangleWhite() {
    	// create the style for inside
		final GradientColoredAreas gcas = StylesFactory.eINSTANCE.createGradientColoredAreas();
		final EList<GradientColoredArea> gcasList = gcas.getGradientColor();
	
		// first, the top shadow
		{
			GradientColoredArea gca1 = createGradientColoredArea(
					"CCCCCC", 
					0, 
					LocationType.LOCATION_TYPE_ABSOLUTE_START,
					"CCCCCCC", 
					1, 
					LocationType.LOCATION_TYPE_ABSOLUTE_START
					);
			gcasList.add(gca1);
		}
		{
			GradientColoredArea gca1 = createGradientColoredArea(
					"CCCCCC", 
					1, 
					LocationType.LOCATION_TYPE_ABSOLUTE_START,
					"FFFFFF", 
					2, 
					LocationType.LOCATION_TYPE_ABSOLUTE_START
					);
			gcasList.add(gca1);
		}
		{
			GradientColoredArea gca1 = createGradientColoredArea(
					"FFFFFF", 
					2, 
					LocationType.LOCATION_TYPE_ABSOLUTE_START,
					"FFFFFF", 
					3, 
					LocationType.LOCATION_TYPE_ABSOLUTE_END
					);
			gcasList.add(gca1);
		}
		{
			GradientColoredArea gca1 = createGradientColoredArea(
					"FFFFFF", 
					3, 
					LocationType.LOCATION_TYPE_ABSOLUTE_END,
					"BBBBBB", 
					0, 
					LocationType.LOCATION_TYPE_ABSOLUTE_END
					);
			gcasList.add(gca1);
		}
		/*
		addGradientColoredArea(gcas, "F8FBFE", 0, LocationType.LOCATION_TYPE_ABSOLUTE_START, "F8FBFE", 1, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "EDF5FC", 1, LocationType.LOCATION_TYPE_ABSOLUTE_START, "EDF5FC", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "DEEDFA", 2, LocationType.LOCATION_TYPE_ABSOLUTE_START, "DEEDFA", 3, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_START);
		addGradientColoredArea(gcas, "D4E7F8", 3, LocationType.LOCATION_TYPE_ABSOLUTE_START, "FAFBFC", 2, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		addGradientColoredArea(gcas, "E2E5E9", 2, LocationType.LOCATION_TYPE_ABSOLUTE_END, "E2E5E9", 0, //$NON-NLS-1$ //$NON-NLS-2$
				LocationType.LOCATION_TYPE_ABSOLUTE_END);
		gradientColoredAreas.setStyleAdaption(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT);
		return gradientColoredAreas;
		*/
		
		return gcas;
    }
    
    /**
     * Returns the style for an "inside" rectangle - a rectangle for algo instances which can 
     * contain other instances.
     * @return
     */
    private static AdaptedGradientColoredAreas getStyleForRectangleInside() {
    	
		final AdaptedGradientColoredAreas agca = StylesFactory.eINSTANCE.createAdaptedGradientColoredAreas();
    	
		agca.setDefinedStyleId("GENLAB_RECT_INSIDEa");
		agca.setGradientType(IGradientType.VERTICAL);
		
		// and apply it to any situation.
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_DEFAULT, createStyleRectangleWhite());
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_PRIMARY_SELECTED, createStyleRectangleWhite());
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_SECONDARY_SELECTED, createStyleRectangleWhite());
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_ACTION_ALLOWED, createStyleRectangleWhite());
		agca.getAdaptedGradientColoredAreas().add(IPredefinedRenderingStyle.STYLE_ADAPTATION_ACTION_FORBIDDEN, createStyleRectangleWhite());
				
		return agca;
    }
    
    public static Style getStyleForRectangleInside(Diagram diagram) {
    
    	   final String styleId = "algo-instance-container-inside";
    	   
           Style style = findStyle(diagram, styleId);
    
           if (style == null) { // style not found - create new style
               IGaService gaService = Graphiti.getGaService();
               style = gaService.createStyle(diagram, styleId);
               style.setForeground(gaService.manageColor(diagram, E_CLASS_FOREGROUND));
               
               
               style.setLineStyle(LineStyle.SOLID);
               style.setLineWidth(1);

             //style.setBackground(gaService.manageColor(diagram, E_CLASS_BACKGROUND));
               gaService.setRenderingStyle(style, getStyleForRectangleInside());
               
               style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, true));
               
           }
           
           return style;
           
    }
    

    public static Style getStyleFor(Diagram diagram) {
    	
        final String styleId = "E-CLASS";
 
        Style style = findStyle(diagram, styleId);
 
        if (style == null) { // style not found - create new style
            IGaService gaService = Graphiti.getGaService();
            style = gaService.createStyle(diagram, styleId);
            style.setForeground(gaService.manageColor(diagram, E_CLASS_FOREGROUND));
            //style.setBackground(gaService.manageColor(diagram, E_CLASS_BACKGROUND));
            style.setLineStyle(LineStyle.SOLID);
            style.setLineWidth(1);
            gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteGlossAdaptions());
        }
        return style;
    }
    
  public static Style getStyleForConnection(Diagram diagram) {
    	
        final String styleId = "connection";
 
        Style style = findStyle(diagram, styleId);
 
        if (style == null) { // style not found - create new style
            IGaService gaService = Graphiti.getGaService();
            style = gaService.createStyle(diagram, styleId);
            style.setForeground(gaService.manageColor(diagram, E_CLASS_FOREGROUND));
            //style.setBackground(gaService.manageColor(diagram, E_CLASS_BACKGROUND));
            style.setLineStyle(LineStyle.SOLID);
            style.setLineWidth(2);
            gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteGlossAdaptions());
        }
        return style;
    }
    
    public static Style getStyleForEClassText(Diagram diagram) {
        final String styleId = "E-CLASS-TEXT";
 
        // this is a child style of the e-class-style
        Style parentStyle = getStyleFor(diagram);
        Style style = findStyle(parentStyle, styleId);
 
        if (style == null) { // style not found - create new style
            IGaService gaService = Graphiti.getGaService();
            style = gaService.createStyle(getStyleFor(diagram), styleId);
            // "overwrites" values from parent style
            style.setForeground(gaService.manageColor(diagram, E_CLASS_TEXT_FOREGROUND));
            style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, 8, false, true));
        }
        return style;
    }

    // find the style with a given id in the style-container, can return null
    private static Style findStyle(StyleContainer styleContainer, String id) {
    	
        // find and return style
        Collection<Style> styles = styleContainer.getStyles();
        if (styles != null) {
            for (Style style : styles) {
                if (id.equals(style.getId())) {
                    return style;
                }
            }
        }
        return null;
    }

	private StylesUtils() {
		
	}

}
