package org.nidget.eclipse.djeclipse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nidget.eclipse.plugin.djeclipse"; //$NON-NLS-1$
	
	private static final String DEFAULT_BUNDLE_KEY = "org.nidget.eclipse.plugin.djeclipse"; //$NON-NLS-1$
	private static final String DEBUG_EVENTS = DEFAULT_BUNDLE_KEY + "/debug/events"; //$NON-NLS-1$
	private static final String DEBUG_PROCESS = DEFAULT_BUNDLE_KEY + "/debug/process"; //$NON-NLS-1$
	
	private static final String PLUGIN_CONSOLE_NAME = "DJEclipse";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {				
		super.start(context);
		plugin = this;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static boolean debugEventsEnabled() {
		return debugEnabled(DEBUG_EVENTS);
	}
	
	public static boolean debugProcessEnabled() {
		return debugEnabled(DEBUG_PROCESS);
	}
	
	public static boolean debugEvent(String message) {
		if(debugEventsEnabled() == false) {
			return false;
		}
		debug(message);
		return true;
	}
	
	public static boolean debugProcess(String message) {
		if(debugProcessEnabled() == false) {
			return false;
		}
		debug(message);
		return true;
	}
	
	private static boolean debugEnabled(String debugKey) {
		String debugOption = Platform.getDebugOption(debugKey);
		if(getDefault().isDebugging() && "true".equalsIgnoreCase(debugOption)) {
			return true;
		}
		return false;
	}
	
	private static void debug(String message) {
		StringBuilder text = new StringBuilder(getBundleKey());
		text.append(": ");
		text.append(message);
		System.out.println(text.toString());
	}
	
	private static MessageConsole findConsole() {
		return findConsole(PLUGIN_CONSOLE_NAME);
	}
	
	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
	
	private static String getBundleKey() {
		if(getDefault() == null) {
			return DEFAULT_BUNDLE_KEY;
		}
		Bundle debugBundle = getDefault().getBundle();
		if(debugBundle == null) {
			return DEFAULT_BUNDLE_KEY;
		}
		return debugBundle.getSymbolicName();
	}
	
	public static boolean printToPluginConsole(String message) {
		if(message == null || message.length() == 0) {
			return false;
		}
		MessageConsole myConsole = findConsole();
		MessageConsoleStream out = myConsole.newMessageStream();		
		out.println(message);
		showPluginConsole();
		return true;
	}
	
	private static void showPluginConsole() {
		 IWorkbench wb = PlatformUI.getWorkbench();
		 if(wb == null) {
			 
		 }
		 IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		 IWorkbenchPage page = win.getActivePage();
		 if(page == null) {
			 // if no active page, don't show the console
			 return;
		 }
		
		IConsole myConsole = findConsole();
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		IConsoleView view = null;
		try {
			view = (IConsoleView)page.showView(id);
		} catch (PartInitException pie) {
			debug(getStackTrace(pie));
		}
		view.display(myConsole);
	}
	
	public static String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	}
	 
}
