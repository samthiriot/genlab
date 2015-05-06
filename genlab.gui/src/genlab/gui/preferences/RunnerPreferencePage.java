package genlab.gui.preferences;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import genlab.gui.Activator;

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
public class RunnerPreferencePage 
				extends FieldEditorPreferencePage 
				implements IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.genlab.preferences.pages.localrunner";
	
	public static final String KEY_MAX_CPUS = PAGE_ID+".max_cpus";
	public static final String KEY_START_SERVER = PAGE_ID+".start_server";
	public static final String KEY_START_SERVER_PORT = PAGE_ID+".start_server_port";
	public static final String KEY_START_SERVER_INTERFACE = PAGE_ID+".start_server_interface";

	public static final String KEY_SERVERS = PAGE_ID+".servers";

	
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
	
	public static String[][] getArrayInterfacesNamesOnMachine() {
		
		List<String> l = listInterfacesNamesOnMachine();
		
		String[][] res = new String[l.size()+1][];
		res[0] = new String[] {"automatic","automatic"};
		for (int i=0; i<l.size(); i++) {
			res[i+1] = new String[] {"use interface "+l.get(i), l.get(i)};
		}
			
		return res;
	}
	
	public RunnerPreferencePage() {
	}


	@Override
	public void init(IWorkbench workbench) {
		
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Settings for the local runner, that is the process in charge of managing the execution of workflows on your local computer.");

	}

	@Override
	protected void createFieldEditors() {
		
		{
			IntegerFieldEditor cpusEditor = new IntegerFieldEditor(
					KEY_MAX_CPUS,
					"Max &CPUs to use", 
					getFieldEditorParent()
					);
			
			cpusEditor.setValidRange(1, Runtime.getRuntime().availableProcessors()*4);
			addField(cpusEditor);
		}
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
		{
			HostsListFieldEditor list = new HostsListFieldEditor(
					KEY_SERVERS, 
					"connect to these GenLab servers to delegate computations", 
					getFieldEditorParent()
					);
			addField(list);
		}
	}

	// TODO  performOk, performApply, performDefaults, performCancel
}
