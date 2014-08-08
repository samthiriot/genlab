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
package genlab.bayesianinference;

import java.awt.Point;
import java.util.List;

/**
 * A node (variable) of a Bayesian network.
 * 
 * @author Samuel Thiriot
 *
 */
public interface IBayesianNode {

	
	
	public int inDegree();
	
	public int outDegree();
	
	public int degree();
	
	
	public String getID();
	public String getLabel();
	
	/**
	 * Returns the names of the domain
	 * @return
	 */
	public List<String> getDomain();
	
	/**
	 * Returns the index of this value in the domain, or throws an exception
	 * @param value
	 * @return
	 */
	public int getIdxInDomain(String value);
	
	/**
	 * Same as getDomain().size();
	 * @return
	 */
	public int getDomainSize();
	
	public IConditionalProbabilityTable getCPT();
	
	public void clearCPT();
	
	public void setID(String id);
	public void setLabel(String id);
	
	public Point getLocation();
	public void setLocation(Point point);
	
	public void validate();
	
	public void setCPT(IConditionalProbabilityTable cpt);
	
}
