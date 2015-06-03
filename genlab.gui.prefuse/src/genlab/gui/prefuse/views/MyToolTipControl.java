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

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;


public class MyToolTipControl extends ControlAdapter { 
	
		  
	    public final static String FIELD = "tooltip";
	    public final static String DEFAULT_TOOLTIP = "";
	    
	    /**
	     * Create a new ToolTipControl.
	     * @param field the field name to use for the tooltip text
	     */
	    public MyToolTipControl() {
	        
	    }

	    
	    /**
	     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	     */
	    public void itemEntered(VisualItem item, MouseEvent e) {
	        Display d = (Display)e.getSource();
	        try {
		        final String tip = item.getString(FIELD);
		        if (tip != null)
		        	d.setToolTipText(tip);
	        } catch (ArrayIndexOutOfBoundsException e2) {
	        	return;
	        }
	    }
	    
	    /**
	     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	     */
	    public void itemExited(VisualItem item, MouseEvent e) {
	        Display d = (Display)e.getSource();
	        d.setToolTipText(DEFAULT_TOOLTIP);
	    }

	   
}
