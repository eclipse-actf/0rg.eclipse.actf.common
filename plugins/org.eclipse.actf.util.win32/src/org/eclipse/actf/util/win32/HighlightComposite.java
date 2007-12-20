/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.win32;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;



public class HighlightComposite extends Composite implements IIntervalExec {

    private static final int BORDER_WIDTH = 3;
    
    private static HighlightComposite instance;

    private StyledText text;
    
    private Display display;

    private int flashingIndex = -1;
    
    private static int suppressRefCount = 0;

    private static boolean needRedraw = true;

    private static final int[][] FLASHING_COLORS = { 
        { SWT.COLOR_YELLOW, 250 }, { SWT.COLOR_GREEN, 250 },
        { SWT.COLOR_YELLOW, 250 }, { SWT.COLOR_GREEN, 250 }, 
        { SWT.COLOR_YELLOW, 250 }, { SWT.COLOR_GREEN, 250 },
        { SWT.COLOR_YELLOW, 1000 } 
    };

    private static final int MESSAGE_COLOR = SWT.COLOR_INFO_FOREGROUND;

    private static final int MESSAGE_BACK = SWT.COLOR_INFO_BACKGROUND;

    public HighlightComposite(Composite parent, int style) {
        super(parent, style);
        display = getDisplay();
        GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = BORDER_WIDTH;
        setLayout(layout);
        text = new StyledText(this, SWT.NONE);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));
        text.setBackground(parent.getBackground());
    }

    private void show(Rectangle rect, String message) {
    	if( -1 != flashingIndex ) {
    		needRedraw = true;
    	}
        flashingIndex = 0;
        if (null != message) {
            text.setText(message);
            StyleRange range = new StyleRange();
            range.start = 0;
            range.length = text.getCharCount();
            range.foreground = display.getSystemColor(MESSAGE_COLOR);
            range.background = display.getSystemColor(MESSAGE_BACK);
            text.setStyleRange(range);
        }
        getOverlayWindow().getComposite().moveAbove(null);
        moveAbove(null);
        setBounds(rect.x, rect.y, Math.max(BORDER_WIDTH, rect.width), Math.max(BORDER_WIDTH, rect.height));
        setVisible(true);
    }

    public int exec() {
    	if( -1 != flashingIndex ) {
            if (flashingIndex < FLASHING_COLORS.length) {
                setBackground(display.getSystemColor(FLASHING_COLORS[flashingIndex][0]));
                return FLASHING_COLORS[flashingIndex++][1];
            } else {
            	flashingIndex = -1;
                setVisible(false);
                if( needRedraw ) {
                    needRedraw = false;
                    getOverlayWindow().getComposite().redraw(); // Clear black block
                }
            }
    	}
        return 0;
    }

    public static void flashRectangle(Rectangle rect) {
        if (null != rect && 0 == suppressRefCount ) {
            if( OverlayWindow.getVisible() ) {
                OverlayWindow window = getOverlayWindow();
                if( null == instance ) {
                    instance = new HighlightComposite(window.getComposite(), SWT.NONE);
                }
                instance.show(rect,""); //$NON-NLS-1$
                window.run();
            }
            else {
                AccessibleHilighter.flashRectangle(rect);
            }
        }
    }
    
    public static int updateSuppressCount(int increment) {
        suppressRefCount += increment;
        return suppressRefCount;
    }
    
    public static void show(boolean show) {
        if( null != instance ) {
            instance.setVisible(show);
        }
    }
    
    public static OverlayWindow getOverlayWindow() {
        return OverlayWindow.getInstance(OverlayWindow.INDEX_HIGHLIGHT,true);
    }
    
    public static HighlightComposite getInstance() {
        return instance;
    }

}
