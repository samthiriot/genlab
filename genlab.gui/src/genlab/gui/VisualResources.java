package genlab.gui;

import genlab.core.commons.FileUtils;
import genlab.core.usermachineinteraction.GLLogger;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

/**
 * TODO see ISharedImages.
 * 
 * @author Samuel Thiriot
 *
 */
public class VisualResources {

	private static Shell swtShell;
	
	private static boolean isInitialized() {
		return swtShell != null;
		
	}
	public static void initVisualResource(Shell shell) {
		
		if (isInitialized())
			return;
		if (shell == null)
			return;
		
		swtShell = shell;

		// compute the height of the default font
		Shell shellTest = new Shell(shell.getDisplay());
	    Text text = new Text(shellTest, SWT.NONE);
	    GC gc = new GC(text); 
	    FontMetrics fm = gc.getFontMetrics();
	    int charheight = fm.getHeight();
	    gc.dispose();
	    shellTest.dispose();
	    IMAGE_ICON_MAX_HEIGHT = charheight;
	    
	}

	public static final Color COLOR_GRAY = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	public static final Color COLOR_RED = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);

	public static final Image ICON_DEBUG = Activator.getImageDescriptor("/icons/message_information.gif").createImage();
	public static final Image ICON_INFO = Activator.getImageDescriptor("/icons/message_information.gif").createImage();
	public static final Image ICON_WARNING = Activator.getImageDescriptor("/icons/message_warning.gif").createImage();
	public static final Image ICON_TIP = Activator.getImageDescriptor("/icons/message_tip.gif").createImage();
	public static final Image ICON_ERROR = Activator.getImageDescriptor("/icons/message_error.gif").createImage();
	
	public static Image getImage(String resourcePath) {
		/*
		 * TODO use this insteadu ???
		 * 
		 Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		    URL url = FileLocator.find(bundle, new Path("icons/alt_window_32.gif"), null);
		    ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		    image = imageDescriptor.createImage();
		
		 */
		return Activator.getImageDescriptor(resourcePath).createImage();
	}

	public static int IMAGE_ICON_MAX_WIDTH = 50;
	public static int IMAGE_ICON_MAX_HEIGHT = 10;
	
	/**
	 * Converts an AWT image to a SWT one, by respecting transparency
	 * Retrieved from http://stackoverflow.com/questions/975919/how-to-display-system-icon-for-a-file-in-swt 
	 * @param bufferedImage
	 * @return
	 */
	public static ImageData convertToSWT(BufferedImage bufferedImage) {
	    if (bufferedImage.getColorModel() instanceof DirectColorModel) {
	        DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
	        PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
	        ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
	        for (int y = 0; y < data.height; y++) {
	                for (int x = 0; x < data.width; x++) {
	                        int rgb = bufferedImage.getRGB(x, y);
	                        int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF)); 
	                        data.setPixel(x, y, pixel);
	                        if (colorModel.hasAlpha()) {
	                                data.setAlpha(x, y, (rgb >> 24) & 0xFF);
	                        }
	                }
	        }
	        return data;            
	    } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
	        IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
	        int size = colorModel.getMapSize();
	        byte[] reds = new byte[size];
	        byte[] greens = new byte[size];
	        byte[] blues = new byte[size];
	        colorModel.getReds(reds);
	        colorModel.getGreens(greens);
	        colorModel.getBlues(blues);
	        RGB[] rgbs = new RGB[size];
	        for (int i = 0; i < rgbs.length; i++) {
	                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
	        }
	        PaletteData palette = new PaletteData(rgbs);
	        ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
	        data.transparentPixel = colorModel.getTransparentPixel();
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[1];
	        for (int y = 0; y < data.height; y++) {
	                for (int x = 0; x < data.width; x++) {
	                        raster.getPixel(x, y, pixelArray);
	                        data.setPixel(x, y, pixelArray[0]);
	                }
	        }
	        return data;
	    }
	    return null;
	}
	
	/**
	 * Takes a swing Icon and returns a SWT image
	 * @param icon
	 * @return
	 */
	public static Image convertFromIcon(Icon icon) {
		
		if (icon instanceof ImageIcon) {
			ImageIcon systemIcon = (ImageIcon)icon;
			java.awt.Image image = systemIcon.getImage();
			int width = image.getWidth(null);
		    int height = image.getHeight(null);
		    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2d = bufferedImage.createGraphics();
		    g2d.drawImage(image, 0, 0, null);
		    g2d.dispose();
		    return new Image(swtShell.getDisplay(), convertToSWT(bufferedImage));
		} else {
			  int w = icon.getIconWidth();
              int h = icon.getIconHeight();
              GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
              GraphicsDevice gd = ge.getDefaultScreenDevice();
              GraphicsConfiguration gc = gd.getDefaultConfiguration();
              BufferedImage image = gc.createCompatibleImage(w, h);
              Graphics2D g = image.createGraphics();
              icon.paintIcon(null, g, 0, 0);
              g.dispose();
              return new Image(swtShell.getDisplay(), convertToSWT(image));
		}
		
	}
	
	/**
	 * Retrieves the icon for a given file using the AWT tools 
	 * @param file
	 * @return
	 */
	private static Image searchImageForFileUsingAWT(File file) {
		
		try {
		    return convertFromIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
		} catch (RuntimeException e) {
			return null;
		} 
	}


	private static Image searchImageResizeIfNecessary(Image imgNative) {
		
		final int width = imgNative.getBounds().width;
	    final int height = imgNative.getBounds().height;
	    if ((height <= IMAGE_ICON_MAX_HEIGHT) && (width <= IMAGE_ICON_MAX_WIDTH))
	    	return imgNative;
	    
	    double scale = Math.min(
	    		(double)IMAGE_ICON_MAX_WIDTH/(double)width, 
	    		(double)IMAGE_ICON_MAX_HEIGHT/(double)height
	    		);
	    
	    final Image scaled = new Image(
	    		swtShell.getDisplay(),
	            imgNative.getImageData().scaledTo(
	            		(int)(width*scale),
	            		(int)(height*scale)
	            		)
	            );
	    
	    return scaled;
	}
	
	/**
	 * Searches an image from the program name.
	 * Not really so efficient, as the program name
	 * may change depending on the locale lang...
	 * @param txt
	 * @return
	 */
	private static Image searchImageByProgramName(String txt) {
		try {
			
			
			ImageData iconData = null;
			for (Program p : Program.getPrograms()) {
			  if (txt.equalsIgnoreCase(p.getName())) {
				  iconData = p.getImageData();
				  break;
			  }
			}
			if (iconData==null)
				return null;
			
			Image imgNative = new Image(swtShell.getDisplay(), iconData);
			return searchImageResizeIfNecessary(imgNative);
			
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Searches an image by searching through SWT the corresponding 
	 * program, and retrieving its icon
	 * @param txt
	 * @return
	 */
	private static Image searchImageByProgram(String txt) {
		try {
			
			ImageData iconData=Program.findProgram(txt).getImageData();
			Image imgNative = new Image(swtShell.getDisplay(), iconData);
			return searchImageResizeIfNecessary(imgNative);

		} catch (RuntimeException e) {
			return null;
		}
	}
	
	private static Map<String,Image> extension2image = new HashMap<String,Image>();
	
	protected static Image searchDefaultImageForFile(String txt) {
		
		// retrieve from cache
		if (extension2image.containsKey(txt)) {
			 return extension2image.get(txt);
		}
		
		Image img = null;
		
		// or, search by extension
		if (img == null) {
			img = searchImageByProgramName(txt);
		}
		
		if (img == null) {
			img = searchImageByProgram(txt);
		}
		extension2image.put(txt,img);
		
		return img;
	}

	public static Image getDefaultImageForFile(String txt) {
		
		// first search with just the extension
		final String extension = FileUtils.getExtension(txt);
		if (extension.length() < 3) {
			// too short
			return null;
		}
		
		Image img = searchDefaultImageForFile(extension);
		
		return img;
			
		/* TODO never works ???
		img = searchDefaultImageForFile(txt);
		
		if (img != null) {
			System.err.println("found with full text");
			return img;
		}
			
		*/
		//System.err.println("not found :-(");
		//return null;
		
	}
	
	/**
	 * Retrieves and resizes a system icon (image).
	 * @param swtCode
	 * @return
	 */
	private static Image searchImageAsSystemImage(int swtCode) {
		try {
			Image imageNative = swtShell.getDisplay().getSystemImage(swtCode);
			return searchImageResizeIfNecessary(imageNative);
		} catch (RuntimeException e) {
			// TODO System.err.println("unable to load image icon "+swtCode);
			return null;
		}		
	}

	private static Image imageDirectory = null;

	public static Image getImageDirectory() {

		// first attempt using the best solution (native icons)
		if (imageDirectory == null) {
			imageDirectory = searchImageByProgramName("Folder");
		}
		if (imageDirectory == null) {	// for french
			imageDirectory = searchImageByProgramName("Ouvrir le dossier");
		}
		if (imageDirectory == null) {	// another french option...
			imageDirectory = searchImageByProgramName("Dossier");
		}
		
		// second attempt using a fallback solution (AWT ugly icon !)
		if (imageDirectory == null) {
			imageDirectory = searchImageForFileUsingAWT(new File(System.getProperty("user.home")));
		}
			
		return imageDirectory;
		
	}

	private static Image imageHtml = null;

	public static Image getImageHtml() {
			
		if (imageHtml == null) 
			imageHtml = searchImageByProgram(".html");
		
		return imageHtml;
		
	}

	private static Image imageJpg = null;

	public static Image getImageJpg() {
		
		if (imageJpg == null) 
			imageJpg = searchImageByProgram(".jpg");
		
		return imageJpg;
	}

	private static Image imageZip = null;

	public static Image getImageZip() {
		
		if (imageZip == null) 
			imageZip = searchImageByProgram(".zip");
		
		return imageZip;
	}
	

	public static void proposeOpenFile(final String filename, final Shell shell, final String msg) {
		
		MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		mb.setText("open the file ?");
		mb.setMessage(
				msg 
				+ "\n" 
				+ "Do you want to open this file ?"
				);
		if (mb.open() == SWT.YES) {
			openFile(filename);
		}
		
	}
	
	protected static Map<String,Image> cachedImages = new HashMap<String, Image>(100);
	
	public static Image getCachedImageForFile(String imagePath, Display display) {
		
		synchronized (cachedImages) {

			Image img = cachedImages.get(imagePath);
			
			if (img == null) {
				try {
					img = new Image(display, imagePath);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				if (img == null)
					return null;
				cachedImages.put(imagePath, img);
			}

			return img;
		}
	}
	
	public static void clearCachedImages() {
		synchronized (cachedImages) {
			for (Image i : cachedImages.values()) {
				i.dispose();
			}
			cachedImages.clear();
		}
		
	}

	public static void clearCachedResources() {
		clearCachedImages();
	}
	
	public static void openFile(final File file) {

		if (file == null)
			return;
		
		try {
			
			// will become true once we will succeed.
			boolean openedFile = false;
			
			// TODO maybe first open with default editors ?
			{
				
				IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
				if (editor != null) {
					System.err.println("default editor:"+editor);
					IFileStore fileStore = EFS.getLocalFileSystem().getStore( new Path(file.getAbsolutePath()));
					IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

					try {
						page.openEditor(new FileStoreEditorInput(fileStore), editor.getId());
						return;

					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
				
			}	
			
			// first attempt using the SWT Program feature
			// nota: cannot open a directory using SWT
			if (!openedFile){ 
				openedFile = Program.launch(file.getAbsolutePath());
			}
			
			// second attempt using the Desktop features 
			if (!openedFile) {
				System.err.println("attempting to open using awt desktop");

				Desktop desktop = null;
		        if (Desktop.isDesktopSupported()) {
			          desktop = Desktop.getDesktop();
		        } else {
		        	System.err.println("No desktop in this environment; unable to open the file");
		        	return;
		        }
				try {
					desktop.open(file);
					openedFile = true;
				} catch (IOException ioe) {
					System.err.println("Exception while attempting to open a file");
				}
				if (!openedFile) {
					try {
						desktop.edit(file);
						openedFile = true;
					} catch (IOException ioe) {
						System.err.println("Exception while attempting to open a file");
					}
				}
			}
			
			// second attempt using program
		} finally {
		}
		
	}
	
	public static void openFile(final String filename) {
		openFile(new File(filename));			
	}

	
	public static void openUrl(final String url) {
		
		boolean openedUrl = false;
		
		openedUrl = Program.launch(url);

		// second attempt using the Desktop features 
		if (!openedUrl) {
			GLLogger.traceTech("attempting to open the URL using AWT Desktop", VisualResources.class);
			
			Desktop desktop = null;
	        if (Desktop.isDesktopSupported()) {
		          desktop = Desktop.getDesktop();
	        } else {
				GLLogger.traceTech("no AWT Desktop", VisualResources.class);
	        }
			try {
				desktop.browse(new URI(url));
				openedUrl = true;
			} catch (IOException ioe) {
				GLLogger.traceTech("exception while opening the URL using AWT Desktop", VisualResources.class, ioe);
			} catch (URISyntaxException e) {
				GLLogger.traceTech("exception while opening the URL using AWT Desktop", VisualResources.class, e);
			}
		
		}
	}

	
}
