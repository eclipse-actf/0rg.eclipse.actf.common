/*******************************************************************************
 * Copyright (c) 2007, 2019 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - 495607
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ie;

import org.eclipse.actf.util.win32.MemoryUtil;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.COMObject;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.internal.win32.MSG;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class WebBrowserIEControlSite extends OleControlSite {

	static final int OLECMDID_SHOWSCRIPTERROR = 40;
	static final String ABOUT_BLANK = "about:blank"; //$NON-NLS-1$

	private static final int S_OK = COM.S_OK;
	private static final int S_FALSE = COM.S_FALSE;
	private static final int E_INVALIDARG = COM.E_INVALIDARG;
	private static final int E_NOTIMPL = COM.E_NOTIMPL;
	private static final int E_NOINTERFACE = COM.E_NOINTERFACE;
	private static final int E_NOTSUPPORTED = COM.E_NOTSUPPORTED;

	private static final int INET_E_DEFAULT_ACTION = 0x800C0011;
	private static final int URLZONE_INTRANET = 1;
	private static final int URLPOLICY_ALLOW = 0x00;
	private static final int URLPOLICY_DISALLOW = 0x03;
	private static final int URLPOLICY_JAVA_PROHIBIT = 0x0;
	private static final int URLPOLICY_JAVA_LOW = 0x00030000;
	private static final int URLACTION_ACTIVEX_RUN = 0x00001200;
	private static final int URLACTION_JAVA_MIN = 0x00001C00;
	private static final int URLACTION_JAVA_MAX = 0x00001Cff;

	COMObject iDocHostUIHandler;
	COMObject iOleCommandTarget;

	COMObject iServiceProvider;
	COMObject iInternetSecurityManager;
	boolean ignoreNextMessage;

	public WebBrowserIEControlSite(Composite parent, int style, String progId) {
		super(parent, style, progId);
	}

	protected void createCOMInterfaces() {
		super.createCOMInterfaces();
		iDocHostUIHandler = new COMObject(new int[] { 2, 0, 0, 4, 1, 5, 0, 0,
				1, 1, 1, 3, 3, 2, 2, 1, 3, 2 }) {
			public long method0(long[] args) {
				return QueryInterface(args[0], args[1]);
			}

			public long method1(long[] args) {
				return AddRef();
			}

			public long method2(long[] args) {
				return Release();
			}

			public long method3(long[] args) {
				return ShowContextMenu(args[0], args[1], args[2], args[3]);
			}

			public long method4(long[] args) {
				return GetHostInfo(args[0]);
			}

			public long method5(long[] args) {
				return ShowUI(args[0], args[1], args[2], args[3], args[4]);
			}

			public long method6(long[] args) {
				return HideUI();
			}

			public long method7(long[] args) {
				return UpdateUI();
			}

			public long method8(long[] args) {
				return EnableModeless(args[0]);
			}

			public long method9(long[] args) {
				return OnDocWindowActivate(args[0]);
			}

			public long method10(long[] args) {
				return OnFrameWindowActivate(args[0]);
			}

			public long method11(long[] args) {
				return ResizeBorder(args[0], args[1], args[2]);
			}

			public long method12(long[] args) {
				return TranslateAccelerator(args[0], args[1], args[2]);
			}

			public long method13(long[] args) {
				return GetOptionKeyPath(args[0], args[1]);
			}

			public long method14(long[] args) {
				return GetDropTarget(args[0], args[1]);
			}

			public long method15(long[] args) {
				return GetExternal(args[0]);
			}

			public long method16(long[] args) {
				return TranslateUrl(args[0], args[1], args[2]);
			}

			public long method17(long[] args) {
				return FilterDataObject(args[0], args[1]);
			}
		};
		iOleCommandTarget = new COMObject(new int[] { 2, 0, 0, 4, 5 }) {
			public long method0(long[] args) {
				return QueryInterface(args[0], args[1]);
			}

			public long method1(long[] args) {
				return AddRef();
			}

			public long method2(long[] args) {
				return Release();
			}

			public long method3(long[] args) {
				return QueryStatus(args[0], args[1], args[2], args[3]);
			}

			public long method4(long[] args) {
				return Exec(args[0], args[1], args[2], args[3], args[4]);
			}
		};

		iServiceProvider = new COMObject(new int[] { 2, 0, 0, 3 }) {
			public long method0(long [] args) {
				return QueryInterface(args[0], args[1]);
			}

			public long method1(long [] args) {
				return AddRef();
			}

			public long method2(long [] args) {
				return Release();
			}

			public long method3(long [] args) {
				return QueryService(args[0], args[1], args[2]);
			}
		};
		iInternetSecurityManager = new COMObject(new int[] { 2, 0, 0, 1, 1, 3,
				4, 8, 7, 3, 3 }) {
			public long method0(long [] args) {
				return QueryInterface(args[0], args[1]);
			}

			public long method1(long [] args) {
				return AddRef();
			}

			public long method2(long [] args) {
				return Release();
			}

			public long method3(long [] args) {
				return SetSecuritySite(args[0]);
			}

			public long method4(long [] args) {
				return GetSecuritySite(args[0]);
			}

			public long method5(long [] args) {
				return MapUrlToZone(args[0], args[1], (int) /* 64 */args[2]);
			}

			public long method6(long [] args) {
				return GetSecurityId(args[0], args[1], args[2], args[3]);
			}

			public long method7(long [] args) {
				return ProcessUrlAction(args[0], (int) /* 64 */args[1],
						args[2], (int) /* 64 */args[3], args[4],
						(int) /* 64 */args[5], (int) /* 64 */args[6],
						(int) /* 64 */args[7]);
			}

			public long method8(long [] args) {
				return QueryCustomPolicy(args[0], args[1], args[2], args[3],
						args[4], (int) /* 64 */args[5], (int) /* 64 */args[6]);
			}

			public long method9(long [] args) {
				return SetZoneMapping((int) /* 64 */args[0], args[1],
						(int) /* 64 */args[2]);
			}

			public long method10(long [] args) {
				return GetZoneMappings((int) /* 64 */args[0], args[1],
						(int) /* 64 */args[2]);
			}
		};

	}

	protected void disposeCOMInterfaces() {
		super.disposeCOMInterfaces();
		if (iDocHostUIHandler != null) {
			iDocHostUIHandler.dispose();
			iDocHostUIHandler = null;
		}
		if (iOleCommandTarget != null) {
			iOleCommandTarget.dispose();
			iOleCommandTarget = null;
		}

		if (iServiceProvider != null) {
			iServiceProvider.dispose();
			iServiceProvider = null;
		}
		if (iInternetSecurityManager != null) {
			iInternetSecurityManager.dispose();
			iInternetSecurityManager = null;
		}
	}

	protected int QueryInterface(long riid, long ppvObject) {
		int result = super.QueryInterface(riid, ppvObject);
		if (result == S_OK)
			return result;
		if (riid == 0 || ppvObject == 0)
			return E_INVALIDARG;
		GUID guid = new GUID();
		COM.MoveMemory(guid, riid, GUID.sizeof);
		if (COM.IsEqualGUID(guid, COM.IIDIDocHostUIHandler)) {
			MemoryUtil.MoveMemory(ppvObject,
					new long[] { iDocHostUIHandler.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return S_OK;
		}
		if (COM.IsEqualGUID(guid, COM.IIDIOleCommandTarget)) {
			MemoryUtil.MoveMemory(ppvObject,
					new long[] { iOleCommandTarget.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return S_OK;
		}

		if (COM.IsEqualGUID(guid, COM.IIDIServiceProvider)) {
			COM.MoveMemory(ppvObject,
					new long[] { iServiceProvider.getAddress() },
					OS.PTR_SIZEOF);
			AddRef();
			return COM.S_OK;
		}
		if (COM.IsEqualGUID(guid, COM.IIDIInternetSecurityManager)) {
			COM.MoveMemory(ppvObject,
					new long[] { iInternetSecurityManager
							.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return COM.S_OK;
		}

		MemoryUtil.MoveMemory(ppvObject, new long[] { 0 }, OS.PTR_SIZEOF);
		return E_NOINTERFACE;
	}

	protected int AddRef() {
		return super.AddRef();
	}

	protected int Release() {
		return super.Release();
	}

	/* IDocHostUIHandler */

	long EnableModeless(long EnableModeless) {
		return E_NOTIMPL;
	}

	long FilterDataObject(long pDO, long ppDORet) {
		return E_NOTIMPL;
	}

	long GetDropTarget(long pDropTarget, long ppDropTarget) {
		return E_NOTIMPL;
	}

	long GetExternal(long ppDispatch) {
		MemoryUtil.MoveMemory(ppDispatch, new long[] { 0 }, OS.PTR_SIZEOF);
		return S_FALSE;
	}

	long GetHostInfo(long pInfo) {
		int style = getParent().getParent().getStyle();
		int info = 0x00040000;
		// if ((style & SWT.BORDER) == 0) info |= 0x00200000;
		MemoryUtil.MoveMemory(pInfo + 4, new int[] { info }, 4);
		return S_OK;
	}

	long GetOptionKeyPath(long pchKey, long dw) {
		return E_NOTIMPL;
	}

	long HideUI() {
		return E_NOTIMPL;
	}

	long OnDocWindowActivate(long fActivate) {
		return E_NOTIMPL;
	}

	long OnFrameWindowActivate(long fActivate) {
		return E_NOTIMPL;
	}

	long ResizeBorder(long prcBorder, long pUIWindow, long fFrameWindow) {
		return E_NOTIMPL;
	}

	long ShowContextMenu(long dwID, long ppt, long pcmdtReserved, long pdispReserved) {
		/* Show default IE popup menu */
		return S_FALSE;
	}

	long ShowUI(long dwID, long pActiveObject, long pCommandTarget, long pFrame,
			long pDoc) {
		return E_NOTIMPL;
	}

	long TranslateAccelerator(long lpMsg, long pguidCmdGroup, long nCmdID) {
		/*
		 * Handle menu accelerator
		 */
		Menu menubar = getShell().getMenuBar();
		if (menubar != null && !menubar.isDisposed() && menubar.isEnabled()) {
			Shell shell = menubar.getShell();
			long hwnd = shell.handle;
			long hAccel = OS.SendMessage(hwnd, OS.WM_APP + 1, 0, 0);
			if (hAccel != 0) {
				MSG msg = new MSG();
				OS.MoveMemory(msg, lpMsg, MSG.sizeof);
				if (OS.TranslateAccelerator(hwnd, hAccel, msg) != 0) {
					return S_OK;
				}
			}
		}
		/*
		 * Block Ctrl+N (New window)
		 */
		MSG msg = new MSG();
		OS.MoveMemory(msg, lpMsg, MSG.sizeof);
		if (msg.message == OS.WM_KEYDOWN && msg.wParam == OS.VK_N
				&& OS.GetKeyState(OS.VK_CONTROL) < 0) {
			return S_OK;
		}
		return S_FALSE;
	}

	long TranslateUrl(long dwTranslate, long pchURLIn, long ppchURLOut) {
		return E_NOTIMPL;
	}

	long UpdateUI() {
		return E_NOTIMPL;
	}

	/* IOleCommandTarget */
	long QueryStatus(long pguidCmdGroup, long cCmds, long prgCmds, long pCmdText) {
		return E_NOTSUPPORTED;
	}

	long Exec(long pguidCmdGroup, long nCmdID, long nCmdExecOpt, long pvaIn,
			long pvaOut) {
		/*
		 * Disable script error dialog.
		 */
		// System.out.println("pguidCmdGroup="+pguidCmdGroup+", nCmdID="+nCmdID+", nCmdExecOpt="+nCmdExecOpt+", pvaIn="+pvaIn+", pvaOut="+pvaOut);
		if (pguidCmdGroup != 0 && nCmdID == OLECMDID_SHOWSCRIPTERROR) {
			boolean bDisable = ((WebBrowserIEComposite) (getParent()
					.getParent())).bDisableScriptDebugger;
			if (bDisable) {
				GUID guid = new GUID();
				COM.MoveMemory(guid, pguidCmdGroup, GUID.sizeof);
				if (COM.IsEqualGUID(guid, COM.CGID_DocHostCommandHandler)) {
					return S_OK;
				}
			}
		}
		return E_NOTSUPPORTED;
	}

	/* IServiceProvider */

	long QueryService(long guidService, long riid,
			long ppvObject) {
		if (riid == 0 || ppvObject == 0)
			return COM.E_INVALIDARG;
		GUID guid = new GUID();
		COM.MoveMemory(guid, riid, GUID.sizeof);
		if (COM.IsEqualGUID(guid, COM.IIDIInternetSecurityManager)) {
			COM.MoveMemory(ppvObject,
					new long [] { iInternetSecurityManager
							.getAddress() }, OS.PTR_SIZEOF);
			AddRef();
			return COM.S_OK;
		}
		COM.MoveMemory(ppvObject, new long [] { 0 }, OS.PTR_SIZEOF);
		return COM.E_NOINTERFACE;
	}

	/* IInternetSecurityManager */

	long SetSecuritySite(long pSite) {
		return INET_E_DEFAULT_ACTION;
	}

	long GetSecuritySite(long ppSite) {
		return INET_E_DEFAULT_ACTION;
	}

	long MapUrlToZone(long pwszUrl, long pdwZone, long dwFlags) {
		// TODO about:blank and non trusted text case
		// COM.MoveMemory(pdwZone, new int[] { URLZONE_INTRANET }, 4);
		// return COM.S_OK;
		
		return INET_E_DEFAULT_ACTION;
	}

	long GetSecurityId(long pwszUrl, long pbSecurityId,
			long pcbSecurityId, long dwReserved) {
		return INET_E_DEFAULT_ACTION;
	}

	long ProcessUrlAction(long pwszUrl, long dwAction,
			long pPolicy, long cbPolicy, long pContext,
			long cbContext, long dwFlags, long dwReserved) {
		ignoreNextMessage = false;

		int policy = URLPOLICY_ALLOW;

		if (dwAction >= URLACTION_JAVA_MIN && dwAction <= URLACTION_JAVA_MAX) {
			// policy = URLPOLICY_JAVA_PROHIBIT;
			policy = URLPOLICY_JAVA_LOW;
			ignoreNextMessage = true;
		}

		if (dwAction == URLACTION_ACTIVEX_RUN && pContext != 0) {
			GUID guid = new GUID();
			COM.MoveMemory(guid, pContext, GUID.sizeof);
			if (COM.IsEqualGUID(guid, COM.IIDJavaBeansBridge)
					|| COM.IsEqualGUID(guid, COM.IIDShockwaveActiveXControl)) {
				policy = URLPOLICY_DISALLOW;
				ignoreNextMessage = true;
			}
		}
		if (cbPolicy >= 4)
			COM.MoveMemory(pPolicy, new int[] { policy }, 4);
		return policy == URLPOLICY_ALLOW ? COM.S_OK : COM.S_FALSE;
	}

	long QueryCustomPolicy(long pwszUrl, long guidKey,
			long ppPolicy, long pcbPolicy,
			long pContext, long cbContext, long dwReserved) {
		return INET_E_DEFAULT_ACTION;
	}

	long SetZoneMapping(long dwZone, long lpszPattern, long dwFlags) {
		return INET_E_DEFAULT_ACTION;
	}

	long GetZoneMappings(long dwZone, long ppenumString, long dwFlags) {
		return COM.E_NOTIMPL;
	}

}
