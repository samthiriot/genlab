package genlab.core.model.exec;

import genlab.core.commons.ProgramException;
import genlab.core.model.instance.IConnection;
import genlab.core.model.meta.IConstantAlgo;

/**
 * TODO Take all of these contraints; use them during workflow checking; 
 * also forbid the GUI to create links not compliant.
 * 
 * @author Samuel Thiriot
 *
 */
public class ExecutableConnectionsFactory {

	
	public static IConnectionExecution createExecutableConnection(
			IAlgoExecution fromExec, 
			IAlgoExecution toExec, 
			IConnection c
			) {
		

		// the links comes from an iteration container
		// so as soon as the container is STARTED, 
		// the connection has to send data to its children
		if (toExec.containedInto(fromExec)) {
		//if (fromExec instanceof AbstractContainerExecutionIteration) {
			
			if (!(toExec instanceof IAlgoExecutionOneshot))
				throw new ProgramException("algorithms contained into iteration containers should only be one shot ones");
			
			return new ConnectionExecFromIterationToChild(
					c, 
					fromExec, 
					(IAlgoExecutionOneshot)toExec,
					false	// don't check when we do fancy things
					);
			
		}
		
		if (fromExec instanceof AbstractContainerExecutionSupervisor) {
			

			if (!(toExec instanceof IReduceAlgoExecution))
				throw new ProgramException("algorithms placed after parallel blocks should always be reduce algorithms");
			
			return new ConnectionExecFromSupervisorToChild(
					c, 
					(AbstractContainerExecutionSupervisor)fromExec, 
					(IReduceAlgoExecution)toExec
					);
						
		}
		
		// one shot connection, the easiest one :-) 
		if ( toExec instanceof IAlgoExecutionOneshot ) {
			
			return new ConnectionExec(
					c, 
					fromExec, 
					(IAlgoExecutionOneshot)toExec,
					false	// don't check when we do fancy things
					);
		
		} 
		
		
		if ( fromExec.getAlgoInstance().getAlgo() instanceof IConstantAlgo) {
			
			// constants are special: they will always provide the same result, 
			// no matter what parent, children, container, loop is involved.
			
			return new ConnectionExec(
					c, 
					fromExec, 
					(IAlgoExecutionOneshot)toExec,
					false	// don't check when we do fancy things
					);
		
		} 
		

		// unknown case
		throw new ProgramException("unable to create an executable connection from "+fromExec+" to "+toExec);
	
	}
	
}
