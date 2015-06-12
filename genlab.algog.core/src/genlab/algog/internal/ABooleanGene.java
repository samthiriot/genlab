package genlab.algog.internal;

import cern.jet.random.Uniform;

public class ABooleanGene extends AGene<Boolean> {

	public ABooleanGene(String name, double mutationProba, Double etam, Double etac) {
		super(name, mutationProba, etam, etac);
	}

	@Override
	public Boolean generateRandomnly(Uniform uniform) {
		return uniform.nextBoolean();
	}

	@Override
	public Boolean mutate(Uniform uniform, Object previousValue) {
		// invert the value
		return !((Boolean)previousValue);
	}

	@Override
	public Boolean[] crossoverSBX(Uniform U, Object genesA, Object genesB) {
		
		Double gA = (Double)genesA;
		Double gB = (Double)genesB;
		
		if( U.nextDoubleFromTo(0, 1)<0.5 ) {
			double rand;
            double y1, y2, yl, yu;
            double c1, c2;
            double alpha, beta, betaq;

            if( StrictMath.abs(gA-gB)>EPS ) {
                if( gA<gB ) {
                    y1 = gA;
                    y2 = gB;
                }else {
                    y1 = gB;
                    y2 = gA;
                }
                
                yl = 0;
                yu = 1;
                rand = U.nextDoubleFromTo(0, 1);
                beta = 1.0 + (2.0*(y1-yl)/(y2-y1));
                alpha = 2.0 - StrictMath.pow( beta , -(eta_c+1.0) );
                
                if( rand<=(1.0/alpha) ) {
                    betaq = StrictMath.pow( (rand*alpha) , (1.0/(eta_c+1.0)) );
                }else {
                    betaq = StrictMath.pow( (1.0/(2.0-rand*alpha)) , (1.0/(eta_c+1.0)) );
                }
                
                c1 = 0.5*((y1+y2)-betaq*(y2-y1));
                beta = 1.0 + (2.0*(yu-y2)/(y2-y1));
                alpha = 2.0 - StrictMath.pow( beta , -(eta_c+1.0) );
                
                if( rand<=(1.0/alpha) ) {
                    betaq = StrictMath.pow( (rand*alpha) , (1.0/(eta_c+1.0)) );
                }else {
                    betaq = StrictMath.pow( (1.0/(2.0-rand*alpha)) , (1.0/(eta_c+1.0)) );
                }
                
                c2 = 0.5*((y1+y2)+betaq*(y2-y1));
                
                if( c1<yl ) c1 = yl;
                if( c2<yl ) c2 = yl;
                if( c1>yu ) c1 = yu;
                if( c2>yu ) c2 = yu;
                
                if( U.nextDoubleFromTo(0, 1)<=0.5 ) {
                	return new Boolean[]{Math.round(c2)==1, Math.round(c1)==1};
                }else {
                	return new Boolean[]{Math.round(c1)==1, Math.round(c2)==1};
                }
            }else {
                if( U.nextDoubleFromTo(0, 1)<=0.5 ) {
                	return new Boolean[]{Math.round(gA)==1, Math.round(gB)==1};
                }else {
                	return new Boolean[]{Math.round(gB)==1, Math.round(gA)==1};
                }
            }
		}else {
        	return new Boolean[]{Math.round(gA)==1, Math.round(gB)==1};
		}
	}
}
