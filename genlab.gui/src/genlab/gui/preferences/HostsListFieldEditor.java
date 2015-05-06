package genlab.gui.preferences;

import genlab.core.exec.client.ServerHostPreference;
import genlab.core.exec.server.GenlabComputationServer;
import genlab.gui.SpinnerCellEditor;

import java.text.NumberFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class HostsListFieldEditor extends FieldEditor {


    /**
     * The list widget; <code>null</code> if none
     * (before creation or after disposal).
     */
    private Table table;
    private TableViewer tableViewer;
    

    /**
     * The button box containing the Add, Remove, Up, and Down buttons;
     * <code>null</code> if none (before creation or after disposal).
     */
    private Composite buttonBox;

    /**
     * The Add button.
     */
    private Button addButton;

    /**
     * The Remove button.
     */
    private Button removeButton;

    /**
     * The Up button.
     */
    private Button upButton;

    /**
     * The Down button.
     */
    private Button downButton;

    /**
     * The selection listener.
     */
    private SelectionListener selectionListener;


    
    protected HostsListFieldEditor() {
    }
    
    /**
     * Creates a list field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    protected HostsListFieldEditor(String name, String labelText, Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Notifies that the Add button has been pressed.
     */
    private void addPressed() {
    	
        setPresentsDefaultValue(false);
        
        tableViewer.add(new ServerHostPreference(false, "192.168.0.1", GenlabComputationServer.DEFAULT_PORT));
        
        
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) table.getLayoutData()).horizontalSpan = numColumns - 1;
    }

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) {
        addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
        removeButton = createPushButton(box, "ListEditor.remove");//$NON-NLS-1$
        upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
        downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
    }

    /**
     * Helper method to create a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return Button
     */
    private Button createPushButton(Composite parent, String key) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        int widthHint = convertHorizontalDLUsToPixels(button,
                IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }

    /**
     * Creates a selection listener.
     */
    public void createSelectionListener() {
        selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                Widget widget = event.widget;
                if (widget == addButton) {
                    addPressed();
                } else if (widget == removeButton) {
                    removePressed();
                } else if (widget == upButton) {
                    upPressed();
                } else if (widget == downButton) {
                    downPressed();
                } 
            }
        };
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        table = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.verticalSpan  = 5;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        table.setLayoutData(gd);
        

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox.setLayoutData(gd);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoad() {
        if (table != null) {
            String s = getPreferenceStore().getString(getPreferenceName());
        	tableViewer.setInput(ServerHostPreference.parseAsArray(s));

        }
    }

    
    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoadDefault() {
        if (table != null) {
            table.removeAll();
            String s = getPreferenceStore().getDefaultString(getPreferenceName());
        	tableViewer.setInput(ServerHostPreference.parseAsArray(s));

        }
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doStore() {
    	
    	StringBuffer sb = new StringBuffer();
		for (TableItem item: table.getItems()) {
			if (sb.length() > 0)
				sb.append("/");
			sb.append(item.getText(0));
			sb.append("|");
			sb.append(item.getText(1));
			sb.append(":");
			sb.append(item.getText(2));
		}
		getPreferenceStore().setValue(getPreferenceName(), sb.toString());
    }

    /**
     * Notifies that the Down button has been pressed.
     */
    private void downPressed() {
        swap(false);
    }

    /**
     * Returns this field editor's button box containing the Add, Remove,
     * Up, and Down button.
     *
     * @param parent the parent control
     * @return the button box
     */
    public Composite getButtonBoxControl(Composite parent) {
        if (buttonBox == null) {
            buttonBox = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            buttonBox.setLayout(layout);
            createButtons(buttonBox);
            buttonBox.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    addButton = null;
                    removeButton = null;
                    upButton = null;
                    downButton = null;
                    buttonBox = null;
                }
            });

        } else {
            checkParent(buttonBox, parent);
        }

        selectionChanged();
        return buttonBox;
    }

    /**
     * Returns this field editor's list control.
     *
     * @param parent the parent control
     * @return the list control
     */
    public Table getListControl(Composite parent) {
        if (table == null) {
        	
        	TableLayout tableLayout = new TableLayout();
        	tableLayout.addColumnData(new ColumnWeightData(1));
        	tableLayout.addColumnData(new ColumnWeightData(2));
        	tableLayout.addColumnData(new ColumnWeightData(2));


            table = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
            table.setFont(parent.getFont());
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(tableLayout);
            
        	tableViewer = new TableViewer(table);

        	/*
        	tableViewer.setCellEditors(new CellEditor[] {
        			new CheckboxCellEditor(parent),
            		new TextCellEditor(parent),
                    new SpinnerCellEditor(parent, NumberFormat.getIntegerInstance(), SWT.NONE, 1000, 65535) }
            		);
*/
        	tableViewer.setColumnProperties(new String[] {"active", "hostname", "port" });

        	tableViewer.setCellModifier(new ICellModifier() {
				
        	      public boolean canModify(Object element, String property) {
        	        return true;
        	      }

        	      public Object getValue(Object element, String property) {
        	    	  ServerHostPreference item = (ServerHostPreference)element;
        	    	  if ("active".equals(property))
        	    		  return item.active;
        	    	  else if ("hostname".equals(property))
        	    		  return item.hostname;
        	    	  else
        	    		  return item.port;
        	      }

        	      public void modify(Object element, String property, Object value) {
        	    	  TableItem item = (TableItem)element;
        	    	  ServerHostPreference e = (ServerHostPreference) item.getData();
        	        
        	    	  if ("active".equals(property))
        	    		  e.active = (Boolean) value;
        	    	  else if ("hostname".equals(property))
	        	        	e.hostname = (String)value;
	        	        else
	        	        	e.port = (Integer)value;

        	        tableViewer.refresh();
        	      }
        	    });

        	TableViewerColumn column1 = new TableViewerColumn(tableViewer, SWT.NONE);
        	column1.getColumn().setText("active");
        	column1.setEditingSupport(new EditingSupport(tableViewer) {
        		@Override
    			protected void setValue(Object element, Object value) {
    				((ServerHostPreference)element).active = (Boolean)value;
    				getViewer().update(element, null);
    			}

    			@Override
    			protected Object getValue(Object element) {
    				return ((ServerHostPreference) element).active;
    			}

    			@Override
    			protected CellEditor getCellEditor(Object element) {
    				return new CheckboxCellEditor((Composite) getViewer().getControl());
    			}

    			@Override
    			protected boolean canEdit(Object element) {
    				return true;
    			}
			});

        	TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
        	column2.getColumn().setText("hostname");
        	column2.setEditingSupport(new EditingSupport(tableViewer) {
        		@Override
    			protected void setValue(Object element, Object value) {
    				((ServerHostPreference)element).hostname = (String)value;
    				getViewer().update(element, null);
    			}

    			@Override
    			protected Object getValue(Object element) {
    				return ((ServerHostPreference) element).hostname;
    			}

    			@Override
    			protected CellEditor getCellEditor(Object element) {
    				return new TextCellEditor((Composite) getViewer().getControl());
    			}

    			@Override
    			protected boolean canEdit(Object element) {
    				return true;
    			}
			});
        	
        	TableViewerColumn column3 = new TableViewerColumn(tableViewer, SWT.NONE);
        	column3.getColumn().setText("port");
        	column3.setEditingSupport(new EditingSupport(tableViewer) {
        		@Override
    			protected void setValue(Object element, Object value) {
    				((ServerHostPreference)element).port = ((Double)value).intValue();
    				getViewer().update(element, null);
    			}

    			@Override
    			protected Object getValue(Object element) {
    				return ((ServerHostPreference) element).port;
    			}

    			@Override
    			protected CellEditor getCellEditor(Object element) {
    				return new SpinnerCellEditor((Composite) getViewer().getControl(), NumberFormat.getIntegerInstance(), SWT.NONE, 1000, 65535);
    			}

    			@Override
    			protected boolean canEdit(Object element) {
    				return true;
    			}
			});
        	tableViewer.setLabelProvider(new ITableLabelProvider() {
				
				@Override
				public void removeListener(ILabelProviderListener listener) {
				}
				
				@Override
				public boolean isLabelProperty(Object element, String property) {
					return property.equals("active") || property.equals("hostname") || property.equals("port");
				}
				
				@Override
				public void dispose() {
				}
				
				@Override
				public void addListener(ILabelProviderListener listener) {
				}
				
				@Override
				public String getColumnText(Object element, int columnIndex) {
					ServerHostPreference data = (ServerHostPreference) element;
    			    switch (columnIndex) {
    			      case 0:
    			    	 return Boolean.toString(data.active);
    			      case 1:
    			        return data.hostname;
    			      case 2:
    			        return Integer.toString(data.port);
    			      default:
    			        return "";
    			    }
				}
				
				@Override
				public Image getColumnImage(Object element, int columnIndex) {
					return null;
				}
        	});

        	tableViewer.setContentProvider(new ArrayContentProvider());
        	

            table.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    table = null;
                }
            });
        } else {
            checkParent(table, parent);
        }
        return table;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public int getNumberOfControls() {
        return 2;
    }

    /**
     * Returns this field editor's selection listener.
     * The listener is created if nessessary.
     *
     * @return the selection listener
     */
    private SelectionListener getSelectionListener() {
        if (selectionListener == null) {
			createSelectionListener();
		}
        return selectionListener;
    }

    /**
     * Returns this field editor's shell.
     * <p>
     * This method is internal to the framework; subclassers should not call
     * this method.
     * </p>
     *
     * @return the shell
     */
    protected Shell getShell() {
        if (addButton == null) {
			return null;
		}
        return addButton.getShell();
    }

    /**
     * Notifies that the Remove button has been pressed.
     */
    private void removePressed() {
        setPresentsDefaultValue(false);
        int index = table.getSelectionIndex();
        if (index >= 0) {
            table.remove(index);
            selectionChanged();
        }
    }

	/**
	 * Invoked when the selection in the list has changed.
	 * 
	 * <p>
	 * The default implementation of this method utilizes the selection index
	 * and the size of the list to toggle the enablement of the up, down and
	 * remove buttons.
	 * </p>
	 * 
	 * <p>
	 * Sublcasses may override.
	 * </p>
	 * 
	 * @since 3.5
	 */
    protected void selectionChanged() {

        int index = table.getSelectionIndex();
        int size = table.getItemCount();

        removeButton.setEnabled(index >= 0);
        upButton.setEnabled(size > 1 && index > 0);
        downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public void setFocus() {
        if (table != null) {
            table.setFocus();
        }
    }

    /**
     * Moves the currently selected item up or down.
     *
     * @param up <code>true</code> if the item should move up,
     *  and <code>false</code> if it should move down
     */
    private void swap(boolean up) {
        setPresentsDefaultValue(false);
        int index = table.getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0) {
            TableItem[] selection = table.getSelection();
            Assert.isTrue(selection.length == 1);
            TableItem previous = table.getItem(index);
        	TableItem item = new TableItem (table, SWT.NONE, target+1);
        	item.setChecked(previous.getChecked());
        	for (int i=0; i<table.getColumnCount(); i++) {
        		item.setText(i, previous.getText(i));
        	}
            table.remove(index);
        	table.setSelection(target);
        }
        selectionChanged();
    }

    /**
     * Notifies that the Up button has been pressed.
     */
    private void upPressed() {
        swap(true);
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        getListControl(parent).setEnabled(enabled);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
    }
    
    /**
     * Return the Add button.  
     * 
     * @return the button
     * @since 3.5
     */
    protected Button getAddButton() {
    	return addButton;
    }
    
    /**
     * Return the Remove button.  
     * 
     * @return the button
     * @since 3.5
     */
    protected Button getRemoveButton() {
    	return removeButton;
    }
    
    /**
     * Return the Up button.  
     * 
     * @return the button
     * @since 3.5
     */
    protected Button getUpButton() {
    	return upButton;
    }
    
    /**
     * Return the Down button.  
     * 
     * @return the button
     * @since 3.5
     */
    protected Button getDownButton() {
    	return downButton;
    }
    
    /**
     * Return the List.
     * 
     * @return the list
     * @since 3.5
     */
    protected Table getList() {
    	return table;
    }

	
	
}
