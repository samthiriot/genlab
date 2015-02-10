package genlab.gui;


import genlab.gui.actions.ExportJavaAction;

import org.eclipse.debug.internal.ui.actions.RunLastAction;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.ide.IDEActionFactory;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 * 
 * TODO add run menu
 * 
 * TODO see http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fui%2Factions%2Fclass-use%2FActionFactory.IWorkbenchAction.html
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
	
	// file menu 
	private IWorkbenchAction newWizardDropDownAction;
	private IContributionItem newWizardMenu;
	private IWorkbenchAction openNewWizard;
    private IWorkbenchAction exportAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAllAction;
    private ExportJavaAction exportJavaAction;
    private IWorkbenchAction switchWorkspace;
    private IWorkbenchAction exitAction;
    
    // edit menu
    private IWorkbenchAction copyAction;
    private IWorkbenchAction pasteAction;
    private IWorkbenchAction deleteAction;
    private IWorkbenchAction selectAllAction;

    
    // window menu
    private IWorkbenchAction openPreferences;
    
    // help menu
    private IWorkbenchAction introAction;
    private IWorkbenchAction aboutAction;
    
    private IContributionItem viewShortListItem;
    private IContributionItem viewListItem;
    
    
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

    	//PlatformUI.getWorkbench().getExportWizardRegistry()

    	// file menu
    	newWizardDropDownAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);
    	register(newWizardDropDownAction);

    	newWizardMenu = ContributionItemFactory.NEW_WIZARD_SHORTLIST.create(window);
    	
        openNewWizard = ActionFactory.NEW.create(window);
        register(openNewWizard);
        
        switchWorkspace = IDEActionFactory.OPEN_WORKSPACE.create(window);
        register(switchWorkspace);
        
        exportAction = ActionFactory.EXPORT.create(window);
        register(exportAction);
        
        saveAction = ActionFactory.SAVE.create(window);
        register(saveAction);
        
        exportJavaAction = new ExportJavaAction();
        register(exportAction);
        
        // TODO print ? 
        saveAllAction = ActionFactory.SAVE_ALL.create(window);
        register(saveAllAction);
        
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        
        // edit menu
        // TODO undo ?
        copyAction = ActionFactory.COPY.create(window);
        register(copyAction);
        pasteAction = ActionFactory.PASTE.create(window);
        register(pasteAction);
        deleteAction = ActionFactory.DELETE.create(window);
        register(deleteAction);
        selectAllAction = ActionFactory.SELECT_ALL.create(window);
        register(selectAllAction);
        
        // window menu
        
        viewShortListItem =  ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        viewListItem =  ContributionItemFactory.VIEWS_SHOW_IN.create(window);
        openPreferences = ActionFactory.PREFERENCES.create(window);
        register(openPreferences);
       
        // help menu

        introAction = ActionFactory.INTRO.create(window);
        register(introAction);
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
      
        //ActionFactory.NEW_EDITOR
        
        
        
    }
    
    protected void fillMenuBar(IMenuManager menuBar) {
    	
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
        
        
        menuBar.add(fileMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(editMenu);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
        
        // create the New submenu, using the same id for it as the New action
        MenuManager fileNewMenu = new MenuManager("New", "new");
        fileNewMenu.add(this.newWizardMenu);
        fileMenu.add(fileNewMenu);

        // File
        fileMenu.add(openNewWizard);
        fileMenu.add(new Separator());
        fileMenu.add(exportAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveAction);
        fileMenu.add(saveAllAction);
        fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        fileMenu.add(exportJavaAction);
        fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        fileMenu.add(switchWorkspace);
        windowMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

       
        // Edit
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.add(deleteAction);
        editMenu.add(new Separator());
        editMenu.add(selectAllAction);
        editMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        // required to avoid bug "can't find IDfind.ext"
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT)); 

        // Windows
        windowMenu.add(viewListItem);
        windowMenu.add(viewShortListItem);
        windowMenu.add(openPreferences);
        windowMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        
        // Help
        helpMenu.add(introAction);
        helpMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        helpMenu.add(aboutAction);
        helpMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {

    	IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        toolbar.add(newWizardDropDownAction);

        coolBar.add(toolbar);   
        
        //coolBar.add(new ToolBarContributionItem(toolbar, "main"));   
        
    }
}
