package genlab.gui.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeConfiguration;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class ResizeIAlgoInstanceFeature extends DefaultResizeShapeFeature {

	public ResizeIAlgoInstanceFeature(IFeatureProvider fp) {
		super(fp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void resizeShape(IResizeShapeContext context) {
		// TODO Auto-generated method stub
		super.resizeShape(context);
		
		
	}

	@Override
	protected void resizeShape(Shape currentShape) {
		// TODO Auto-generated method stub
		super.resizeShape(currentShape);
		

		
	}

	@Override
	public IResizeConfiguration getResizeConfiguration(
			IResizeShapeContext context) {
		
		return new IResizeConfiguration() {
			
			@Override
			public boolean isVerticalResizeAllowed() {
				return true;
			}
			
			@Override
			public boolean isHorizontalResizeAllowed() {
				return true;
			}
		};
	}
	
	

}
