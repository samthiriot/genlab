package genlab.gui.views;

import genlab.core.usermachineinteraction.IListOfMessagesListener;
import genlab.core.usermachineinteraction.ITextMessage;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.ListsOfMessages;
import genlab.gui.VisualResources;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Locale;

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
 * TODO add exportation to a text file ?!
 * 
 * @author Samuel Thiriot
 *
 */
public class MessagesView extends ViewPart  {

	public static final String ID = "genlab.gui.views.MessagesView";

	
	public static final DateFormat DATE_FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault()); 


	private MyViewerComparator comparator = null;
	private TableViewer viewer = null;
	
	public MessagesView() {
	}

	private class MessagesContentProvider implements IStructuredContentProvider {

		private final ListOfMessages list;
		
		public MessagesContentProvider(ListOfMessages list) {
			this.list = list;
		}
		
		@Override
		public void dispose() {
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			// nothing to do (read only !)
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return list.asArray();
		}

		
	}
	
	/**
	 * Basic label provider that manages colors
	 * 
	 * @author Samuel Thiriot
	 *
	 */
	protected class ColorColumnProvider extends ColumnLabelProvider {
		
		  @Override
		  public Color getForeground(Object element) {
			    ITextMessage message = (ITextMessage)element;
			    
			    switch (message.getLevel()) {
			    case DEBUG:
			    case TRACE:
			    	return VisualResources.COLOR_GRAY;
			    case ERROR:
			    	return VisualResources.COLOR_RED;
			    default:
			    	return null;
			    }
			    
		  }

	}
	
	/**
	 * Provides comparison for sorting.
	 *
	 */
	public class MyViewerComparator extends ViewerComparator {
		
		  private int propertyIndex;
		  private static final int DESCENDING = 1;
		  private int direction = DESCENDING;

		  public MyViewerComparator() {
		    this.propertyIndex = 2; // sort by date by default
		    direction = DESCENDING;
		  }

		  public int getDirection() {
		    return direction == 1 ? SWT.DOWN : SWT.UP;
		  }

		  public void setColumn(int column) {
		    if (column == this.propertyIndex) {
		      // Same column as last sort; toggle the direction
		      direction = 1 - direction;
		    } else {
		      // New column; do an ascending sort
		      this.propertyIndex = column;
		      direction = DESCENDING;
		    }
		  }

		  @Override
		  public int compare(Viewer viewer, Object e1, Object e2) {
		    ITextMessage p1 = (ITextMessage) e1;
		    ITextMessage p2 = (ITextMessage) e2;
		    int rc = 0;
		    switch (propertyIndex) {
		    case 0:
		      rc = p1.getLevel().compareTo(p2.getLevel());
		      break;
		    case 1:
		    	rc = p1.getAudience().compareTo(p2.getAudience());
		      break;
		    case 2:
		      rc = p1.getTimestamp().compareTo(p2.getTimestamp());
		      break;
		    case 3:
		    	rc = p1.getEmitter().getCanonicalName().compareTo(p2.getEmitter().getCanonicalName());
		      break;
		    case 4:
		    	rc = p1.getMessage().compareTo(p2.getMessage());
		      break;
		    default:
		      rc = 0;
		    }
		    // If descending order, flip the direction
		    if (direction == DESCENDING) {
		      rc = -rc;
		    }
		    return rc;
		  }

		} 
	
	/**
	 * Listens for click events on the table, and changes sorting
	 * @param column
	 * @param index
	 * @return
	 */
	 private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		    SelectionAdapter selectionAdapter = new SelectionAdapter() {
		      @Override
		      public void widgetSelected(SelectionEvent e) {
		    	  if (comparator == null || viewer == null)
		    		  return;
		    	  
		        comparator.setColumn(index);
		        int dir = comparator.getDirection();
		        viewer.getTable().setSortDirection(dir);
		        viewer.refresh();
		      }
		    };
		    return selectionAdapter;
	}
	 
	/**
	 * Returns the list of messages tracked by this view
	 * @return
	 */
	public ListOfMessages getListOfMessages() {
		return ListsOfMessages.getGenlabMessages();
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		
		viewer = new TableViewer(
				parent, 
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.READ_ONLY
				);

		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);


		// configure viewer
		
		comparator = new MyViewerComparator();
		
	    viewer.setComparator(comparator);

		// ... columns
		{
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(100);
			col.getColumn().setText("Level");
			col.setLabelProvider(new ColorColumnProvider() {
			  @Override
			  public String getText(Object element) {
			    ITextMessage message = (ITextMessage)element;
			    return message.getLevel().toString();
			  }
				
			  @Override
				public Image getImage(Object element) {
				    ITextMessage message = (ITextMessage)element;

				  switch (message.getLevel()) {
				  case TRACE:
				  case DEBUG:
					  return VisualResources.ICON_DEBUG;
				  case TIP:
					  return VisualResources.ICON_TIP;
				  case ERROR:
					  return VisualResources.ICON_ERROR;
				  case INFO:
					  return VisualResources.ICON_INFO;
				  case WARNING:
					  return VisualResources.ICON_WARNING;
				  default:
					  return null;
				  }

				}
			});
			col.getColumn().addSelectionListener(getSelectionAdapter(col.getColumn(), 0));

		}
		{
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(100);
			col.getColumn().setText("Audience");
			col.setLabelProvider(new ColorColumnProvider() {

			@Override
			  public String getText(Object element) {
			    ITextMessage message = (ITextMessage)element;
			    return message.getAudience().toString();
			  }
			});
			col.getColumn().addSelectionListener(getSelectionAdapter(col.getColumn(), 1));

		}
		{
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(100);
			col.getColumn().setText("When");
			col.setLabelProvider(new ColorColumnProvider() {
			  @Override
			  public String getText(Object element) {
			    ITextMessage message = (ITextMessage)element;
			    return DATE_FORMAT.format(message.getDate());
			  }
			});
			col.getColumn().addSelectionListener(getSelectionAdapter(col.getColumn(), 2));

		}
		{
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(300);
			col.getColumn().setText("From");
			
			col.setLabelProvider(new ColorColumnProvider() {
				
				@Override
				public String getToolTipText(Object element) {
					ITextMessage message = (ITextMessage)element;
					return message.getEmitter().getCanonicalName();
				}
				

				public int getToolTipDisplayDelayTime(Object object) {
					return 500;
				}

				public int getToolTipTimeDisplayed(Object object) {
					return 10000;
				}

				  @Override
				  public String getText(Object element) {
				    ITextMessage message = (ITextMessage)element;
				    return message.getEmitter().getCanonicalName();
				  }
			  
			  
			  
			});
			
			col.getColumn().addSelectionListener(getSelectionAdapter(col.getColumn(), 3));
			
		}
		{
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(400);
			col.getColumn().setText("Message");
			
			col.setLabelProvider(new ColorColumnProvider() {
				
				@Override
				public String getToolTipText(Object element) {
					ITextMessage message = (ITextMessage)element;
					if (message.getException() != null) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						message.getException().printStackTrace(pw);
						return sw.toString(); 
					} else
						return null;
				}
				

				public int getToolTipDisplayDelayTime(Object object) {
					return 500;
				}

				public int getToolTipTimeDisplayed(Object object) {
					return 10000;
				}

				  @Override
				  public String getText(Object element) {
				    ITextMessage message = (ITextMessage)element;
				    if (message.getCount() > 1) {
				    	StringBuffer sb = new StringBuffer();
				    	return sb
					    		.append("(")					//$NON-NLS-1$
					    		.append(message.getCount())
					    		.append(" times") 
					    		.append(") ")					//$NON-NLS-1$
					    		.append(message.getMessage())
					    		.toString();
					  } else
				    	return message.getMessage();
				  	}
			  
			  
			  
			});
			
			col.getColumn().addSelectionListener(getSelectionAdapter(col.getColumn(), 4));
			
		}
		
		// configure table
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 

		// ... listen for data
		viewer.setContentProvider(new MessagesContentProvider(ListsOfMessages.getGenlabMessages()));
		viewer.setInput("toto");
		
		IListOfMessagesListener listener = new IListOfMessagesListener() {
			
			@Override
			public void contentChanged(ListOfMessages list) {

				if (parent.isDisposed()) {
					ListsOfMessages.getGenlabMessages().removeListener(this);
					return;
				}
				
				final IListOfMessagesListener myThis = this;
				
				parent.getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						
						if (parent.isDisposed()) {
							ListsOfMessages.getGenlabMessages().removeListener(myThis);
							return;
						}
						
						try {
							viewer.setInput("toto");
			
							viewer.refresh();
							
						} catch (RuntimeException e) {
								// TODO manage disposed exception
							e.printStackTrace();
						}
					}
				});
				
			}

			@Override
			public void messageAdded(ListOfMessages list, ITextMessage message) {
				// TODO Auto-generated method stub
				
			}
		};
		
		ListsOfMessages.getGenlabMessages().addListener(listener);

		
	}
	


	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}


}
