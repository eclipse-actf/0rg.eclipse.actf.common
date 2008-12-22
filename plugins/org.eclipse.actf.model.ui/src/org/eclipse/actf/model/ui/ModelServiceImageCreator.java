/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui;

import java.io.File;

import org.eclipse.actf.model.ui.util.ModelServiceMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class to create a screenshot (bitmap file) of the
 * {@link IModelService}
 */
public class ModelServiceImageCreator {

	// 256M -> 7000000 400M->10000000
	/**
	 * Maximum size of screenshot image
	 */
	public static final int DUMP_IMG_SIZE_LIMIT = 10000000;

	private IModelService modelService;

	/**
	 * Constructor of the class
	 * 
	 * @param modelService
	 *            target {@link IModelService}
	 */
	public ModelServiceImageCreator(IModelService modelService) {
		this.modelService = modelService;
	}

	/**
	 * Generate screenshot of the {@link IModelService} and save it as bitmap
	 * file
	 * 
	 * @param saveFileName
	 *            target file path
	 * @param isWhole
	 *            <ul>
	 *            <li>true: try to include entire area of the content into
	 *            screenshot</li>
	 *            <li>false: generate screenshot of current visible area</li>
	 *            </ul>
	 * @return resulting {@link File}
	 */
	public File getScreenImageAsBMP(String saveFileName, boolean isWhole) {

		IModelServiceScrollManager scrollManager = modelService
				.getScrollManager();
		int[] size = scrollManager.getSize(isWhole).toArray();

		boolean hugeImage = false;

		if (size[0] < 1 || size[1] < 1) {
			size = new int[] { 1, 1, 1, 1 };
		}

		if (size[2] * size[3] > DUMP_IMG_SIZE_LIMIT) {
			size[3] = DUMP_IMG_SIZE_LIMIT / size[2];
			if (isWhole) {
				hugeImage = true;
			}
		}

		ImageData[] wholeImgData;

		if (isWhole) {
			switch (scrollManager.getScrollType()) {
			case IModelServiceScrollManager.ABSOLUTE_COORDINATE:
				wholeImgData = getImageByAbsoluteCoordinateScroll(size,
						scrollManager, true);
				break;
			case IModelServiceScrollManager.PAGE:
				wholeImgData = getImageByPageScroll(size, scrollManager);
				break;
			case IModelServiceScrollManager.INCREMENTAL:
				wholeImgData = getImageByIncrementalScroll(size, scrollManager);
				break;
			case IModelServiceScrollManager.NONE:
			default:
				hugeImage = false;
				wholeImgData = getImageByAbsoluteCoordinateScroll(size,
						scrollManager, false);
			}
		} else {
			wholeImgData = getImageByAbsoluteCoordinateScroll(size,
					scrollManager, false);
		}

		ImageLoader loader = new ImageLoader();
		loader.data = wholeImgData;
		loader.save(saveFileName, SWT.IMAGE_BMP);

		if (hugeImage) {
			MessageBox warn = new MessageBox(PlatformUI.getWorkbench()
					.getDisplay().getActiveShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			warn.setMessage(ModelServiceMessages.ImageCreator_ImageTooLarge);
			warn.open();
		}

		return new File(saveFileName);
	}

	private ImageData[] getImageByAbsoluteCoordinateScroll(int[] size,
			IModelServiceScrollManager scrollManager, boolean isWhole) {
		Image img = new Image(Display.getDefault(), size[0] + 2, size[1] + 2);
		ImageData[] wholeImgData = new ImageData[1];
		GC gc = new GC(modelService.getTargetComposite());
		int xCnt, yCnt;

		if (isWhole) {
			xCnt = (size[2] + size[0] - 1) / size[0];
			yCnt = (size[3] + size[1] - 1) / size[1];
		} else {
			xCnt = 1;
			yCnt = 1;
			size[2] = size[0];
			size[3] = size[1];
		}
		ImageData[][] imgData = new ImageData[xCnt][yCnt];

		// System.out.println("start a:"+xCnt+" "+yCnt);

		for (int i = 0; i < xCnt; i++) {
			for (int j = 0; j < yCnt; j++) {
				if (isWhole) {
					scrollManager.absoluteCoordinateScroll(i * size[0], j
							* size[1], true);
				}

				gc.copyArea(img, 0, 0);
				imgData[i][j] = img.getImageData();
			}
		}
		if (isWhole) {
			scrollManager.absoluteCoordinateScroll(0, 0, false);
		}

		int depth = imgData[0][0].depth;
		// System.out.println(size[0] + " " + size[1] + " " + size[2] + " " +
		// size[3] + " " + depth);

		wholeImgData[0] = new ImageData(size[2], size[3], depth,
				imgData[0][0].palette);
		// new PaletteData(0xff,0xff,0xff));

		int wholeRowBytes = ((size[2] * depth + 31) / 32) * 4;
		int partRowBytes = (((size[0] + 2) * depth + 31) / 32) * 4;
		depth = depth / 8;
		int xBegin, yBegin;

		int wholeSize = wholeImgData[0].data.length;

		for (int j = 0; j < yCnt; j++) {
			for (int i = 0; i < xCnt; i++) {
				if (j < yCnt - 1) {
					yBegin = 0;
				} else {
					yBegin = size[1] * yCnt - size[3];
				}

				if (i < xCnt - 1) {
					xBegin = 0;
				} else {
					xBegin = size[0] * xCnt - size[2];
				}
				xBegin += 2;

				for (int k = yBegin; k < size[1]; k++) {
					// System.out.println("i:" + i + " j:" + j + " k:" + k);
					int wholeBegin = (j * size[1] + (k - yBegin))
							* wholeRowBytes + i * size[0] * depth;
					int copySize = partRowBytes - xBegin * depth;
					if (wholeSize < wholeBegin + copySize) {
						copySize = wholeSize - wholeBegin;
					}
					System.arraycopy(imgData[i][j].data, (k + 2) * partRowBytes
							+ xBegin * depth, wholeImgData[0].data, wholeBegin,
							copySize);
				}
			}
		}

		gc.dispose();
		// System.out.println("ImageCreator(abs): fin");

		return wholeImgData;

	}

	private ImageData[] getImageByIncrementalScroll(int[] size,
			IModelServiceScrollManager scrollManager) {
		final Image img = new Image(Display.getDefault(), size[0] + 2,
				size[1] + 2);
		ImageData[] wholeImgData = new ImageData[1];
		final GC gc = new GC(modelService.getTargetComposite());
		int xCnt = 1, yCnt = 1;

		// TODO use getScrollcount() method
		while (scrollManager.incrementLargeScrollX(false) > 0)
			;
		while (scrollManager.incrementLargeScrollY(false) > 0)
			;

		while (scrollManager.decrementLargeScrollX(false) > 0) {
			xCnt++;
		}
		while (scrollManager.decrementLargeScrollY(false) > 0) {
			yCnt++;
		}

		// for rendering wait
		scrollManager.decrementLargeScrollY(true);

		ImageData[][] imgData = new ImageData[xCnt][yCnt];
		int[] scrollX = new int[xCnt];
		int[] scrollY = new int[yCnt];

		// System.out.println("start:" + xCnt + " " + yCnt);

		// System.out.println("X: "+scrollManager.incrementScrollX());
		// System.out.println("Y: "+scrollManager.incrementScrollY());

		for (int i = 0; i < xCnt; i++) {
			for (int j = 0; j < yCnt; j++) {

				gc.copyArea(img, 0, 0);
				imgData[i][j] = img.getImageData();

				// gc.copyArea(img, 0, 0);
				// imgData[i][j] = img.getImageData();
				scrollY[j] = scrollManager.incrementLargeScrollY(true);
			}
			scrollX[i] = scrollManager.incrementLargeScrollX(true);
			while (scrollManager.decrementLargeScrollY(false) > 0)
				;
		}

		int depth = imgData[0][0].depth;
		// System.out.println(size[0] + " " + size[1] + " " + size[2] + " " +
		// size[3] + " " + depth);

		wholeImgData[0] = new ImageData(size[2], size[3], depth,
				imgData[0][0].palette);

		int wholeRowBytes = ((size[2] * depth + 31) / 32) * 4;
		int partRowBytes = (((size[0] + 2) * depth + 31) / 32) * 4;
		depth = depth / 8;
		int xBegin, yBegin;
		int curX = 0, curY = 0;
		int finX = 0, finY = 0;

		int wholeSize = wholeImgData[0].data.length;

		// System.out.println("copy start");
		for (int j = 0; j < yCnt; j++) {
			if (j == 0) {
				yBegin = 0;
			} else {
				yBegin = finY - curY;
			}
			// System.out.println("Y: " + yBegin + " " + curY + " " + finY);
			for (int i = 0; i < xCnt; i++) {

				if (i == 0) {
					xBegin = 0;
				} else {
					xBegin = finX - curX;
				}
				xBegin += 2;
				// System.out.println("X: " + xBegin + " " + curX + " " + finX);

				// for (int k = yBegin; k < size[1]; k++) {
				for (int k = 0; k < size[1]; k++) {
					// System.out.println("i:" + i + " j:" + j + " k:" + k);

					int wholeBegin = (finY + (k - yBegin)) * wholeRowBytes
							+ finX * depth;
					int copySize = partRowBytes - xBegin * depth;
					// System.out.println(wholeBegin + " " + copySize + " " +
					// wholeSize);
					if (wholeSize < wholeBegin + copySize) {
						copySize = wholeSize - wholeBegin;
					}
					if (copySize > 0) {
						System.arraycopy(imgData[i][j].data, (k + 2)
								* partRowBytes + xBegin * depth,
								wholeImgData[0].data, wholeBegin, copySize);
					}
				}
				curX += scrollX[i];
				finX += size[0] - xBegin;
			}
			curY += scrollY[j];
			finY += size[1] - yBegin;
		}

		gc.dispose();
		// System.out.println("ImageCreator(inc): fin");
		return wholeImgData;

	}

	private ImageData[] getImageByPageScroll(int[] size,
			IModelServiceScrollManager scrollManager) {
		final Image img = new Image(Display.getDefault(), size[0] + 2,
				size[1] + 2);
		ImageData[] wholeImgData = new ImageData[1];
		final GC gc = new GC(modelService.getTargetComposite());
		int yCnt = 1;

		yCnt = scrollManager.getLastPageNumber();
		if (yCnt < 1) {
			// System.out.println("page count:" + yCnt);
			yCnt = 1;
		}

		scrollManager.jumpToPage(1, true);

		ImageData[] imgData = new ImageData[yCnt];

		// System.out.println("start:" + " " + yCnt);

		for (int j = 0; j < yCnt; j++) {
			gc.copyArea(img, 0, 0);
			imgData[j] = img.getImageData();
			scrollManager.incrementPageScroll(true);
		}
		scrollManager.jumpToPage(1, false);

		int depth = imgData[0].depth;
		// System.out.println(size[0] + " " + size[1] + " " + size[2] + " " +
		// size[3] + " " + depth);

		wholeImgData[0] = new ImageData(size[2], size[3], depth,
				imgData[0].palette);

		int wholeRowBytes = ((size[2] * depth + 31) / 32) * 4;
		int partRowBytes = (((size[0] + 2) * depth + 31) / 32) * 4;
		depth = depth / 8;

		int wholeSize = wholeImgData[0].data.length;

		for (int j = 0; j < yCnt; j++) {
			for (int k = 0; k < size[1]; k++) {
				// System.out.println("i:" + i + " j:" + j + " k:" + k);
				int wholeBegin = (size[1] * j + k) * wholeRowBytes;
				int copySize = partRowBytes;
				if (wholeSize < wholeBegin + copySize) {
					copySize = wholeSize - wholeBegin;
				}
				if (copySize > 0) {
					System.arraycopy(imgData[j].data, (k + 2) * partRowBytes,
							wholeImgData[0].data, wholeBegin, copySize);
				}

			}
		}

		gc.dispose();
		// System.out.println("ImageCreator(page): fin");
		return wholeImgData;
	}

}
