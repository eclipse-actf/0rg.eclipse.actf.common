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

package org.eclipse.actf.core.config;

import java.util.StringTokenizer;

public class Version
{

	public static final String bundleVersionKey = "Bundle-Version";

	private int major;

	private int minor;

	private int micro;

	public Version (int major, int minor, int micro) {
		this.major = major;
		this.minor = minor;
		this.micro = micro;
	}

	public Version (String version) {
		StringTokenizer st = new StringTokenizer(version, ".", false);
		if (st.hasMoreTokens()) {
			major = Integer.valueOf(st.nextToken()).intValue();
			if (st.hasMoreTokens()) {
				minor = Integer.valueOf(st.nextToken()).intValue();
				if (st.hasMoreTokens()) {
					micro = Integer.valueOf(st.nextToken()).intValue();
				}
			}
		}
	}

	public String toString () {
		return major + "." + minor + "." + micro;
	}

	/**
	 * @param args
	 */
	public static void main (String[] args) {
		// TODO Auto-generated method stub
	}

	public int getMajor () {
		return major;
	}

	public void setMajor (int major) {
		this.major = major;
	}

	public int getMicro () {
		return micro;
	}

	public void setMicro (int micro) {
		this.micro = micro;
	}

	public int getMinor () {
		return minor;
	}

	public void setMinor (int minor) {
		this.minor = minor;
	}
}
