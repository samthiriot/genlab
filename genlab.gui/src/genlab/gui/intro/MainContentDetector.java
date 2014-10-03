package genlab.gui.intro;

import org.eclipse.ui.intro.IntroContentDetector;

/**
 * Makes the intro to be displayed again, even if the user closed it. 
 * Not used yet. SHould be used later after an update, or 
 * after the install of novel examples, etc.
 * TODO detect when to redisplay intro
 * 
 * @author Samuel Thiriot
 *
 */
public class MainContentDetector extends IntroContentDetector {

	public MainContentDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isNewContentAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

}
