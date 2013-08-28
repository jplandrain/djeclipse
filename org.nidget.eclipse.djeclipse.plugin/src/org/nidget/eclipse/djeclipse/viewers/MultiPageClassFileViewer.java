package org.nidget.eclipse.djeclipse.viewers;


import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.javaeditor.ExternalClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.InternalClassFileEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.nidget.eclipse.djeclipse.Activator;
import org.nidget.eclipse.djeclipse.decompilers.cfr.CFRDecompiler;
import org.nidget.eclipse.djeclipse.decompilers.exceptions.DecompilerException;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
@SuppressWarnings("restriction")
public class MultiPageClassFileViewer extends MultiPageEditorPart implements IResourceChangeListener {


	private StyledText CFRText;
	
	private IPath path;	
	
	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageClassFileViewer() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	/*void createPage0() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
	}*/
	
	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
	/*void createPage1() {

		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		Button fontButton = new Button(composite, SWT.NONE);
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		fontButton.setLayoutData(gd);
		fontButton.setText("Change Font...");
		
		fontButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setFont();
			}
		});

		int index = addPage(composite);
		setPageText(index, "Properties");
	}*/
	
	/**
	 * Creates page 2 of the multi-page editor,
	 * which shows the sorted text.
	 */
	/*void createPage2() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		int index = addPage(composite);
		setPageText(index, "Preview");
	}*/
	
	void createCFRPage() {		
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		CFRText = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		CFRText.setEditable(false);
		
		Menu menu = new Menu(CFRText.getShell(), SWT.POP_UP);
		MenuItem copy = new MenuItem(menu, SWT.PUSH);
		copy.setText("Copy");
		copy.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				Activator.debugEvent("Copy");
				String selectedText = CFRText.getSelectionText();
				if(selectedText == null || selectedText.length() == 0) {
					Activator.printToPluginConsole("Nothing selected");
					return;
				}
				Activator.printToPluginConsole("Copying selected content to the clipboard");
				// TODO
			}
		});

		//  Create the separator
	    new MenuItem(menu, SWT.SEPARATOR);
	    
		MenuItem selectAll = new MenuItem(menu, SWT.PUSH);
		selectAll.setText("Select All");		
		selectAll.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				Activator.debugEvent("Select All");
				// TODO
			}
		});

		MenuItem invertSelection = new MenuItem(menu, SWT.PUSH);
		invertSelection.setText("Invert Selection");
		invertSelection.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				Activator.debugEvent("Invert Selection");
				// TODO
			}
		});
		
		CFRText.setMenu(menu);		

		CFRText.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				StyledText st = (StyledText) event.widget;

				Menu menu = st.getMenu();
				menu.setVisible(true);
				
				event.doit = false;
			}
		});

		
		final String headerText = String.format("Class File Reader (CFR) decompiler by lee@benf.org | http://www.benf.org%n%n");
		final Display display = Display.getCurrent();
		
		StyledTextContent content = CFRText.getContent();
		content.setText(headerText);
		StyleRange headerStyle = new StyleRange();		
		headerStyle.start = 0;
		headerStyle.length = headerText.length();
		headerStyle.fontStyle = SWT.ITALIC;
		headerStyle.foreground = display.getSystemColor(SWT.COLOR_GRAY);
		CFRText.setStyleRange(headerStyle);
		
		StringBuilder strPath = new StringBuilder("Decompiling class file at ");
		strPath.append(path);
		String logMsg = strPath.toString();
		Activator.debugProcess(logMsg);
		Activator.printToPluginConsole(logMsg);
		
		String cfrResult = null;
		try {
			cfrResult = CFRDecompiler.decompile(path.toString());
		} catch(DecompilerException dce) {
			cfrResult = Activator.getStackTrace(dce);
		}		
		CFRText.append(cfrResult);

		int index = addPage(composite);
		setPageText(index, "CFR");
	}
	
	
	/**
	 * Creates the pages of the multi-page editor.
	 */
	@Override
	protected void createPages() {
		createCFRPage();
		
		if (getPageCount() == 1) {
			Composite container = getContainer();
			if (container instanceof CTabFolder) {
				((CTabFolder) container).setTabHeight(0);
			}
		}
	}
	
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {		
		super.init(site, editorInput);		
		
		if(editorInput instanceof FileEditorInput == false &&
				editorInput instanceof FileStoreEditorInput == false &&
				editorInput instanceof InternalClassFileEditorInput == false &&
				editorInput instanceof ExternalClassFileEditorInput == false) {
			throw new PartInitException("must be a class file");
		} else if(editorInput instanceof FileEditorInput) {
			FileEditorInput fileEditor = (FileEditorInput)editorInput;
			IFile file = fileEditor.getFile();
			if(!"class".equals(file.getFullPath().getFileExtension())) {
				StringBuilder errorMsg = new StringBuilder(file.getName());
				errorMsg.append(" is not a class file");
				throw new PartInitException(errorMsg.toString());
			}
			path = file.getLocation();
		} else if(editorInput instanceof FileStoreEditorInput) {
			FileStoreEditorInput fileEditor = (FileStoreEditorInput)editorInput;
			URI fileURI = fileEditor.getURI();
						
			//IFile file = fileEditor.get
			if(!fileURI.getPath().endsWith(".class")) {
				StringBuilder errorMsg = new StringBuilder(fileURI.getPath());
				errorMsg.append(" is not a class file");
				throw new PartInitException(errorMsg.toString());
			}
			path = new Path(fileURI.getPath());	
		} else if(editorInput instanceof InternalClassFileEditorInput) {
			InternalClassFileEditorInput classFileEditorInput = (InternalClassFileEditorInput)editorInput;
			path = classFileEditorInput.getPath();
		} else if(editorInput instanceof ExternalClassFileEditorInput) {
			ExternalClassFileEditorInput classFileEditorInput = (ExternalClassFileEditorInput)editorInput;
			path = classFileEditorInput.getPath();
		}
		
		// Set the title of the multi-editor tab with the filename
		// TODO
		setPartName(editorInput.getName());
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent rce) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub		
	}
			

}
