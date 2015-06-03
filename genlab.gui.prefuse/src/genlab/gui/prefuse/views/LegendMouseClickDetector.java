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

import java.awt.event.MouseEvent;

import org.eclipse.swt.graphics.Rectangle;

import prefuse.controls.ControlAdapter;
import fr.research.samthiriot.gui.TabVisuGui;

/**
 * If the user clicks into the Legend, displays visu parameters.
 * 
 * @author Samuel Thiriot
 */
public class LegendMouseClickDetector extends ControlAdapter {

	private final Legend legend;
	
	private final TabVisuGui tabVisuGui;
	
	private Long lastUserClickForOpening = null; 
	
	private static long DELAY_BEFORE_REOPEN = 1000; // ms
	
	public LegendMouseClickDetector(Legend legend, TabVisuGui tabVisuGui) {
		
		assert legend != null;
		
		this.legend = legend;
		this.tabVisuGui = tabVisuGui; 
	}

	/*
	
	@Override
	public void mousePressed(MouseEvent e) {
	

		final int x = e.getX();
		final int y = e.getY();
		
		if (!legend.getClientArea().contains(x, y))
			return;
	
		e.consume();
		
		System.err.println("Mouwse down inside");
	}
*/

	@Override
	public void mouseClicked(MouseEvent e) {
		
		// TODO
		//if (e.getButton() != LEFT_MOUSE_BUTTON)
		//	return;
		
		if ( 
				(lastUserClickForOpening == null) || 
				(System.currentTimeMillis() - lastUserClickForOpening > DELAY_BEFORE_REOPEN) ) {
				
			
			final int x = e.getX();
			final int y = e.getY();
			
			try {
				final Rectangle clientarea = legend.getClientArea(); 
				
				if (clientarea == null || !clientarea.contains(x, y))
					return;
				
	
			} catch (NullPointerException e2) {
				return;
			}
	
			lastUserClickForOpening = System.currentTimeMillis();
			
			e.consume();
			
			//System.out.println("("+x+","+y+") Inside");
			
			
			tabVisuGui.displayVisuParameters();
			//else
			//	System.out.println("("+x+","+y+") OUT ! ("+legend.getClientArea()+")");
			

		}
		
	}
	
	

}
