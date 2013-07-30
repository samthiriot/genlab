package genlab.core.usermachineinteraction;


/**
 * Facilitates the use of the logging tools for the GenLab platform.
 * 
 * @author Samuel Thiriot
 *
 */
@SuppressWarnings("rawtypes")
public class GLLogger {

	public static void debugUser(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().debugUser(message, fromShort, fromClass);
	}

	public static void debugUser(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().debugUser(message, fromClass);
	}

	public static void warnUser(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().warnUser(message, fromShort, fromClass);
	}

	public static void warnUser(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().warnUser(message, fromClass);
	}

	public static void infoUser(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().infoUser(message, fromShort, fromClass);
	}

	public static void infoUser(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().infoUser(message, fromClass);
	}

	public static void tipUser(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().tipUser(message, fromShort, fromClass);
	}

	public static void tipUser(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().tipUser(message, fromClass);
	}
	
	public static void errorUser(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().errorUser(message, fromShort, fromClass);
	}

	public static void errorUser(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().errorUser(message, fromClass);
	}
	

	public static void traceTech(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().traceTech(message, fromShort, fromClass);
	}

	public static void traceTech(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().traceTech(message, fromClass);
	}
	

	public static void traceTech(String message, String fromShort, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().traceTech(message, fromShort, fromClass, e);
	}

	public static void traceTech(String message, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().traceTech(message, fromClass, e);
	}

	public static void debugTech(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().debugTech(message, fromShort, fromClass);
	}

	public static void debugTech(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().debugTech(message, fromClass);
	}
	

	public static void debugTech(String message, String fromShort, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().debugTech(message, fromShort, fromClass, e);
	}

	public static void debugTech(String message, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().debugTech(message, fromClass, e);
	}


	public static void warnTech(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().warnTech(message, fromShort, fromClass);
	}
	
	public static void warnTech(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().warnTech(message, fromClass);
	}

	public static void warnTech(String message, String fromShort, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().warnTech(message, fromShort, fromClass, e);
	}

	public static void warnTech(String message, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().warnTech(message, fromClass, e);
	}

	public static void infoTech(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().infoTech(message, fromShort, fromClass);
	}

	public static void infoTech(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().infoTech(message, fromClass);
	}

	public static void tipTech(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().tipTech(message, fromShort, fromClass);
	}

	public static void tipTech(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().tipTech(message, fromClass);
	}
	
	public static void errorTech(String message, String fromShort, Class fromClass) {
		ListsOfMessages.getGenlabMessages().errorTech(message, fromShort, fromClass);
	}

	public static void errorTech(String message, Class fromClass) {
		ListsOfMessages.getGenlabMessages().errorTech(message, fromClass);
	}
	
	public static void errorTech(String message, String fromShort, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().errorTech(message, fromShort, fromClass, e);
	}

	public static void errorTech(String message, Class fromClass, Throwable e) {
		ListsOfMessages.getGenlabMessages().errorTech(message, fromClass, e);
	}
	
	

	
	
}
