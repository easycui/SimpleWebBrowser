package myExplorer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import java.io.*;
import java.util.*;

class ViewSourceFrame extends JFrame implements ActionListener {
	JPanel contentPane;
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	Border border1;
	JButton closebutton = new JButton();
	JButton savebutton = new JButton();
	JScrollPane jScrollPanel = new JScrollPane();
	TextAreaMenu jTextArea1 = new TextAreaMenu();

	String htmlSource;

	public ViewSourceFrame(String htmlSource) {
		this.htmlSource = htmlSource;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setSize(new Dimension(600, 500));
		setTitle("source");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		panel2.setLayout(new FlowLayout());
		savebutton.setText("save");
		closebutton.setText("quit");
		closebutton.addActionListener(this);
		savebutton.addActionListener(this);

		jScrollPanel.getViewport().add(jTextArea1, null);
		border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
		panel1.setLayout(new BorderLayout());
		panel1.setBorder(border1);
		panel1.add(jScrollPanel, BorderLayout.CENTER);
		contentPane.add(panel1, BorderLayout.CENTER);
		panel2.add(savebutton);
		panel2.add(closebutton);

		contentPane.add(panel2, BorderLayout.SOUTH);
		this.jTextArea1.setEditable(true);
		this.jTextArea1.setText(this.htmlSource);
		this.jTextArea1.setCaretPosition(0);
	}

	class TextAreaMenu extends JTextArea implements MouseListener {

		private static final long serialVersionUID = -2308615404205560110L;

		private JPopupMenu pop = null; // pop menu

		private JMenuItem copy = null, paste = null, cut = null;

		public TextAreaMenu() {
			super();
			init();
		}

		private void init() {
			this.addMouseListener(this);
			pop = new JPopupMenu();
			pop.add(copy = new JMenuItem("COPY"));
			pop.add(paste = new JMenuItem("PASTE"));
			pop.add(cut = new JMenuItem("CUT"));
			copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
			paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
			cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
			copy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					action(e);
				}
			});
			paste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					action(e);
				}
			});
			cut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					action(e);
				}
			});
			this.add(pop);
		}

		public void action(ActionEvent e) {
			String str = e.getActionCommand();
			if (str.equals(copy.getText())) { // copy
				this.copy();
			} else if (str.equals(paste.getText())) { // paste
				this.paste();
			} else if (str.equals(cut.getText())) { // cut
				this.cut();
			}
		}

		public JPopupMenu getPop() {
			return pop;
		}

		public void setPop(JPopupMenu pop) {
			this.pop = pop;
		}

		/**
		 * check whether clipboard has data
		 * 
		 * @return true or false
		 */
		public boolean isClipboardString() {
			boolean b = false;
			Clipboard clipboard = this.getToolkit().getSystemClipboard();
			Transferable content = clipboard.getContents(this);
			try {
				if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
					b = true;
				}
			} catch (Exception e) {
			}
			return b;
		}

		public boolean isCanCopy() {
			boolean b = false;
			int start = this.getSelectionStart();
			int end = this.getSelectionEnd();
			if (start != end)
				b = true;
			return b;
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				copy.setEnabled(isCanCopy());
				paste.setEnabled(isClipboardString());
				cut.setEnabled(isCanCopy());
				pop.show(this, e.getX(), e.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

	public void actionPerformed(ActionEvent e) {
		String url = "";
		if (e.getSource() == closebutton) {
			dispose();
		} else if (e.getSource() == savebutton) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(ViewSourceFrame.this);
			File saveFile = fc.getSelectedFile();
			try {
				FileWriter writeOut = new FileWriter(saveFile);
				writeOut.write(jTextArea1.getText());
				writeOut.close();
			} catch (IOException ex) {
				System.out.println("save faild");
			}
		}
	}
}