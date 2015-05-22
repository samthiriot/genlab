package genlab.algog.internal;

import cern.jet.random.Uniform;

public class ADoubleGene extends ANumericGene<Double> {

	public ADoubleGene(String name,double mutationProba, Double min, Double max) {
		super(name, mutationProba, min, max);
	}

	@Override
	public Double generateRandomnly(Uniform uniform) {
		return uniform.nextDoubleFromTo(min, max);
	}

	@Override
	public Double crossoverArithmetic(Double one, Double other, double weight) {
		
		return one.doubleValue()*weight+other.doubleValue()*(1-weight);
	}

	@Override
	public Double crossoverArithmetic(Object one, Object other, double weight) {
		return crossoverArithmetic((Double)one, (Double)other, weight);
	}

	@Override
	public Double mutate(Uniform uniform, Object previousValue) {
		double y = (Double)previousValue;
		double yl = min;
		double yu = max;
		double delta1 = (y-yl)/(yu-yl);
		double delta2 = (yu-y)/(yu-yl);
		double rnd = uniform.nextDoubleFromTo(0.0, 1.0);
		double mut_pow = 1/(eta_m + 1);
		double deltaq;
        if( rnd <= 0.5 ) {
            double xy = 1 - delta1;
            double val = 2*rnd + (1 - 2*rnd)*(Math.pow(xy, (eta_m + 1)));
            deltaq =  Math.pow(val, mut_pow) - 1;
        }else {
            double xy = 1 - delta2;
            double val = 2*(1 - rnd) + 2*(rnd - 0.5)*(Math.pow(xy, (eta_m + 1)));
            deltaq = 1 - (Math.pow(val, mut_pow));
        }
        
        y = y + deltaq*(yu-yl);
        
        if( y < yl )
            y = yl;
        if( y > yu )
            y = yu;

		return y;
	}

	@Override
	public Double[] crossoverSBX(Uniform U, Object genesA, Object genesB) {
		
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
                
                yl = min;
                yu = max;
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
                	return new Double[]{c2, c1};
                }else {
                	return new Double[]{c1, c2};
                }
            }else {
                if( U.nextDoubleFromTo(0, 1)<=0.5 ) {
                	return new Double[]{gA, gB};
                }else {
                	return new Double[]{gB, gA};
                }
            }
		}else {
        	return new Double[]{gA, gB};
		}
	}
}
