package genlab.algog.internal;

import cern.jet.random.Uniform;

public class ADoubleGene extends ANumericGene<Double> {

	/** value of distribution index for mutation */
	public final double eta_m = 20.0;
	/** value of distribution index for crossover */
	public final double eta_c = 20.0;

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

}
