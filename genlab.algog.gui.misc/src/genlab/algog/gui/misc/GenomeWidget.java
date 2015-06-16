package genlab.algog.gui.misc;

import genlab.algog.algos.meta.GeneticExplorationAlgoConstants;
import genlab.core.model.meta.basics.flowtypes.GenlabTable;
import genlab.core.usermachineinteraction.ListOfMessages;
import genlab.gui.Utils;
import genlab.gui.VisualResources;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolTip;

/**
 * TODO tooltip with actual value!
 * 
 * @author Samuel Thiriot
 *
 */
public class GenomeWidget extends Canvas {

	protected GenlabTable lastVersionDataToDisplay = null;
	protected ListOfMessages messages = null;
	
	/**
	 * Associates each gene to display with the column where to get its value
	 */
	//protected Map<String,String> gene2columnIdForValue = new LinkedHashMap<String,String>();
	
	protected Map<String,String> goal2columnIdForFitness = new LinkedHashMap<String,String>();
	
	protected String columnIdForIteration = null;
	
	protected Integer iterationToDisplay = null;
	
	protected int heightText = 10;
	protected final int heightLine = 10;
	protected int paddingVertical = 2;
	protected int titleHeight = heightLine;
	
	protected final Display display; 
	
	/**
	 * List of all the colors
	 */
	protected Map<Integer,Color> colors = new HashMap<Integer, Color>();

	protected String[] geneIdx2columnValue;
	protected  Gradient[] geneIdx2gradient;
	
	/**
	 * The height required to display data
	 */
	protected int requiredHeight = 800;
	protected int requiredWidth = 400;

	protected int rowLast = 0;
	protected int rowFirst = 0;
	protected int widthPerGene;
    final ToolTip tip;


	/**
	 * Should be called from the SWT thread
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	private Color getOrCreateColor(int red, int green, int blue) {
		
		Integer total = red*90000 + green*300 + blue;
		
		Color color = colors.get(total);
		if (color == null) {
			color = new Color(display, red, green, blue);
			colors.put(total, color);
		}
		
		return color;
		
	}
	
	/**
	 * Disposes all the colors defined by the widget.
	 */
	private void disposeColors() {
		for (Color color: colors.values()) {
			color.dispose();
		}
	}
	
	
	/**
	 * Stores a gradient, that is something able, from a min and max value
	 * and two colors, to give a color for any value
	 * @author Samuel Thiriot
	 *
	 */
	protected final class Gradient {
		
		public final double min;
		public final double max;
		public final Color colorStart;
		public final Color colorEnd;
		//public final double steps = 40;
		
		public Gradient(double min, double max, Color colorStart, Color colorEnd) {
			super();
			this.min = min;
			this.max = max;
			this.colorStart = colorStart;
			this.colorEnd = colorEnd;
		}
		
		public Color getColorForValue(double v) {
			
            double ratio = (v-min) / (max-min); // TODO steps ? 
            int red = (int) (colorEnd.getRed() * ratio + colorStart.getRed() * (1 - ratio));
			int green = (int) (colorEnd.getGreen() * ratio + colorStart.getGreen() * (1 - ratio));
			int blue = (int) (colorEnd.getBlue() * ratio + colorStart.getBlue() * (1 - ratio));
  			
			return getOrCreateColor(red, green, blue);
		}
		
	}
	
	/**
	 * From the current table, loads into gene2columnIdForValue the metadata required for a display
	 * @return true if success
	 */
	protected boolean loadMetadataForGenes() {

		// retrieve info on genes
			
		// retrieve genes
		Object metadataRawGenes = lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_GENES2METADATA);
		if (metadataRawGenes == null) {
			messages.warnTech("unable to find the expected metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return false;
		}
		Map<String,Object> metadataValues = null;
		try {
			metadataValues = (Map<String,Object>)metadataRawGenes;
		} catch (ClassCastException e) {
			messages.warnTech("wrong metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return false;
		}
			
		// display genes
		geneIdx2columnValue = new String[metadataValues.size()];
		geneIdx2gradient = new Gradient[metadataValues.size()];
		int geneIdx = 0;
		for (String gene: metadataValues.keySet()) {
			final Map<String, Object> metadata = (Map<String, Object>) metadataValues.get(gene);
			final String columnValue = (String)metadata.get(GeneticExplorationAlgoConstants.TABLE_COLUMN_GENE_METADATA_KEY_VALUE);
			geneIdx2columnValue[geneIdx] = columnValue;
			// create color
			final Double min = (Double)metadata.get(GeneticExplorationAlgoConstants.TABLE_COLUMN_GENE_METADATA_KEY_MIN);
			final Double max = (Double)metadata.get(GeneticExplorationAlgoConstants.TABLE_COLUMN_GENE_METADATA_KEY_MAX);
			Gradient gradientForGene = new Gradient(
						min, 
						max, 
						display.getSystemColor(SWT.COLOR_CYAN), 
						display.getSystemColor(SWT.COLOR_DARK_BLUE)
						);
			geneIdx2gradient[geneIdx] = gradientForGene;
			
			geneIdx++;
		}
	
		return true;
	}
	
	/**
	 * 	@return true if success
	 */
	protected boolean loadMetaDataForGoals() {
		Object metadataRawGoals = lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_GOALS2COLS);
		if (metadataRawGoals == null) {
			messages.warnTech("unable to find the expected metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return false;
		}
		Map<String,Map<String,String>> metadata = null;
		try {
			metadata = (Map<String,Map<String,String>>)metadataRawGoals;
		} catch (ClassCastException e) {
			messages.warnTech("wrong metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return false;
		}
		for (String goal: metadata.keySet()) {
					
			Map<String,String> goalMetadata = metadata.get(goal);
			//final String columnValue = goalMetadata.get(GeneticExplorationAlgoConstants.TABLE_COLUMN_GOAL_METADATA_VALUE_VALUE);
			final String columnFitness = goalMetadata.get(GeneticExplorationAlgoConstants.TABLE_COLUMN_GOAL_METADATA_VALUE_FITNESS);
			//final String columnTarget = goalMetadata.get(GeneticExplorationAlgoConstants.TABLE_COLUMN_GOAL_METADATA_VALUE_TARGET);
			goal2columnIdForFitness.put(goal, columnFitness);
		
		}
		
		return true;
	}
	
	private boolean loadMetadataGlobal() {
		
		columnIdForIteration = (String) lastVersionDataToDisplay.getTableMetaData(GeneticExplorationAlgoConstants.TABLE_METADATA_KEY_COLTITLE_ITERATION);

		if (columnIdForIteration == null) {
			messages.warnTech("wrong metadata in this table; maybe it does not comes from a genetic algorithm ?", getClass());
			return false;
		}
		
		return true;
	}
	
	public void setData(GenlabTable tab) {
		lastVersionDataToDisplay = tab;
		this.redraw();
	}
		
	protected void paintData(GC gc) {
	
		// paint background
		gc.setBackground(getBackground());
		gc.fillRectangle(getClientArea());
		
		// quick exit if nothing to display
		if (lastVersionDataToDisplay == null || lastVersionDataToDisplay.isEmpty())
			return;
		
		// load metadata if not done; exit if failure
		if (geneIdx2columnValue == null || geneIdx2columnValue.length==0) {
			if (!loadMetadataGlobal() || !loadMetadataForGenes() || !loadMetaDataForGoals())
				return;
		}
		
		gc.setFont(getFont());

		
		// identify data to display
		// search for the index covering the last iteration
		iterationToDisplay = (Integer)lastVersionDataToDisplay.getValue(lastVersionDataToDisplay.getRowsCount()-1, columnIdForIteration);
		int currentRow = lastVersionDataToDisplay.getRowsCount();
		rowLast = currentRow-1;
		Integer currentRowIteration = null;
		currentRowIteration = (Integer)lastVersionDataToDisplay.getValue(rowLast, columnIdForIteration); 
		do {
			currentRow --;
			currentRowIteration = (Integer)lastVersionDataToDisplay.getValue(currentRow, columnIdForIteration);
		} while (currentRowIteration == iterationToDisplay && currentRow > 0);
		
		rowFirst = currentRow;
		final int totalToDisplay = rowLast-rowFirst;
	
		// compute size
		heightText = (int)Math.ceil(gc.getFontMetrics().getHeight());

		requiredHeight = heightText + heightLine*(totalToDisplay+1) + paddingVertical*totalToDisplay;
		
		
		// resize to this size, thanks
		if (this.getSize().y != requiredHeight)
			setSize(this.getSize().x, requiredHeight);
		
		Rectangle clientArea = getClientArea();
		widthPerGene = clientArea.width/geneIdx2columnValue.length;
		
		// actual titles


		// text iteration
		gc.setForeground(getForeground());
		gc.setClipping(0, 0, clientArea.width, heightText);
		gc.drawText(
				"iteration "+iterationToDisplay, 
				0, 
				0, 
				true
				);
		gc.setClipping((Rectangle)null);
		
		/*
		for (int geneIdx=0; geneIdx<geneIdx2columnValue.length; geneIdx++) {
			
			gc.setClipping(widthPerGene*geneIdx, 0, widthPerGene, heightLine);
			gc.drawText(
					geneIdx2columnValue[geneIdx], 
					widthPerGene*geneIdx, 
					0, 
					true
					);
			
			
		}
		gc.setClipping((Rectangle)null);
		*/
		// paint genes
		int idxLine = 0;
		for (int dataLineIdx=rowFirst; dataLineIdx<rowLast; dataLineIdx++) {
			
			int top = heightText + idxLine*heightLine + idxLine*paddingVertical;
			
			for (int geneIdx=0; geneIdx<geneIdx2columnValue.length; geneIdx++) {
			
				// retrieve data
				double value;
				
				try {
					Object v = lastVersionDataToDisplay.getValue(
							dataLineIdx, 
							geneIdx2columnValue[geneIdx]
						);
					
					// set color
					if (v == null)
						gc.setBackground(getBackground());	
					else { 
						value = ((Number)v).doubleValue();
						gc.setBackground(geneIdx2gradient[geneIdx].getColorForValue(value));
					}
					// fill rectangle
					gc.fillRectangle(
							geneIdx*widthPerGene, 
							top, 
							widthPerGene, 
							heightLine
							);
					
				} catch (Exception e) {
					// unable to read the data ? skip it silently
					e.printStackTrace();
				}
				
				
			}
			
			
					
			idxLine++;
			
		}
		
		
	}
	

	public GenomeWidget(Composite parent, int style) {
		super(parent, style);
		
		display = parent.getDisplay();
		
		tip = new ToolTip(parent.getShell(), SWT.BALLOON);
		tip.setVisible(false);
		
		
		setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					disposeColors();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				try {
					paintData(e.gc);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
				System.out.println("mouse exit !");
				try {
					tip.setVisible(false);
				} catch (RuntimeException e2) {
					e2.printStackTrace();
				}
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				//System.out.println("mouse move ! "+e);

				try {
	            	GenomeWidget actionWidget = (GenomeWidget)e.widget;
	            	if ((actionWidget == null) || (actionWidget.isDisposed()))
	            		return;
	            	
	                //Point loc = actionWidget.toDisplay(e.x, e.y);
	            	Point loc = new Point(e.x, e.y);
	                String[] c = getTooltipForLocation(loc);
	                if (c == null) {
	                	tip.setVisible(false);
	                	return;
	                }
	                tip.setLocation(actionWidget.toDisplay(loc));
	                if (!tip.getText().equals(c[0]) || !tip.getMessage().equals(c[1])) {
	                	System.out.println("change text !");
		                tip.setVisible(false);
	                	tip.setText(c[0]);
	                	tip.setMessage(c[1]);
	                	tip.setVisible(true);
	                }
            	} catch (Exception e2) {
            		e2.printStackTrace();
            	}
			}
		});
		/*
		addFocusListener(new FocusListener() {
			
            @Override
            public void focusLost(FocusEvent e) {
                tip.setVisible(false);
            }

            @Override
            public void focusGained(FocusEvent e) {
            	try {
	            	GenomeWidget actionWidget = (GenomeWidget)e.widget;
	            	if ((actionWidget == null) || (actionWidget.isDisposed()))
	            		return;
	            	
	                Point loc = actionWidget.toDisplay(actionWidget.getLocation());
	                String c = getTooltipForLocation(loc);
	                if (c == null)
	                	return;
	                tip.setLocation(loc.x, loc.y);
	                tip.setText(c);
	                tip.setVisible(true);
            	} catch (RuntimeException e2) {
            		e2.printStackTrace();
            	}
            }
        });*/
	}

	protected String[] getTooltipForLocation(Point loc) {

		if (lastVersionDataToDisplay == null || lastVersionDataToDisplay.isEmpty() || widthPerGene==0)
			return null;
		
		if (loc == null || loc.y < heightText)
			return null;
		
		try {
			int idxLineWidget = (int)Math.floor(
					((double)(loc.y-heightText-paddingVertical))/((double)(heightLine+paddingVertical))
					);
			int idxLineTable = rowFirst + idxLineWidget;
			
			int colLineWidget = loc.x/widthPerGene;
			if (colLineWidget >= geneIdx2columnValue.length)
				return null;
			
			String geneId = geneIdx2columnValue[colLineWidget];
			
			Object v = lastVersionDataToDisplay.getValue(idxLineTable, geneId);;
			if (v == null) {
				return new String[] {
						geneId, "?"	
				};
			} else {
				return new String[] {
						geneId, v.toString()	
				};
			}
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(requiredWidth, requiredHeight);
	}
	
	

}
