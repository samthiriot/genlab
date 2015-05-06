package genlab.gui;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author <a href="mailto:abba-best@mail.ru">Cherednik, Oleg</a>
 * @since 20.12.2010
 */
public class SpinnerCellEditor extends CellEditor {
	
        private final int multiplier;

        public SpinnerCellEditor(Composite parent, NumberFormat nf, int style, int min, int max) {
            super(parent, style);

            this.multiplier = (int)Math.pow(10, getControl().getDigits());

            postConstruct(nf, min, max);
        }

        private void postConstruct(NumberFormat nf, int min, int max)
        {
                Spinner spinner = getControl();

                spinner.setDigits(nf.getMaximumFractionDigits());
                spinner.setMinimum(min);
                spinner.setMaximum(max);
                spinner.setIncrement(1);
        
        }

        /*
         * CellEditor
         */

        @Override
        protected Spinner createControl(Composite parent)
        {
                final Spinner spinner = new Spinner(parent, SWT.NONE);
                
                spinner.addTraverseListener(new TraverseListener() {
					
					@Override
					public void keyTraversed(TraverseEvent e) {
						
						  if (e.detail == SWT.TRAVERSE_ESCAPE
			                        || e.detail == SWT.TRAVERSE_RETURN) {
			                    e.doit = false;
			                }
					}
				});
                spinner.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        SpinnerCellEditor.this.focusLost();
                    }
                });
                spinner.addKeyListener(new KeyListener() {
        			@Override
        			public void keyPressed(KeyEvent e) {
        				
        			}
        			@Override
        			public void keyReleased(KeyEvent e) {
        				if (e.character == '\n') {
        					SpinnerCellEditor.this.focusLost();
        				}
        				if (e.character == '\r') {
        					SpinnerCellEditor.this.focusLost();
        				}
        			}
        		});
        		

                spinner.setFont(parent.getFont());
                spinner.setBackground(parent.getBackground());

                return spinner;
        }

        @Override
        public Spinner getControl()
        {
                return (Spinner)super.getControl();
        }
        

        @Override
        protected Object doGetValue()
        {
                int intValue = getControl().getSelection();
                double doubleValue = (double)intValue;

                return doubleValue / multiplier;
        }

        @Override
        protected void doSetFocus()
        {
                getControl().setFocus();
        }

        @Override
        protected void doSetValue(Object value)
        {
                if(value == null)
                        return;

                double doubleValue = ((Number)value).doubleValue() * multiplier;
                int intValue = (int)Math.round(doubleValue);

                getControl().setSelection(intValue);
        }
        
        
}