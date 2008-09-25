/*******************************************************************************
* Copyright (c) 2004, 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.resources;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * a default implementation of the IResourceLocator interface for ACTF. Subclasses add special functionality
 * for locating resources (e.g. .properties files) in particular environments.
 *
 * @author Barry Feigenbaum
 */
public class DefaultResourceLocator implements IResourceLocator
{

	/** {@inheritDoc} */
	public InputStream getResourceAsStream (String id) {
		return getResourceAsStream(id, getClass().getClassLoader());
	}

	/** {@inheritDoc} */
	public InputStream getResourceAsStream (String id, ClassLoader loader) {
		String name = System.getProperty(id);
		return getResourceAsBufferedStream(name != null ? name : id, loader);
	}

	/** {@inheritDoc} */
	public InputStream getResourceAsStream (String id, String base,
											String ext, ClassLoader loader) {
		String name = System.getProperty(id);
		return getResourceAsBufferedStream(
			name != null ? name : id, base, ext, loader);
	}

	protected InputStream makeBufferedStream (InputStream is) {
		if (is != null) {
			if (!(is instanceof BufferedInputStream)) {
				is = new BufferedInputStream(is);
			}
		}
		return is;
	}

	protected InputStream getResourceAsBufferedStream (String id, ClassLoader loader) {
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		return makeBufferedStream(loader.getResourceAsStream(id));
	}

	protected InputStream getResourceAsBufferedStream (String id, String base,
													   String ext, ClassLoader loader) {
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		String path = id;
		if (base != null) {
			path = base + "/" + path;
		}
		if (ext != null) {
			path = path + "." + ext;
		}
		return makeBufferedStream(loader.getResourceAsStream(path));
	}
	
	public URL getResource (String name) {
		return ClassLoaderCache.getDefault().getResource( name );
	}
	
	public URL[] getResources (String name) {
		List urls = new LinkedList();
		Enumeration en = null;
		
		try {
			en = ClassLoaderCache.getDefault().getResources(name);
		} catch (IOException e) {
		}
		
		while (en != null && en.hasMoreElements()) {
			urls.add((URL) en.nextElement());
		}
		
		return (URL[]) urls.toArray(new URL[urls.size()]);
	}
	
	public String getPath ( String name ) {
		String result = null;
		
		URL url = getResource( name );
		
		if ( url != null ) {
			result = url.toString();
		}
		
		return result;
	}
	
	public String[] getPaths( String name ) {
		String[] result = null;
		
		URL[] urls = getResources( name );
		
		if ( urls != null ) {
			result = new String[urls.length];
			
			for( int i=0; i < urls.length; i++ ) {
				if ( urls[i] != null ) {
					result[i] = urls[i].toString();
				}
			}
		}
		
		return result;
	}	
}
