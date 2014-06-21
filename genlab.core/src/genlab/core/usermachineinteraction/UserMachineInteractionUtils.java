package genlab.core.usermachineinteraction;

public class UserMachineInteractionUtils {


	public static String getHumanReadableTimeRepresentation(Long durationMs) {
	
		if (durationMs == null)
			return "?";
		
		StringBuffer sb = new StringBuffer();
		
		if (durationMs < 1000)
			sb.append(durationMs).append(" ms");
		else {
			final long durationS = durationMs/1000;
			if (durationS < 60) {
				sb.append(durationS).append(" s");
			} else {
				final int durationM = (int)Math.round(durationS / 60.0);
				sb.append(durationM).append(" m");
			}
		}
		return sb.toString();
		
	}
	
	private UserMachineInteractionUtils() {
	}

}
