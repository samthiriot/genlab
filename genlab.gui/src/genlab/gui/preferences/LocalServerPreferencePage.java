package genlab.gui.preferences;

import genlab.gui.Activator;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * THe page of preferences for the local runner.
 * 
 * @author Samuel Thiriot
 *
 */
public class LocalServerPreferencePage 
				extends FieldEditorPreferencePage 
				implements IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.genlab.preferences.pages.localserver";
	
	public static final String KEY_START_SERVER = PAGE_ID+".start_server";
	public static final String KEY_START_SERVER_PORT = PAGE_ID+".start_server_port";
	public static final String KEY_START_SERVER_INTERFACE = PAGE_ID+".start_server_interface";


	public LocalServerPreferencePage() {
	}


	@Override
	public void init(IWorkbench workbench) {
		
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Settings for the server for publishing this computer on the local network and share it with another local computer.");

	}
	
	/**
	 * Lists the names of network interfaces of interest on the machine.
	 * Used to build the set of choices in the preference page.
	 * @return
	 */
	public static List<String> listInterfacesNamesOnMachine() {
		
		List<String> res = new LinkedList<String>();
	
		try {
			for (NetworkInterface n: Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (n.isVirtual()) {
					continue;
				}
				if (!n.isUp()) {
					continue;
				}
				
				// keep this interface only if it contains at least on IP of interest. 
				loopIps : for (InetAddress ip: Collections.list(n.getInetAddresses())) {

					// don't consider interfaces local or IPv6
					if (n.isLoopback() || ip.isLoopbackAddress() || !(ip instanceof Inet4Address)) {
						continue loopIps ;
					}					
					
					try {
						if (!ip.isReachable(500))
							continue loopIps ;
						
					} catch (IOException e) {
						continue loopIps ;
					}
					
					// this interface contains at least a valid IP; let's keep it
					res.add(n.getDisplayName());
					
					// and stop investigating on this one
					break loopIps ;
				}
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} 
		return res;
	}
	
	/**
	 * Returns the list of interface names as an array of displayable names
	 * @return
	 */
	public static String[][] getArrayInterfacesNamesOnMachine() {
		
		List<String> l = listInterfacesNamesOnMachine();
		
		String[][] res = new String[l.size()+1][];
		res[0] = new String[] {"automatic","automatic"};
		for (int i=0; i<l.size(); i++) {
			res[i+1] = new String[] {"use interface "+l.get(i), l.get(i)};
		}
			
		return res;
	}
	

	@Override
	protected void createFieldEditors() {

		{
			BooleanFieldEditor startServerEditor = new BooleanFieldEditor(
					KEY_START_SERVER, 
					"start server", 
					getFieldEditorParent()
					);
			addField(startServerEditor);
		}
		{
			IntegerFieldEditor portServerEditor = new IntegerFieldEditor(
					KEY_START_SERVER_PORT, 
					"start on port", 
					getFieldEditorParent()
					);
			portServerEditor.setValidRange(1000, 65535);
			addField(portServerEditor);
		}
		{
			// list all the interfaces available on this machine
			
			ComboFieldEditor interfaceServerEditor = new ComboFieldEditor(
					KEY_START_SERVER_INTERFACE, 
					"force interface to use", 
					getArrayInterfacesNamesOnMachine(), 
					getFieldEditorParent()
					);
			addField(interfaceServerEditor);
		}
	}

}
