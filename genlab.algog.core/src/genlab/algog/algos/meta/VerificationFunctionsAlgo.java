package genlab.algog.algos.meta;

import genlab.core.commons.ProgramException;
import genlab.core.exec.IExecution;
import genlab.core.model.exec.AbstractAlgoExecutionOneshot;
import genlab.core.model.exec.ComputationProgressWithSteps;
import genlab.core.model.exec.ComputationResult;
import genlab.core.model.exec.ComputationState;
import genlab.core.model.exec.IAlgoExecution;
import genlab.core.model.instance.AlgoInstance;
import genlab.core.model.meta.basics.flowtypes.DoubleInOut;
import genlab.core.parameters.ListParameter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VerificationFunctionsAlgo extends AbstractGeneticAlgo {
	
	// inputs: x,y
	public static final DoubleInOut INPUT_X = new DoubleInOut(
			"in_x", 
			"x",
			"parameter x",
			1.0
			);
	
	public static final DoubleInOut INPUT_Y = new DoubleInOut(
			"in_y", 
			"y",
			"parameter y",
			2.0
			);
	
	// outputs: f1, f2
	public static final DoubleInOut OUTPUT_F1 = new DoubleInOut(
			"out_f1", 
			"f1",
			"result of function F1"
			);
	
	public static final DoubleInOut OUTPUT_F2 = new DoubleInOut(
			"out_f2", 
			"f2",
			"result of function F2"
			);
	
	public static enum EAvailableFunctions {
		
		BNH ("BNH (Binh and Korn)"),
		CTP1 ("CTP1 (Constrained Test Problem n°1)"),
		CTP2 ("CTP2 (Constrained Test Problem n°2)"),
		CTP3 ("CTP3 (Constrained Test Problem n°3)"),
		CTP4 ("CTP4 (Constrained Test Problem n°4)"),
		CTP5 ("CTP5 (Constrained Test Problem n°5)"),
		CTP6 ("CTP6 (Constrained Test Problem n°6)"),
		CTP7 ("CTP7 (Constrained Test Problem n°7)"),
		CTP8 ("CTP8 (Constrained Test Problem n°8)"),
		POL ("POL (Poloni)"),
		TNK ("TNK (Tanaka)"),
		CONSTR_EX ("Constr-Ex problem"),
		SRN ("SRN (Chakong and Haimes)"),
		NEO ("test")
		;
		
		public final String label;
		
		private static Map<String, EAvailableFunctions> label2value = new HashMap<String, VerificationFunctionsAlgo.EAvailableFunctions>();

		private EAvailableFunctions (String label) {
			this.label = label;
		}
	
		public static EAvailableFunctions parseFromLabel(String label) {
			return label2value.get(label);
		}
		
		public static List<String> getLabelsAsList() {
			
			List<String> res = new LinkedList<String>();
			for (EAvailableFunctions e: values()) {
				res.add(e.label);
			}
			return res;
		}
		
		static {
			
			// cache the map from label to enum value
			for (EAvailableFunctions value : EAvailableFunctions.values()) {
				
				// refuse double values
				if (label2value.containsKey(value.label)) {
					throw new ProgramException("label "+value.label+" was defined several times in "+EAvailableFunctions.class.getCanonicalName());
				}
			
				label2value.put(value.label, value);
			}
			
		}
		
	}
	
	// parameter: enum
	public static final ListParameter PARAM_FUNCTION = new ListParameter(
			"param_function", 
			"function", 
			"reference function to compute",
			0,
			EAvailableFunctions.getLabelsAsList()
			);
	
	
	public VerificationFunctionsAlgo() {
		super(
				"multi-objective test functions", 
				"functions for validating multi-objective functions"
				);

		inputs.add(INPUT_X);
		inputs.add(INPUT_Y);
		
		outputs.add(OUTPUT_F1);
		outputs.add(OUTPUT_F2);
		
		registerParameter(PARAM_FUNCTION);
	}


	@Override
	public IAlgoExecution createExec(IExecution execution,
			AlgoInstance algoInstance) {
		return new AbstractAlgoExecutionOneshot(execution, algoInstance, new ComputationProgressWithSteps()) {
			
			@Override
			public void kill() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
			}
			
			@Override
			public void cancel() {
				progress.setComputationState(ComputationState.FINISHED_CANCEL);
			}
			
			@Override
			public void run() {

				ComputationResult res = new ComputationResult(algoInst, progress, messages);
				setResult(res);

				// retrieve parameters and inputs
				final Integer idxParam = (Integer)algoInst.getValueForParameter(PARAM_FUNCTION);
				final EAvailableFunctions testedFunction = EAvailableFunctions.values()[idxParam];
				final double x = (Double) getInputValueForInput(INPUT_X);
				final double y = (Double) getInputValueForInput(INPUT_Y);

				// reserved for CTP2-CTP8
				double exp1, exp2;
				// reserved for CTP8
				double exp3, exp4;
				
				try {
					// detect constraint violation 
					boolean violatesConstraint = false;
					boolean violatesSearchDomain = false;
					switch (testedFunction) {
						case NEO:
							violatesConstraint = false;
						violatesSearchDomain =
								(x < 0) || (x > 1) ||
								(y < 0) || (y > 1);
							break;
						case BNH:
							violatesConstraint = 
								(
									(StrictMath.pow(x - 5, 2) + StrictMath.pow(y, 2)) > 25
								) || (
									(StrictMath.pow(x-8, 2) + StrictMath.pow(y+3, 2)) < 7.7		
								);
							violatesSearchDomain =
									(x < 0) || (x > 5) ||
									(y < 0) || (y > 3);
							break;
						case SRN:
							violatesConstraint =
								(
									(StrictMath.pow(x, 2) + StrictMath.pow(y, 2) - 225) > 0
								) || (
									(x - 3*y + 10) > 0
								);
							violatesSearchDomain =
									(x < -20) || (x > 20) ||
									(y < -20) || (y > 20);
							break;
						case CTP1:
							violatesConstraint =
								(
									(((1+y) * StrictMath.exp(-x/(1+y))) / ( 0.858*StrictMath.exp(-0.541*x) )) < 1
								) || (
									(((1+y) * StrictMath.exp(-x/(1+y))) / ( 0.728*StrictMath.exp(-0.295*x) )) < 1
								);
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case CTP2:
							// θ = -0.2π
							// a = 0.2
							// b = 10
							// c = 1
							// d = 6
							// e = 1
							{
							    double ctp_g = 1 + y;
							    double ctp_theta = -0.2 * StrictMath.PI;
							    double ctp_a = 0.2;
							    double ctp_b = 10;
							    double ctp_c = 1;
							    double ctp_d = 6;
							    double ctp_e = 1;
							    
							    double ctp_f1 = x;
							    double ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp1 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp2 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							}
//						    exp1 = (ctp_g*(1 - StrictMath.sqrt(x/ctp_g))-ctp_e)*StrictMath.cos(ctp_theta) - x*StrictMath.sin(ctp_theta);
//						    exp2 = (ctp_g*(1 - StrictMath.sqrt(x/ctp_g))-ctp_e)*StrictMath.sin(ctp_theta) + x*StrictMath.cos(ctp_theta);
//						    exp2 = ctp_b*StrictMath.PI*StrictMath.pow(exp2,ctp_c);
//						    exp2 = StrictMath.abs(StrictMath.sin(exp2));
//						    exp2 = ctp_a*StrictMath.pow(exp2,ctp_d);
//						    violatesConstraint = ( (exp1/exp2) < 1 );
						    violatesConstraint = ( exp1<exp2 );
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case CTP3:
							// θ = -0.2π
							// a = 0.1
							// b = 10
							// c = 1
							// d = 0.5
							// e = 1
							{
							    double ctp_g = 1 + y;
							    double ctp_theta = -0.2 * StrictMath.PI;
							    double ctp_a = 0.1;
							    double ctp_b = 10;
							    double ctp_c = 1;
							    double ctp_d = 0.5;
							    double ctp_e = 1;
							    
							    double ctp_f1 = x;
							    double ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp1 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp2 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							}
						    violatesConstraint = ( exp1<exp2 );
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case CTP4:
							// θ = -0.2π
							// a = 0.75
							// b = 10
							// c = 1
							// d = 0.5
							// e = 1
							{
							    double ctp_g = 1 + y;
							    double ctp_theta = -0.2 * StrictMath.PI;
							    double ctp_a = 0.75;
							    double ctp_b = 10;
							    double ctp_c = 1;
							    double ctp_d = 0.5;
							    double ctp_e = 1;
							    
							    double ctp_f1 = x;
							    double ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp1 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp2 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							}
						    violatesConstraint = ( exp1<exp2 );
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case CTP5:
							// θ = -0.2π
							// a = 0.1
							// b = 10
							// c = 2
							// d = 0.5
							// e = 1
							{
							    double ctp_g = 1 + y;
							    double ctp_theta = -0.2 * StrictMath.PI;
							    double ctp_a = 0.1;
							    double ctp_b = 10;
							    double ctp_c = 2;
							    double ctp_d = 0.5;
							    double ctp_e = 1;
							    
							    double ctp_f1 = x;
							    double ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp1 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp2 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							}
						    violatesConstraint = ( exp1<exp2 );
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case CTP6:
							// θ = 0.1π
							// a = 40
							// b = 0.5
							// c = 1
							// d = 2
							// e = -2
							{
							    double ctp_g = 1 + y;
							    double ctp_theta = 0.1 * StrictMath.PI;
							    double ctp_a = 40;
							    double ctp_b = 0.5;
							    double ctp_c = 1;
							    double ctp_d = 2;
							    double ctp_e = -2;
							    
							    double ctp_f1 = x;
							    double ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp1 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp2 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							}
						    violatesConstraint = ( exp1<exp2 );
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case CTP7:
							// θ = -0.05π
							// a = 40
							// b = 5
							// c = 1
							// d = 6
							// e = 0
							{
							    double ctp_g = 1 + y;
							    double ctp_theta = -0.05 * StrictMath.PI;
							    double ctp_a = 40;
							    double ctp_b = 5;
							    double ctp_c = 1;
							    double ctp_d = 6;
							    double ctp_e = 0;
							    
							    double ctp_f1 = x;
							    double ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp1 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp2 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							}
						    violatesConstraint = ( exp1<exp2 );
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case CTP8:
							// θ = 0.1π		and		-0.05π
							// a = 40		and 	40
							// b = 0.5		and 	2
							// c = 1 		and 	1
							// d = 2 		and 	6
							// e = -2 		and 	0
							{
								/*
								 * First constraint
								 */
							    double ctp_g = 1 + y;
							    double ctp_theta = 0.1 * StrictMath.PI;
							    double ctp_a = 40;
							    double ctp_b = 0.5;
							    double ctp_c = 1;
							    double ctp_d = 2;
							    double ctp_e = -2;
							    
							    double ctp_f1 = x;
							    double ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp1 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp2 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							    
							    /*
							     * Second constraint
							     */
							    ctp_g = 1 + y;
							    ctp_theta = -0.05 * StrictMath.PI;
							    ctp_a = 40;
							    ctp_b = 2;
							    ctp_c = 1;
							    ctp_d = 6;
							    ctp_e = 0;
							    
							    ctp_f1 = x;
							    ctp_f2 = ctp_g*( 1 - ctp_f1/ctp_g );
							    
							    exp3 = StrictMath.cos( ctp_theta ) * ( ctp_f2-ctp_e )   -   StrictMath.sin( ctp_theta ) * ctp_f1;
							    exp4 = 
							    	ctp_a * StrictMath.pow(
							    		StrictMath.abs(
							    			StrictMath.sin(
							    				ctp_b * StrictMath.PI * StrictMath.pow( 
							    					StrictMath.sin( ctp_theta ) * ( ctp_f2-ctp_e )   +   StrictMath.cos( ctp_theta ) * ctp_f1
							    				, ctp_c )
							    			)
							    		)
							    	, ctp_d );
							}
						    violatesConstraint = ( (exp1<exp2) || (exp3<exp4) );
							violatesSearchDomain =
									(x < 0) || (x > 1) ||
									(y < 0) || (y > 1);
							break;
						case POL:
							violatesConstraint = false; // no constraint
							violatesSearchDomain =
									(x < -StrictMath.PI) || (x > StrictMath.PI) ||
									(y < -StrictMath.PI) || (y > StrictMath.PI);
							break;
						case TNK:
							if( y==0 ) {
								violatesConstraint = true;
							}else {
								violatesConstraint = 
								(
									(StrictMath.pow(x, 2) + StrictMath.pow(y, 2) - 0.1*StrictMath.cos( 16*StrictMath.atan(x/y) ) - 1) < 0
								) || (
									(2*StrictMath.pow(x-0.5, 2) + 2*StrictMath.pow(y-0.5, 2)) > 1
								);
							}
							violatesSearchDomain =
									(x < 0) || (x > StrictMath.PI) ||
									(y < 0) || (y > StrictMath.PI);
							break;
						case CONSTR_EX:
							violatesConstraint = 
								(
									(y + 9*x) < 6
								) || (
									(-y + 9*x) < 1
								);
							violatesSearchDomain =
									(x < 0.1) || (x > 1) ||
									(y < 0) || (y > 5);
							break;
						default:
							throw new ProgramException("unknown test function "+testedFunction);
					}
										
					// if constraint is violated, then fail
					if (violatesConstraint) {
						messages.infoUser("constraint violated for function "+testedFunction, getClass());
						progress.setComputationState(ComputationState.FINISHED_FAILURE);
						return;
					}
					if (violatesSearchDomain) {
						messages.errorUser("out of search domain for function "+testedFunction+" x="+x+", y="+y+"; please correct the search domain", getClass());
						progress.setComputationState(ComputationState.FINISHED_FAILURE);
						return;
					}

					// we compute the goals
					Double f1 = null;
					Double f2 = null;
					switch (testedFunction) {
						case NEO:
							double lambda = 0.0001d;
							f1 = (1/lambda) * (x-y);
							f2 = lambda * (y-x);
							break;
						case BNH:
							f1 = 4*StrictMath.pow(x, 2)+4*StrictMath.pow(y, 2);
							f2 = StrictMath.pow(x - 5,  2) + StrictMath.pow(y - 5, 2);
							break;
						case SRN:
							f1 = StrictMath.pow(x-2, 2) + StrictMath.pow(y-1, 2) + 2;
							f2 = 9*x - StrictMath.pow(y-1, 2);
							break;
						case CTP1:
							f1 = x;
							f2 = (1+y) * StrictMath.exp(-x/(1+y));
							break;
						case CTP2:
						case CTP3:
						case CTP4:
						case CTP5:
						case CTP6:
						case CTP7:
						case CTP8:
						    double ctp_g = 1 + y;
						    f1 = x;
						    f2 = ctp_g*(1 - f1/ctp_g);
							break;
						case POL:
						    double a1, a2, b1, b2;
						    a1 = 0.5*StrictMath.sin(1) - 2.0*StrictMath.cos(1) + StrictMath.sin(2) - 1.5*StrictMath.cos(2);
						    a2 = 1.5*StrictMath.sin(1) - StrictMath.cos(1) + 2*StrictMath.sin(2) - 0.5*StrictMath.cos(2);
						    b1 = 0.5*StrictMath.sin(x) - 2*StrictMath.cos(x) + StrictMath.sin(y) - 1.5*StrictMath.cos(y);
						    b2 = 1.5*StrictMath.sin(x) - StrictMath.cos(x) + 2*StrictMath.sin(y) - 0.5*StrictMath.cos(y);
						    
							f1 = 1 + StrictMath.pow(a1-b1, 2) + StrictMath.pow(a2-b2, 2);
							f2 = StrictMath.pow(x+3, 2) + StrictMath.pow(y+1, 2);
							break;
						case TNK:
							f1 = x;
							f2 = y;
							break;
						case CONSTR_EX:
							f1 = x;
							f2 = (1+y) / x;
							break;
						default:
							throw new ProgramException("unknown test function "+testedFunction);
					}
					
					res.setResult(OUTPUT_F1, f1);
					res.setResult(OUTPUT_F2, f2);
					
					progress.setComputationState(ComputationState.FINISHED_OK);
					
				} catch (RuntimeException e) {
					messages.errorUser("unexpected error while computing "+testedFunction.label+": "+e.getMessage(), getClass());
					progress.setComputationState(ComputationState.FINISHED_FAILURE);
				}
			}
			
			@Override
			public long getTimeout() {
				return 200;
			}
		};
	}

}
