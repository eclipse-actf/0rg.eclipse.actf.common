/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package com.ibm.haac.raven.core.traverse;

import java.util.Map;

import com.ibm.haac.raven.core.model.InvalidComponentException;
import com.ibm.haac.raven.core.traverse.filters.INodeFilter;

/**
 * used to traverse the nodes in a hierarchical structure. The nodes are usually of homogeneous in that 
 * the type of nodes is typically defined by an <code>IModel</code> object.
 * 
 * @author Mike Squillace
 */
public interface ITreeNodeWalker extends INodeWalker
{

	/**
	 * returns the children of the given node.
	 *
	 * @param element - node of tree
	 * @return children of given node or empty array if no children are found
	 * @throws InvalidComponentException if component is disposed or otherwise invalid
	 */
	public Object[] getChildren (Object element) throws InvalidComponentException;

	/**
	 * returns the children of the given node that pass all filteres 
	 * that were added using the <code>addNodeFilter</code> method.
	 *
	 * @param element - node of tree
	 * @return filtered children of given node or empty array if no children are found
	 * @see INodeWalker#getFilteredSuccessorNodes(Object)
	 * @see INodeWalker#addNodeFilter(INodeFilter)
	 * @throws InvalidComponentException if component is disposed or otherwise invalid
	 */
	public Object[] getFilteredChildren (Object element) throws InvalidComponentException;

	/**
	 * returns whether or not the given node has any children.
	 *
	 * @param element - node of tree
	 * @return <code>true</code> if node has children, <code>false</code> otherwise
	 * @throws InvalidComponentException if component is disposed or otherwise invalid
	 */
	public boolean hasChildren (Object element) throws InvalidComponentException;

	/**
	 * return the parent of the given node.
	 *
	 * @param element - node of tree
	 * @return parent of given node or <code>null</code> if node has no parent
	 * @throws InvalidComponentException if component is disposed or otherwise invalid
	 */
	public Object getParent (Object element) throws InvalidComponentException;

	/**
	 * map for associating nodes of heterogeneous types. The map should contain keys that are parents 
	 * and each parent key should be associated with a child to which the walker can traverse once it encounters the parent. Bridge 
	 * maps are useful for traversing nodes of vastly different types linked via unconventional mechanisms.
	 *  
	 * @param bridgeMap 
	 */
	public void setComponentBridgeMap (Map bridgeMap);
	
}
