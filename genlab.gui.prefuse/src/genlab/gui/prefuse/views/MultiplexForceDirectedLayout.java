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

import java.util.Map;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.visual.EdgeItem;

/**
 * The prefuse force-directed layout, but which accepts different weights for different link types
 * 
 * @author Samuel Thiriot
 *
 */
public class MultiplexForceDirectedLayout extends ForceDirectedLayout {

	protected final static String FIELD = "type";
	protected final Map<Integer,LinktypeParameters> linktype2parameters;
	
	//protected long count = 0;
	
	public MultiplexForceDirectedLayout(String graph, Map<Integer,LinktypeParameters> linktype2parameters) {
		super(graph, false);

		this.linktype2parameters = linktype2parameters;
		
		//this.getForceSimulator().setIntegrator(new EulerIntegrator());
	}


	@Override
	protected final float getSpringCoefficient(EdgeItem e) {
		
		final Object linktypeId = e.get(FIELD);
		Double weight = linktype2parameters.get(linktypeId).weight;
		if (weight == null)
			return super.getSpringCoefficient(e);
		else 
			return weight.floatValue(); 
		
	}

	@Override
	protected final float getSpringLength(EdgeItem e) {
		return super.getSpringLength(e);
	}

	
	
}
