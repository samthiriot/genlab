package genlab.algog.gui.jfreechart.views;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;

/**
 * 
 * TODO add legent
 * TODO pan area including the lines ? 
 * 
 * @author Samuel Thiriot
 *
 */
public final class FastScatterPlotWithLines extends FastScatterPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Float> linePositions = new LinkedList<Float>();
	private List<String> lineTitles = new LinkedList<String>();	
	private List<Color> lineColors = new LinkedList<Color>();

	private float yLowest = 0;
	private float yHighest = 0;

	public FastScatterPlotWithLines() {

	}

	
	@Override
	public Range getDataRange(ValueAxis axis) {
		// TODO Auto-generated method stub
		return super.getDataRange(axis);
	}


	public FastScatterPlotWithLines(float[][] data, ValueAxis domainAxis,
			ValueAxis rangeAxis) {
		super(data, domainAxis, rangeAxis);
	}

	public boolean hasHorizontalLine() {
		return !linePositions.isEmpty();
		
	}
	
	public void addHorizontalLine(float position, String title, Color color) {
	
		linePositions.add(position);
		lineTitles.add(title);
		lineColors.add(color);
		
		//LegendItem i = new LegendItem(title);
		//i.set
		// display the corresponding line in the legend ! 
		//this.getLegendItems().add(i);
	}

	@Override
	public void render(Graphics2D g, Rectangle2D dataArea,
			PlotRenderingInfo arg2, CrosshairState arg3) {

		// render all
		//super.render(g, dataArea, arg2, arg3);
		
		g.setPaint(this.getPaint());
		
		final ValueAxis domainAxis = getDomainAxis();
		final ValueAxis rangeAxis = getRangeAxis();

		final float[][] data = this.getData();
		if (data != null) {
			
			for (int i=0; i<data[0].length; i++) {
				final float x = data[0][i];
				final float y = data[1][i];
				final int transx = (int)domainAxis.valueToJava2D(x, dataArea, RectangleEdge.BOTTOM);
				final int transy = (int)rangeAxis.valueToJava2D(y, dataArea, RectangleEdge.LEFT);
				
				g.fillRect(
						transx -1, 
						transy -1, 
						3, 
						3
						);
				
			}
		}
		
		final int xMin = (int) domainAxis.valueToJava2D(domainAxis.getRange().getLowerBound(), dataArea, RectangleEdge.BOTTOM);
		final int xMax = (int) domainAxis.valueToJava2D(domainAxis.getRange().getUpperBound(), dataArea, RectangleEdge.BOTTOM);
		
		// add lines
		for (int i=0; i<linePositions.size(); i++) {
			
			final int lineY = (int) rangeAxis.valueToJava2D(linePositions.get(i), dataArea, RectangleEdge.LEFT);
			g.setColor(lineColors.get(i));
			//System.err.println("printing line "+lineTitles.get(i)+" at "+lineY);
			g.drawLine(
					xMin, 
					lineY, 
					xMax, 
					lineY
					);
		}
		
	}
	
	public void setRange(float yLowest, float yHighest) {
		
		this.yLowest = yLowest;
		this.yHighest = yHighest;
		
		float margin = 0;
		if (!Float.isInfinite(yHighest) && !Float.isInfinite(yLowest) && (yHighest-yLowest > 0)) {
			margin = (yHighest-yLowest)/60;				
		}	
		getRangeAxis().setRange(yLowest-margin, yHighest+margin);
		
	}
	
	public float getRangeLow() {
		return this.yLowest;
	}
	
	public float getRangeUp() {
		return this.yHighest;
	}
	
}
