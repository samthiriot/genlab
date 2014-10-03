package genlab.gui.examples.contributors;

public enum GenlabExampleDifficulty {

	BEGINNER("beginner"),
	EASY("easy"),
	MEDIUM("medium"),
	ADVANCED("advanced")
	;
	
	public final String humanReadable;
	
	private GenlabExampleDifficulty(String txt) {
		humanReadable = txt;
	}
	
	@Override
	public final String toString() {
		return humanReadable;
	}
	
}
