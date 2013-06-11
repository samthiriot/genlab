package genlab.core.model.instance;

import genlab.core.usermachineinteraction.MessageAudience;
import genlab.core.usermachineinteraction.MessageLevel;
import genlab.core.usermachineinteraction.TextMessage;;

/**
 * A test message sent during checling before run: each message is associated with algo instance.
 * @author Samuel Thiriot
 *
 */
public class TextMessageFromAlgoInstance extends TextMessage {

	private final IAlgoInstance algoInstance;
	
	public TextMessageFromAlgoInstance(
			IAlgoInstance algoInstance,
			MessageLevel level,
			String message,
			Exception exception
			) {
		super(level, MessageAudience.USER, algoInstance.getClass(), message, exception);
		this.algoInstance = algoInstance;
	}

	public TextMessageFromAlgoInstance(
			IAlgoInstance algoInstance,
			MessageLevel level,
			String message
			) {
		super(level, MessageAudience.USER, algoInstance.getClass(), message);
		this.algoInstance = algoInstance;
	}

	public final IAlgoInstance getAlgoInstance() {
		return algoInstance;
	}
	
}
