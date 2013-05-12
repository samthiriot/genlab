package genlab.core.usermachineinteraction;

public class GLLogger {

	public static void debugUser(String message) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.USER, 
						message)
				);
	}
	
	public static void infoUser(String message) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.INFO, 
						MessageAudience.USER, 
						message)
				);
	}
	
	public static void warningUser(String message) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.USER, 
						message)
				);
	}
	
	public static void debugTech(String message) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.DEBUG, 
						MessageAudience.DEVELOPER, 
						message)
				);
	}
	
	public static void infoTech(String message) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.INFO, 
						MessageAudience.DEVELOPER, 
						message)
				);
	}
	
	public static void warningTech(String message) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.DEVELOPER, 
						message)
				);
	}
	
	public static void warningTech(String message, Exception e) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.WARNING, 
						MessageAudience.DEVELOPER, 
						message,
						e)
				);
	}
	
	public static void errorTech(String message) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.DEVELOPER, 
						message)
				);
	}
	
	public static void errorTech(String message, Exception e) {
		ListsOfMessages.getGenlabMessages().add(
				new TextMessage(
						MessageLevel.ERROR, 
						MessageAudience.DEVELOPER, 
						message,
						e)
				);
	}
}
