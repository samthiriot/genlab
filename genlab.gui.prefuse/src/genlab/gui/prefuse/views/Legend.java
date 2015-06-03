/*******************************************************************************
 * Copyright (c) 2009-2012 Samuel Thiriot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Samuel Thiriot - initial API and implementation
 ******************************************************************************/
package genlab.gui.prefuse.views;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import prefuse.Display;
import fr.research.samthiriot.commons.parameters.EventParameterDispatcher;
import fr.research.samthiriot.commons.parameters.IParametersObserver;
import fr.research.samthiriot.commons.parameters.events.EventParameterAbstract;
import fr.research.samthiriot.commons.parameters.events.EventParameterValueChanged;
import fr.research.samthiriot.parameters.Attribute;
import fr.research.samthiriot.parameters.IUserParametersListener;
import fr.research.samthiriot.parameters.UserParameters;
import fr.research.samthiriot.socialNetwork.LinkType;
import genlab.gui.prefuse.fromYang.PrefuseVisuLinktypeParameter;
import genlab.gui.prefuse.fromYang.PrefuseVisuNodeAttributeColorParameter;
import genlab.gui.prefuse.fromYang.PrefuseVisuParameters;

/**
 * Displays a legend for a prefuse visu, for UserParameters.  
 * 
 * @author Samuel Thiriot
 *
 */
public class Legend implements IParametersObserver, IUserParametersListener {

	/**
	 * The aera occupied by the legent on the display.
	 */
	private Rectangle area = null;
	
	private final UserParameters userParameters;
	private final PrefuseVisuParameters prefuseVisuParameters;
	
	/**
	 * cache of AWT Colors for RGB parameters 
	 */
	private Map<RGB, Color> rgb2awtColor;
	
	/**
	 * Cache of strings to display as GlyphVectors
	 */
	private Map<String, GlyphVector> string2glyph;
	
	/**
	 * If false, nothing is drawn; is automatically updated from parameters
	 */
	private boolean enabled = true;
	
	/**
	 * Paint background and border ?
	 */
	private final boolean paintBackground = true;
	
	/**
	 * Default color for displaying the text 
	 */
	private final Color colorText = Color.BLACK;
	
	/**
	 * Default color of the border of the legend frame
	 */
	private final Color colorBorder = Color.GRAY;
	
	/**
	 * Background color of the legend
	 */
	private final Color colorBackground = Color.WHITE;
	
	/**
	 * Antialias text ? (automatically read from parameters - high quality)
	 */
	private boolean antiAlias = true;
	
	/**
	 * THe width of the first column (with a sample of the line / color)
	 */
	final int LINK_WIDTH = 30;
	
	/**
	 * Width added as an internal margin
	 */
	final int PADDING = 12;
	
	/**
	 * The height of a line of the legend
	 */
	final int LINE_HEIGHT = 14;
	
	/**
	 * External margins of the legend frame
	 */
	final int MARGIN_RIGHT = 10;
	final int MARGIN_BOTTOM = 10;
	
	/**
	 * Distance between columns
	 */
	final int PADDING_WIDTH = 10;
	
	/**
	 * Distance between lines
	 */
	final int PADDING_HEIGHT = 6;
	
	/**
	 * Base line for text 
	 */
	final int BASELINE_BOTTOM = 2;
	
	public Legend(UserParameters userParameters, PrefuseVisuParameters prefuseVisuParameters) {
		
		this.userParameters = userParameters;
		this.prefuseVisuParameters = prefuseVisuParameters;
		
		rgb2awtColor = new HashMap<RGB, Color>();
		string2glyph = new HashMap<String, GlyphVector>();
		
		antiAlias = (Boolean)prefuseVisuParameters.getValue(prefuseVisuParameters.prefuseParamHighQuality);
		
		EventParameterDispatcher.getDispatcher().addParametersListener(this);
		
		userParameters.addListener(this);
		
	}
	
	
	private final Color getColorForRGB(RGB colorRGB) {
		

		if (colorRGB == null)
			return null;
		
		Color color = rgb2awtColor.get(colorRGB);
		if (color == null) {
			color = new Color(
					colorRGB.red,
					colorRGB.green,
					colorRGB.blue
					);
			rgb2awtColor.put(colorRGB, color);
		}
		
		return color;
		
	}
	
	private final GlyphVector getGlyphForString(Graphics2D graphics, String txt) {
		GlyphVector gl = string2glyph.get(txt);

		if (gl == null) {
			gl = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), txt);
			string2glyph.put(txt, gl);
		}
		
		return gl;
	}
	
	/**
	 * Actually paints the legend. 
	 * @param display
	 * @param graphics
	 */
	public void paintOn(Display display, Graphics2D graphics, double scale) {
		
		
		// exit if no legend
		if (!enabled)
			return;
		
		// antialias if highquality 
		if (display.isHighQuality())
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
		// start the computation of total height
		// .... first the lines required for linktypes 
		int TOTAL_HEIGHT = PADDING*3 + userParameters.getAllLinksTypes().size()*(LINE_HEIGHT+PADDING_HEIGHT) - PADDING_HEIGHT;
		
		// .... then the lines required for all the values of the selected attribute
		Integer idxAtt = (Integer)prefuseVisuParameters.prefuseVisuNodeColorParameter.getValue(prefuseVisuParameters.prefuseVisuNodeColorParameter.listAttributesParameter);
		Attribute attToDisplay =  (Attribute)prefuseVisuParameters.prefuseVisuNodeColorParameter.listAttributesParameter.getObjectForIndex(idxAtt);
		LinkedList<Object> domainValues = null;
		PrefuseVisuNodeAttributeColorParameter colorParam = null;
		if (attToDisplay != null) {
			colorParam = prefuseVisuParameters.prefuseVisuNodeColorParameter.getParameterForAttribute(attToDisplay);
			if (idxAtt != prefuseVisuParameters.prefuseVisuNodeColorParameter.idxNone) {
				domainValues = attToDisplay.getDomainValues();
				if (domainValues != null)
					TOTAL_HEIGHT += domainValues.size()*(LINE_HEIGHT+PADDING_HEIGHT) - PADDING_HEIGHT;
			}
		}
		
		// compute the total width;
		// first cache all the glyphs  & measure the required size
		int maxWidth = 0;
		for (LinkType linkType : userParameters.getAllLinksTypes()) {
			
			GlyphVector glyph = getGlyphForString(graphics, linkType.getName());
			maxWidth = Math.max(
					maxWidth, 
					(int)glyph.getVisualBounds().getWidth()
					);
			
		}
		
		// draw attribute values, line by line 
		if (domainValues != null) {
				
			for (Object domainValue : domainValues) {
				
				String key = null;
				if (domainValue instanceof String)
					key = (String)domainValue;
				else
					key = domainValue.toString();
				
				GlyphVector glyph = getGlyphForString(graphics, key);
				maxWidth = Math.max(
						maxWidth, 
						(int)glyph.getVisualBounds().getWidth()
						);
			}
		}
		
		
			
		final int TOTAL_WIDTH = PADDING*2 + LINK_WIDTH + PADDING_WIDTH + maxWidth;
		
					
		final int xTopLeft = display.getWidth() - TOTAL_WIDTH - MARGIN_RIGHT;
		final int yTopLeft = display.getHeight() - TOTAL_HEIGHT - MARGIN_BOTTOM;
		
//		final int xTopLeft = (int) (Math.round(display.getWidth()*scale) - TOTAL_WIDTH - MARGIN_RIGHT);
//		final int yTopLeft = (int) (Math.round(display.getHeight()*scale) - TOTAL_HEIGHT - MARGIN_BOTTOM);
//		
		int currentXleft = xTopLeft + PADDING;
		int currentYleftTop = yTopLeft + PADDING;
		int currentYleftBottom = currentYleftTop + LINE_HEIGHT;
	
		if (scale != 1.0) 
			graphics.scale(scale, scale);
		
		// paint border & background
		if (paintBackground) {
			graphics.setColor(colorBackground);
			graphics.fillRect(xTopLeft, yTopLeft, TOTAL_WIDTH, TOTAL_HEIGHT);
			graphics.setColor(colorBorder);
			graphics.drawRect(xTopLeft, yTopLeft, TOTAL_WIDTH, TOTAL_HEIGHT);
			area = new Rectangle(xTopLeft, yTopLeft, TOTAL_WIDTH, TOTAL_HEIGHT);
		}
		
		// display linktypes, line per line
		for (LinkType linkType : userParameters.getAllLinksTypes()) {
		
			PrefuseVisuLinktypeParameter linkTypeParam = prefuseVisuParameters.prefuseVisuLinkParameters.getParametersForLinkType(linkType);
			if (linkTypeParam == null)
				continue;
			RGB colorRGB = (RGB)linkTypeParam.getValue(linkTypeParam.colorParameter);
			
			Double lineWidth = (Double)linkTypeParam.getValue(linkTypeParam.width);
			if (lineWidth == null)
				continue;
			
			lineWidth = lineWidth * 2;
			
			graphics.setColor(getColorForRGB(colorRGB));
			
			graphics.fillRect(
					currentXleft, 
					currentYleftTop+LINE_HEIGHT/2, 
					LINK_WIDTH, 
					(int)Math.round(lineWidth.doubleValue())
					);
			
			graphics.setColor(
					colorText
					);

			GlyphVector gv = getGlyphForString(graphics, linkType.getName()); 
			graphics.drawGlyphVector(
					gv,
					currentXleft + LINK_WIDTH + PADDING_WIDTH,
					currentYleftBottom - BASELINE_BOTTOM
					);
			
			currentYleftTop = currentYleftBottom+PADDING_HEIGHT;
			currentYleftBottom = currentYleftTop+LINE_HEIGHT;
		}
		
		
		// draw attribute values, line by line 
		if (domainValues != null) {
				
			for (Object domainValue : domainValues) {
				RGB colorRGB = (RGB) colorParam.getValue(colorParam.getColorParameterForValue(domainValue));
	
				if (colorRGB == null)
					return ;
				

				graphics.setColor(getColorForRGB(colorRGB));
				
				graphics.fillRect(
						currentXleft, 
						currentYleftTop, 
						LINK_WIDTH, 
						LINE_HEIGHT
						);

				graphics.setColor(
						colorText
						);
				
				String value = null;
				try {
					value = (String)domainValue;
				} catch (ClassCastException e) {
					value = domainValue.toString();
				}
				GlyphVector gv = getGlyphForString(graphics, value); 
				graphics.drawGlyphVector(
						gv,
						currentXleft + LINK_WIDTH + PADDING_WIDTH,
						currentYleftBottom - BASELINE_BOTTOM
						);
				
				currentYleftTop = currentYleftBottom+PADDING_HEIGHT;
				currentYleftBottom = currentYleftTop+LINE_HEIGHT;
				
			}
			
		}
	}


	/**
	 * Updates quality (antialias) and enabled states from parameters
	 * Also, cache will be cleared - just in case.  
	 */
	public void notifyParameterEvent(EventParameterAbstract parameterEvent) {
		
		if (
				(parameterEvent.parameter != prefuseVisuParameters.prefuseParamDisplayLegend) &&
				(parameterEvent.parameter != prefuseVisuParameters.prefuseParamHighQuality)
				)
			return;
		
		if (!(parameterEvent instanceof EventParameterValueChanged)) 
			return;
		
		this.enabled = (Boolean)prefuseVisuParameters.getValue(prefuseVisuParameters.prefuseParamDisplayLegend);
		this.antiAlias = (Boolean)prefuseVisuParameters.getValue(prefuseVisuParameters.prefuseParamHighQuality);
		
		clearCache();
	}

	/**
	 * Clears cached glyph and colors
	 */
	public void clearCache() {
		rgb2awtColor.clear();
		string2glyph.clear();
	}

	/**
	 * Cache is cleared when parameters change 
	 */
	public void userParametersChanged() {
		clearCache();
	}
	
	@Override
	public void userParametersCleared() {
		userParametersChanged();
	}
		
	/**
	 * Returns the rectangle drawn on screen.
	 * Returns null if disabled or never painted.
	 * @return
	 */
	public Rectangle getClientArea() {
		if (!enabled)
			return null;
		else
			return area;
	}
	
}
