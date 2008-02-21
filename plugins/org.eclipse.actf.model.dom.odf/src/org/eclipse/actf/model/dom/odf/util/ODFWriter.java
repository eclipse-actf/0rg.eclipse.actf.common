/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tatsuya ISHIHARA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.odf.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.actf.model.dom.odf.ODFConstants;
import org.eclipse.actf.model.dom.odf.OdfPlugin;
import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.core.runtime.IPath;


public class ODFWriter {
	private void addFileToZip(ZipOutputStream zos, String inputDir, File file)
			throws IOException, Exception {
		if (file.isDirectory()) {
			String entry = file.getPath();
			entry = entry.substring(inputDir.length() + 1);
			entry = entry.replaceAll("\\\\", "/");
			ZipEntry target = new ZipEntry(entry + "/");
			target.setSize(0);
			zos.putNextEntry(target);
			zos.closeEntry();

			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				addFileToZip(zos, inputDir, fileList[i]);
			}
		} else {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			String entry = file.getPath();
			entry = entry.substring(inputDir.length() + 1);
			entry = entry.replaceAll("\\\\", "/");
			ZipEntry target = new ZipEntry(entry);
			zos.putNextEntry(target);
			byte buf[] = new byte[1024];
			int count;
			while ((count = bis.read(buf, 0, 1024)) != -1) {
				zos.write(buf, 0, count);
			}
			bis.close();
			zos.closeEntry();
		}
	}

	public List<String> unzipODFFile(String odfName, String outputDir) {
		List<String> outputFileList = new ArrayList<String>();
		ZipInputStream zis = null;
		FileOutputStream fos = null;
		try {
			zis = new ZipInputStream(new FileInputStream(odfName));
			ZipEntry zent = null;
			String fileName = null;
			while ((zent = zis.getNextEntry()) != null) {
				fileName = zent.getName();
				if (fileName.contains("/")) {
					int lastIndex = fileName.lastIndexOf("/");
					File dir = new File(outputDir + File.separator
							+ fileName.substring(0, lastIndex));
					if (!dir.exists()) {
						dir.mkdirs();
					}
				}
				if (!fileName.endsWith("/")) {
					BufferedInputStream bis = new BufferedInputStream(zis);
					String outputFile = outputDir + File.separator + fileName;
					outputFileList.add(outputFile);
					fos = new FileOutputStream(outputFile);
					try {
						byte[] buffer = new byte[8192];
						int size;
						while ((size = bis.read(buffer)) != -1) {
							fos.write(buffer, 0, size);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						fos.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				zis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return outputFileList;
	}

	public void createODFFile(String inputDir, String outputODFName) {
		File inputDirFile = new File(inputDir);
		if (inputDirFile.isDirectory()) {
			File zipFile = new File(outputODFName);
			try {
				File[] fileList = inputDirFile.listFiles();
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
						zipFile));
				for (int i = 0; i < fileList.length; i++) {
					addFileToZip(zos, inputDir, fileList[i]);
				}
				zos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean rmDir(File deletedDir) {
		File[] files = deletedDir.listFiles();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				if (!rmDir(files[i]))
					return false;
			}
		return deletedDir.delete();
	}

	/*
	 * ODFDocument doc -- modified content.xml String outFile -- output ODF file
	 */
	public void saveODFFile(ODFDocument doc, String outFile) {
		String inFile = doc.getURL();

		// create tmp directory
		IPath tmpDir = OdfPlugin.getDefault().getStateLocation().append("tmp");
		File tmpDirFile = tmpDir.toFile();
		tmpDirFile.mkdir();
		String tmpDirPath = tmpDirFile.getPath();

		// unzip ODF file
		ODFWriter odfWriter = new ODFWriter();
		odfWriter.unzipODFFile(inFile, tmpDirPath);

		// save content.xml
		try {
			FileOutputStream writer = new FileOutputStream(tmpDirPath
					+ System.getProperty("file.separator")
					+ ODFConstants.ODF_CONTENT_FILENAME);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Source source = new DOMSource(doc);
			Result target = new StreamResult(writer);
			if (source != null) {
				try {
					tFactory.newTransformer().transform(source, target);
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// save ODF file
		odfWriter.createODFFile(tmpDirPath, outFile);

		// remove tmp directory
		rmDir(tmpDirFile);
	}
}
