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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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
	
	protected int heightLine = 15;
	protected int paddingVertical = 2;
	
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
			colors.put(total, new Color(display, red, green, blue));
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
		public final double steps = 20;
		
		public Gradient(double min, double max, Color colorStart, Color colorEnd) {
			super();
			this.min = min;
			this.max = max;
			this.colorStart = colorStart;
			this.colorEnd = colorEnd;
		}
		
		public Color getColorForValue(double v) {
			
            double ratio = v / steps;

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
			geneIdx2columnValue[geneIdx++] = columnValue;
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
	
		// quick exit if nothing to display
		if (lastVersionDataToDisplay == null || lastVersionDataToDisplay.isEmpty())
			return;
		
		// load metadata if not done; exit if failure
		if (geneIdx2columnValue == null || geneIdx2columnValue.length==0) {
			if (!loadMetadataGlobal() || !loadMetadataForGenes() || !loadMetaDataForGoals())
				return;
		}
		
		// identify data to display
		// search for the index covering the last iteration
		iterationToDisplay = (Integer)lastVersionDataToDisplay.getValue(lastVersionDataToDisplay.getRowsCount()-1, columnIdForIteration);
		int currentRow = lastVersionDataToDisplay.getRowsCount()-1;
		final int rowLast = currentRow;
		Integer currentRowIteration = null;
		currentRowIteration = (Integer)lastVersionDataToDisplay.getValue(rowLast, columnIdForIteration); 
		do {
			currentRow --;
			currentRowIteration = (Integer)lastVersionDataToDisplay.getValue(currentRow, columnIdForIteration);
		} while (currentRowIteration == iterationToDisplay);
		
		final int rowFirst = currentRow;
		final int totalToDisplay = rowLast-rowFirst;
	
		// compute size
		requiredHeight = heightLine*totalToDisplay+paddingVertical*(totalToDisplay-1);
		
		// resize to this size, thanks
		if (this.getSize().y != requiredHeight)
			setSize(this.getSize().x, requiredHeight);
		
		// actual paint
		
		// paint genes
		Rectangle clientArea = getClientArea();
		final int widthPerGene = clientArea.width/geneIdx2columnValue.length;
		int idxLine = 0;
		for (int dataLineIdx=rowFirst; dataLineIdx<=rowLast; dataLineIdx++) {
			
			int top = idxLine*requiredHeight+(idxLine-1)*paddingVertical;
			
			for (int geneIdx=0; geneIdx<geneIdx2columnValue.length; geneIdx++) {
			
				// retrieve data
				double value;
				
				try {
					value = (Double)lastVersionDataToDisplay.getValue(
								dataLineIdx, 
								geneIdx2columnValue[geneIdx]
							);
						
					// set color
					gc.setBackground(geneIdx2gradient[geneIdx].getColorForValue(value));
					// fill rectangle
					gc.fillRectangle(
							geneIdx, 
							top, 
							widthPerGene, 
							requiredHeight
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
		
		setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				disposeColors();
			}
		});
		addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				paintData(e.gc);
			}
		});
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(requiredWidth, requiredHeight);
	}
	
	

}
