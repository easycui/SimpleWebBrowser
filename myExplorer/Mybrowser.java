package myExplorer;
/**
 * A very simple brower which can go back and go forward and can save bookmarks.
 * 
 * @author: Yuxin Cui
 * @version: 1.1
 */
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;

public class Mybrowser {
	static String htmlSource;
	static String url;
	static int selectedItem = 0;
	static TabFolder tabFolder;
	static Button back;
	static Button forward;
	static Urls[] urls;
	static int totalItem = 20;
	static Shell shell;
	static JFileChooser chooser1 = new JFileChooser();
	static ToolItem[] items;
	static int selectedToolItem=0;
	static Browser[] browsers ;
	static TabItem[] tabItems ;
	/*
	 * get the source code of current web page
	 */
	static void getHtmlSource(String url) {
		String linesep, htmlLine;
		linesep = System.getProperty("line.separator");
		htmlSource = "";
		try {
			java.net.URL source = new URL(url);
			InputStream in = new BufferedInputStream(source.openStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while ((htmlLine = br.readLine()) != null) {
				htmlSource = htmlSource+htmlLine + linesep;
			}

		} catch (java.net.MalformedURLException muex) {
		} catch (Exception e) {
		}

	}
	/*
	 * save the web Page to a file in the hard disk
	 */
	static void saveFile(final String url) {
		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] { "web page", "All Files (*.*)" });
		dialog.setFilterPath("D:\\");
		dialog.setFileName(".html");
		dialog.open();
		Thread thread = new Thread() {
			public void run() {
				try {
					java.net.URL source = new URL(url);
					InputStream in = new BufferedInputStream(source
							.openStream());//
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in));
					String fileName = dialog.getFilterPath() + "\\"
							+ dialog.getFileName();
					System.out.println(fileName);
					FileWriter out = new FileWriter(new File(fileName));
					BufferedWriter bw = new BufferedWriter(out);
					String line;
					while ((line = br.readLine()) != null) {
						bw.write(line);
						System.out.println(line);
						bw.newLine();
					}
					bw.flush();
					bw.close();
					out.close();
				} catch (java.net.MalformedURLException muex) {
					JOptionPane.showMessageDialog((Component) null, muex
							.toString(), "network by liujia",
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog((Component) null, ex
							.toString(), "network by liujia",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		};
		thread.start();
	}

	public static void main(String args[]) {

		Display display = new Display();
		shell = new Shell(display);
		shell.setText("CuiBrowser");
		shell.setSize(1024, 768);
		shell.setLocation(200, 0);
		final Text text = new Text(shell, SWT.BORDER);
		text.setBounds(485, 2, 455, 25);
		final BookMark mark=new BookMark();
		Button go = new Button(shell, SWT.BORDER);
		go.setBounds(946, 0, 59, 25);
		go.setText("Go");
		Label label = new Label(shell, SWT.LEFT);
		label.setText("Address:");
		label.setBounds(420, 5, 59, 25);
		
		/*
		 * implement the file menu
		 */
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("&File");
		Menu filemenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(filemenu);
		MenuItem savefile = new MenuItem(filemenu, SWT.CASCADE);
		/*
		 * response to the savefile menu
		 */
		savefile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getSelectionIndex();
				url = urls[i].getUrl();
				if (!url.equals("")) {
					saveFile(url);
				}

			}
		});
		savefile.setText("&Save");
		
		/*
		 * response to the exit menu
		 */
		MenuItem exit = new MenuItem(filemenu, SWT.CASCADE);
		exit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		exit.setText("&Exit");
		items=new ToolItem[20];
		
		/*
		 * implement the toolbar which  have bookmarks,
		 */
		final ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(5, 24, 993, 23);
		/*
		 * build a popup menu:popupMenu
		 */
	    final Menu popupMenu = new Menu(toolBar);
	    MenuItem openItem = new MenuItem(popupMenu, SWT.CASCADE);
	    openItem.setText("open");
	    MenuItem renameItem = new MenuItem(popupMenu, SWT.NONE);
	    renameItem.setText("rename");
	    MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
	    deleteItem.setText("delete");
	    toolBar.setMenu(popupMenu);
		final Listener listener = new Listener() {
		      public void handleEvent(Event event) {
		        ToolItem item = (ToolItem) event.widget;
		        String string = item.getText();
		        System.out.println("total:"+toolBar.getItemCount());
		        for(int i=0;i<toolBar.getItemCount();i++)
		        {
		        	System.out.println("i:"+i);
		        	System.out.println("name:"+items[i].getText());
			        if (string.equals(items[i].getText()))
			        {
			        	selectedToolItem=i;
			        	popupMenu.setVisible(true);
			        	break;
			        }
		        }
		    }
		};
		/*build the bookmarks
		 * 
		 */
		for(int i=0;i<mark.length();i++)
		{
			items[i]=new ToolItem(toolBar, SWT.NONE);
			items[i].setText(mark.getBookmarkName(i));
			items[i].addListener(SWT.Selection, listener);
		}
		/*
		 * deal with the menus on the popupMenu
		 */
		openItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				String input=mark.getBookmarkUrl(selectedToolItem);
				System.out.println(input);
				int i=tabFolder.getSelectionIndex();
				urls[i].Add(input);
				browsers[i].setUrl(input);
				tabItems[i].setText(input.substring(11, input.length() - 4));
				if (!urls[i].isBottom()) {
					back.setEnabled(true);
				}
				forward.setEnabled(false);
			}
			
		});
		renameItem.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				InputDialog dialog=new InputDialog(shell);
				String name=dialog.open();
				if(name!=null)
				{
					mark.rename(selectedToolItem, name);
					items[selectedToolItem].setText(name);
				}
			}
			
		});
		deleteItem.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				items[selectedToolItem].removeListener(SWT.Selection, listener);
				items[selectedToolItem].dispose();
				for(int i=selectedToolItem;i<toolBar.getItemCount()+1;i++)
				{
					if(i<toolBar.getItemCount())
						items[i]=items[i+1];
				}
				mark.deleteBookmark(selectedToolItem);
			}
			
		});
		// JTabbedPane tabbedPane= new JTabbedPane();
		/*
		 * build a tabFolder.Add tabs and browers to it. 
		 */
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(5, 53, 1003, 657);
		
		browsers = new Browser[totalItem];
		tabItems = new TabItem[totalItem];
		urls = new Urls[totalItem];

		tabItems[0] = new TabItem(tabFolder, SWT.NONE);
		tabItems[0].setText("New Tab");
		browsers[0] = new Browser(tabFolder, SWT.FILL);
		browsers[0].setBounds(5, 107, 1000, 620);
		tabItems[0].setControl(browsers[0]);
		urls[0] = new Urls();
		
		/*
		 * Implement the back and forward bottons
		 */
		back = new Button(shell, SWT.NONE);
		forward = new Button(shell, SWT.NONE);
		back.setEnabled(false);
		forward.setEnabled(false);
		/*
		 * deal with back botton event
		 */
		back.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getSelectionIndex();
				
				url = urls[i].Back();
				// System.out.println();
				browsers[i].setUrl(url);
				tabItems[i].setText(url.substring(11, url.length() - 4));
				text.setText(url);
				if (!forward.isEnabled()) {
					if (!urls[i].isTop()) {
						forward.setEnabled(true);
					}
				}
				if (urls[i].isBottom()) {
					back.setEnabled(false);
				}
			}
		});
		back.setBounds(5, 0, 37, 25);
		back.setText("<-");
		
		/*
		 * deal with forward botton event
		 */
		forward.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getSelectionIndex();
				url = urls[i].Forward();
				// System.out.println();
				browsers[i].setUrl(url);
				tabItems[i].setText(url.substring(11, url.length() - 4));
				text.setText(url);
				if (urls[i].isTop()) {
					forward.setEnabled(false);
				}
				if (!back.isEnabled()) {
					if (!urls[i].isBottom()) {
						back.setEnabled(true);
					}
				}
			}
		});
		forward.setBounds(48, 0, 37, 25);
		forward.setText("->");
		
		/*
		 * Implement refresh button
		 */
		Button refresh = new Button(shell, SWT.NONE);
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getSelectionIndex();
				browsers[i].refresh();
				tabItems[i].setText((browsers[i].getUrl()).substring(11,
						browsers[i].getUrl().length() - 4));
			}
		});
		refresh.setBounds(91, 0, 49, 25);
		refresh.setText("Refresh");
		
		/*
		 * Implement sourse botton.press it to see source code
		 */
		Button sourse = new Button(shell, SWT.NONE);
		sourse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getSelectionIndex();
				// System.out.println(browsers[i].getUrl());
				url = urls[i].getUrl();
				System.out.println(url);
				if (url.length() > 0 && !url.startsWith("http://")) {
					url = "http://" + url;
				}
				if (!url.equals("")) {
					getHtmlSource(url);
					ViewSourceFrame vsframe = new ViewSourceFrame(htmlSource);
					vsframe.setBounds(200, 200, 800, 500);
					vsframe.setVisible(true);
				} else {
				}
			}
		});
		sourse.setBounds(256, 0, 49, 25);
		sourse.setText("Sourse");
		
		/*
		 * Implement the newTab button,press it to open a new tab
		 */
		Button newTab = new Button(shell, SWT.NONE);
		newTab.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getItemCount();
				// System.out.println(i);
				tabItems[i] = new TabItem(tabFolder, SWT.NULL);
				tabItems[i].setText("new Tab");

				tabFolder.setSelection(i);
				browsers[i] = new Browser(tabFolder, SWT.FILL);
				browsers[i].setBounds(5, 107, 1000, 620);
				tabItems[i].setControl(browsers[i]);
				urls[i] = new Urls();
				back.setEnabled(false);
				forward.setEnabled(false);
				text.setText("");
				text.setFocus();
				selectedItem = i;
				// tabItems[i].dispose();
			}
		});
		newTab.setBounds(146, 0, 49, 25);
		newTab.setText("New");
		/*
		 * Implement the close button,press it to close current tab
		 */
		Button close = new Button(shell, SWT.NONE);
		close.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getSelectionIndex();
				// System.out.println(i);
				if (i == tabFolder.getItemCount()) {
					tabItems[i].dispose();
					browsers[i].dispose();
				} else {
					tabItems[i].dispose();
					browsers[i].dispose();
					for (int j = i; j < tabFolder.getItemCount(); j++) {
						if (j < tabFolder.getItemCount()) {
							browsers[j] = browsers[j + 1];
							tabItems[j] = tabItems[j + 1];
							urls[j] = urls[j + 1];
						}
					}
				}

			}
		});
		close.setBounds(201, 0, 49, 25);
		close.setText("Close");
		
		/*
		 * Implement the bookmark buuton, press it to add current tab to toolbar
		 */
		Button bookmark = new Button(shell, SWT.NONE);
		bookmark.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int i=tabFolder.getSelectionIndex();
				url=urls[i].getUrl();
				InputDialog dialog=new InputDialog(shell);
				String name=dialog.open();
				if(name!=null)
				{	
					mark.addBookmark(name, url);
					i=toolBar.getItemCount();
					items[i]=new ToolItem(toolBar,SWT.NONE);
					items[i].setText(name);	
					items[i].addListener(SWT.Selection, listener);
				}
			}
		});
		bookmark.setBounds(313, 0, 101, 25);
		bookmark.setText("Add to bookmark");
		
		/*
		 * open the web page
		 */
		go.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String input = text.getText().trim();
				if (input.length() == 0)
					return;
				if (!input.startsWith("http://")) {
					input = "http://" + input;
					text.setText(input);
				}
				int i = tabFolder.getSelectionIndex();
				// text.setText(Integer.toString(tabFolder.getSelectionIndex()));
				urls[i].Add(input);
				browsers[i].setUrl(input);
				tabItems[i].setText(input.substring(11, input.length() - 4));
				if (!urls[i].isBottom()) {
					back.setEnabled(true);
				}
				forward.setEnabled(false);
				// final Browser browser1=new Browser(tabFolder,SWT.FILL);
				// browser.setBounds(5,107,299,141);
			}
		});
		/*
		 * deal with the event of pressing "enter" key 
		 */
		text.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.keyCode == 13) {
					// System.out.println(e.keyCode);
					String input = text.getText().trim();
					if (input.length() == 0)
						return;
					if (!input.startsWith("http://")) {
						input = "http://" + input;
						text.setText(input);
					}
					int i = tabFolder.getSelectionIndex();
					// text.setText(Integer.toString(tabFolder.getSelectionIndex()));
					urls[i].Add(input);
					browsers[i].setUrl(input);
					if (input.length() > 12) {
						tabItems[i].setText(input.substring(11,
								input.length() - 4));
					} else {
						tabItems[i].setText("");
					}
					if (!urls[i].isBottom()) {
						back.setEnabled(true);
					}
					forward.setEnabled(false);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		/*
		 * deal with the event of change tab
		 */
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				int i = tabFolder.getSelectionIndex();
				System.out.println("i=" + i);
				System.out.println("Item=" + selectedItem);
				
				if (i != selectedItem) {
					back.setEnabled(false);
					if (urls[i].isBottom()) {
						System.out.println("back false");
						back.setEnabled(false);
						System.out.println(back.getEnabled());
					} else {
						System.out.println("back true");
						back.setEnabled(true);
					}
					if (urls[i].isTop()) {
						System.out.println("forward false");
						forward.setEnabled(false);
					} else {
						System.out.println("forward ture");
						back.setEnabled(true);
					}
					text.setText(urls[i].getUrl());
					selectedItem = i;
				}
			}
			//			
			// // TODO Auto-generated method stub

		});
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
