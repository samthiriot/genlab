package genlab.gui.preferences;


import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.core.usermachineinteraction.MessageLevel;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class MessageLevelForEmittersFieldEditor extends FieldEditor {

    /**
     * The list widget; <code>null</code> if none
     * (before creation or after disposal).
     */
    private Table table;
    private TableViewer tableViewer;
    
    private EmitterAndLevel[] decodeFromString(String list) {
    	
    	String[] tok = list.split(",");
    	EmitterAndLevel[] res = new EmitterAndLevel[tok.length];
    	for (int i=0; i<tok.length; i++) {
    		res[i] = EmitterAndLevel.fromString(tok[i]);
    	}
    	return res;
    }
    

    private EmitterAndLevel[] createFromCurrent() {
    	
    	SortedMap<String,MessageLevel> emitter2level = new TreeMap<String, MessageLevel>(ListOfMessages.getClassnameAndLevel());
    	EmitterAndLevel[] res = new EmitterAndLevel[emitter2level.size()];
    	int i = 0;
    	for (Entry<String,MessageLevel> c: emitter2level.entrySet()) {
    		res[i++] = new EmitterAndLevel(c.getKey(), c.getValue());
    	}
    	return res;
    }
    
    
	public MessageLevelForEmittersFieldEditor() {
	}

	public MessageLevelForEmittersFieldEditor(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) table.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		
		Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        table = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        table.setLayoutData(gd);
        

	}

	@Override
	protected void doLoad() {
		 if (table != null) {
            String s = getPreferenceStore().getString(getPreferenceName());
        	tableViewer.setInput(createFromCurrent());

        }
	}

	@Override
	protected void doLoadDefault() {
		if (table != null) {
            table.removeAll();
            String s = getPreferenceStore().getDefaultString(getPreferenceName());
        	tableViewer.setInput(decodeFromString(s));
        }
	}

	@Override
	protected void doStore() {
		StringBuffer sb = new StringBuffer();
		ArrayContentProvider acp = null;
		EmitterAndLevel[] data = (EmitterAndLevel[]) tableViewer.getInput();
		for (EmitterAndLevel eAl: data) {
			if (eAl.level == null)
				continue;
			if (sb.length() > 0)
				sb.append(",");
			sb.append(eAl.toSaveString());
		}
		getPreferenceStore().setValue(getPreferenceName(), sb.toString());
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	private String[] getMessageLevelsAsString() {
		String[] res = new String[MessageLevel.values().length+1];
		
		res[0] = "<default>";
		
		for (int i=0; i<MessageLevel.values().length; i++) {
			res[i+1] = MessageLevel.values()[i].toString();
		}
		
		return res;
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


            table = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
            table.setFont(parent.getFont());
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(tableLayout);
            
        	tableViewer = new TableViewer(table);

        	tableViewer.setColumnProperties(new String[] {"emitter", "level" });

        	tableViewer.setCellModifier(new ICellModifier() {
				
        	      public boolean canModify(Object element, String property) {
        	    	  return "level".equals(property);
        	      }

        	      public Object getValue(Object element, String property) {
        	    	  EmitterAndLevel item = (EmitterAndLevel)element;
        	    	  if ("emitter".equals(property))
        	    		  return item.emitter;
        	    	  else if ("level".equals(property))
        	    		  return item.level;
        	    	  return null;
        	      }

        	      public void modify(Object element, String property, Object value) {
        	    	  TableItem item = (TableItem)element;
        	    	  EmitterAndLevel e = (EmitterAndLevel) item.getData();
        	        
        	    	  if ("emitter".equals(property))
        	    		  e.emitter = (String) value;
        	    	  else if ("level".equals(property))
	        	          e.level = (MessageLevel)value;
	        	        
        	        tableViewer.refresh();
        	      }
        	    });

        	TableViewerColumn column1 = new TableViewerColumn(tableViewer, SWT.NONE);
        	column1.getColumn().setText("message emitter");


        	TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
        	column2.getColumn().setText("level");
        	column2.setEditingSupport(new EditingSupport(tableViewer) {
        		@Override
    			protected void setValue(Object element, Object value) {
        			// value is an integer with the index of what was selected
    				final Integer index = (Integer)value;
        			if (index == 0) {
        				// corresponds to "default"
        				((EmitterAndLevel)element).level = null;
        			} else {
        				((EmitterAndLevel)element).level = MessageLevel.values()[index-1];	
        			}
    				getViewer().update(element, null);
    			}

    			@Override
    			protected Object getValue(Object element) {
    				final EmitterAndLevel edited = (EmitterAndLevel) element;
    				if (edited.level == null)
    					return 0;
    				else 
    					return edited.level.ordinal()+1;
    			}

    			@Override
    			protected CellEditor getCellEditor(Object element) {
    				return new ComboBoxCellEditor(
    						(Composite) getViewer().getControl(), 
    						getMessageLevelsAsString()
    						);
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
					return property.equals("emitter") || property.equals("level");
				}
				
				@Override
				public void dispose() {
				}
				
				@Override
				public void addListener(ILabelProviderListener listener) {
				}
				
				@Override
				public String getColumnText(Object element, int columnIndex) {
					EmitterAndLevel data = (EmitterAndLevel) element;
    			    switch (columnIndex) {
    			      case 0:
    			    	 return data.emitter;
    			      case 1:
    			    	  if (data.level == null) 
    			    		  return "<default>";
    			    	  else 
    			    		  return data.level.toString();
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
        	
        	table.getColumn(1).pack();
        	
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
    
    public static String getSaveForEmptyList() {
    	return "";
    }
    
    
}
