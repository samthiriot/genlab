package genlab.gui.views;

import genlab.core.usermachineinteraction.GLLogger;
import genlab.core.usermachineinteraction.IListOfMessagesListener;
import genlab.core.usermachineinteraction.ITextMessage;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.gui.VisualResources;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Locale;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays messages as a table. 
 * Once created an initialized, this view will wait for the property PROPERTY_MESSAGES_ID.
 * If PROPERTY_VALUE_GLOBAL is provided as a value, the global messages from 
 * genlab are listened. Else, the view will attempt to find the messages provided. 
 * 
 * TODO add exportation to a text file ?!
 * 
 * @author Samuel Thiriot
 *
 */
public class MessagesView extends MessagesViewGeneral implements IPropertyChangeListener  {

	public static final String ID = "genlab.gui.views.MessagesView";

	public static final String PROPERTY_MESSAGES_ID = "message_to_observe";
	
	
	public MessagesView() {
		addPartPropertyListener(this);
	}
 
	
	protected void listenMessages() {
		
		String value = (String)getPartProperty(PROPERTY_MESSAGES_ID);
		
		if (value == null || listener == null)
			// the view is not yet initialized; lets' wait
			return;
		

		GLLogger.traceTech("should show the list of messages for id "+value+"", getClass());

		messages = ListsOfMessages.getListOfMessages(value);
		if (messages==null) {
			GLLogger.warnTech("unable to find a list of messages for id "+value+"; the messages will not be displayed", getClass());
			return;
		}
		messages.addListener(listener);
		
		// ... listen for data
		viewer.setContentProvider(new MessagesContentProvider(messages));
		viewer.setInput("toto");
		
	
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		if (event.getProperty().equals(PROPERTY_MESSAGES_ID)) {
			listenMessages();
		}
		
		
	}


}
