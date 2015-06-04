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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;


public class MyToolTipControl extends ControlAdapter { 
	
		private ToolTip tip = null;
	    
		public final static String FIELD = "__tooltip";
	    public final static String DEFAULT_TOOLTIP = "";
	    
	    private final Shell shell;
	    
	    /**
	     * Create a new ToolTipControl.
	     * @param field the field name to use for the tooltip text
	     */
	    public MyToolTipControl(Shell shell) {
	        this.shell = shell ;
	    }

	    private void createTooltip() {
	    	tip = new ToolTip(shell, SWT.BALLOON);
	    	tip.setVisible(false);
	    }
	    
	    private void showTooltipSync(int x, int y, String text) {
	    	if (tip == null)
	    		createTooltip();
	    	tip.setLocation(x, y);
	    	int idxLine = text.indexOf("\n", 0);
	    	if (idxLine == -1) {
	    		tip.setText("");
	    		tip.setMessage(text);
	    	} else {
	    		tip.setText(text.substring(0, idxLine-1));
	    		tip.setMessage(text.substring(idxLine));
	    	}
	    	tip.setVisible(true);
	    }
	    
	    private void showTooltipAsync(final int x, final int y, final String text) {
	    	org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					showTooltipSync(x, y, text);
				}
			});
	    }
	    
	    private void hideTooltipSync() {
	    	tip.setVisible(false);
	    }
	    
	    private void hideTooltipAsync() {
	    	org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					hideTooltipSync();
				}
			});
	    }

	    /**
	     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	     */
	    public void itemEntered(VisualItem item, MouseEvent e) {
	    	
	    	try {
	    	String msg = item.getString(FIELD);
	    	if (msg != null)  
	    		showTooltipAsync(e.getXOnScreen(), e.getYOnScreen(), msg);
	    	else if (tip != null) 
	    		hideTooltipAsync();
	    	} catch (ArrayIndexOutOfBoundsException ex) {
	    		// data not found
	    		// ignore
	    	}
	    	/*
	        Display d = (Display)e.getSource();
	        try {
		        final String tip = item.getString(FIELD);
		        if (tip != null)
		        	d.setToolTipText(tip);
	        } catch (ArrayIndexOutOfBoundsException e2) {
	        	return;
	        }*/
	    }
	    
	    /**
	     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	     */
	    public void itemExited(VisualItem item, MouseEvent e) {
	        
	    	if (tip != null) 
	    		hideTooltipAsync();
	    	
	    	//Display d = (Display)e.getSource();
	        //d.setToolTipText(DEFAULT_TOOLTIP);
	    }

	   
}
