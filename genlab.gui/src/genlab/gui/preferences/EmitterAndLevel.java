package genlab.gui.preferences;

import genlab.core.usermachineinteraction.MessageLevel;

class EmitterAndLevel {
	public String emitter;
	public MessageLevel level;
	
	public EmitterAndLevel(String emitter, MessageLevel level) {
		this.emitter = emitter;
		this.level = level;
	}
	
	public static EmitterAndLevel fromString(String e) {
		String[] ee = e.split("=");
		return new EmitterAndLevel(ee[0], MessageLevel.valueOf(ee[1]));
	}
	
	public String toSaveString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.emitter);
		sb.append("=");
		sb.append(level.toString());
		return sb.toString();
	}
}