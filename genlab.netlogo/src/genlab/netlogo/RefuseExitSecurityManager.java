package genlab.netlogo;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

import org.eclipse.swt.widgets.Shell;

/**
 * This security manager forbits external apps to force exit of genlab.
 * It is notably of use for netlogo which tends to close the whole app when clicking the small
 * cross to just close the window. Out of its domain of expertise, this security manager
 * just delegates to the previous installed security manager.
 * 
 * @author Samuel Thiriot
 *
 */
public class RefuseExitSecurityManager extends SecurityManager {

	private final SecurityManager sm;
	private final Shell shell;
	public RefuseExitSecurityManager(Shell shell) {
		this.sm = System.getSecurityManager();
		this.shell= shell;
	}

	 public boolean isExitAllowed(){
		 
		 return shell.isDisposed();
	 }
	 
	 @Override
	  public void checkExit(int status) {
		 if (!isExitAllowed())
			 throw new SecurityException();
	  }

	public void checkAccept(String host, int port) {
		sm.checkAccept(host, port);
	}

	public void checkAccess(Thread t) {
		sm.checkAccess(t);
	}

	public void checkAccess(ThreadGroup g) {
		sm.checkAccess(g);
	}

	public void checkAwtEventQueueAccess() {
		sm.checkAwtEventQueueAccess();
	}

	public void checkConnect(String host, int port, Object context) {
		sm.checkConnect(host, port, context);
	}

	public void checkConnect(String host, int port) {
		sm.checkConnect(host, port);
	}

	public void checkCreateClassLoader() {
		sm.checkCreateClassLoader();
	}

	public void checkDelete(String file) {
		sm.checkDelete(file);
	}

	public void checkExec(String cmd) {
		sm.checkExec(cmd);
	}

	public void checkLink(String lib) {
		sm.checkLink(lib);
	}

	public void checkListen(int port) {
		sm.checkListen(port);
	}

	public void checkMemberAccess(Class<?> clazz, int which) {
		sm.checkMemberAccess(clazz, which);
	}

	public void checkMulticast(InetAddress maddr, byte ttl) {
		sm.checkMulticast(maddr, ttl);
	}

	public void checkMulticast(InetAddress maddr) {
		sm.checkMulticast(maddr);
	}

	public void checkPackageAccess(String pkg) {
		sm.checkPackageAccess(pkg);
	}

	public void checkPackageDefinition(String pkg) {
		sm.checkPackageDefinition(pkg);
	}

	public void checkPermission(Permission perm, Object context) {
		sm.checkPermission(perm, context);
	}

	public void checkPermission(Permission perm) {
		sm.checkPermission(perm);
	}

	public void checkPrintJobAccess() {
		sm.checkPrintJobAccess();
	}

	public void checkPropertiesAccess() {
		sm.checkPropertiesAccess();
	}

	public void checkPropertyAccess(String key) {
		sm.checkPropertyAccess(key);
	}

	public void checkRead(FileDescriptor fd) {
		sm.checkRead(fd);
	}

	public void checkRead(String file, Object context) {
		sm.checkRead(file, context);
	}

	public void checkRead(String file) {
		sm.checkRead(file);
	}

	public void checkSecurityAccess(String target) {
		sm.checkSecurityAccess(target);
	}

	public void checkSetFactory() {
		sm.checkSetFactory();
	}

	public void checkSystemClipboardAccess() {
		sm.checkSystemClipboardAccess();
	}

	public boolean checkTopLevelWindow(Object window) {
		return sm.checkTopLevelWindow(window);
	}

	public void checkWrite(FileDescriptor fd) {
		sm.checkWrite(fd);
	}

	public void checkWrite(String file) {
		sm.checkWrite(file);
	}

	public boolean equals(Object obj) {
		return sm.equals(obj);
	}

	public boolean getInCheck() {
		return sm.getInCheck();
	}

	public Object getSecurityContext() {
		return sm.getSecurityContext();
	}

	public ThreadGroup getThreadGroup() {
		return sm.getThreadGroup();
	}

	public int hashCode() {
		return sm.hashCode();
	}

	public String toString() {
		return sm.toString();
	}


	 
	 
}
