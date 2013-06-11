/**
 * General principles:
 * <ul>
 * <li>an IAlgo defines a possible process in a workflow (like: open a file).</li>
 * <li>an IAlgoInstance defines an instance of an algo into a workflow. Like: 'algo "open a file" number 10'</li>.
 * <li>you can ask an algo instance to execute according to given input and output parameters. 
 * In this case, it creates an returns an IAlgoExecution (but nothing else happened, no computation)</li>
 * <li>then you can ask the AlgoExecution to execute</li> 
 * </ul>
 * 
 */

package genlab.core.model.meta;
